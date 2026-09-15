package com.example.data.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.math.SmartProblemEngine
import com.example.data.model.ProblemDoubt
import com.example.data.model.SolutionStep
import com.example.data.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiSolver(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun solveDoubt(
        questionText: String,
        subject: Subject,
        imageUri: Uri? = null,
        fileName: String? = null
    ): ProblemDoubt = withContext(Dispatchers.IO) {
        // Retrieve API key from BuildConfig or custom user settings
        val prefs = context.getSharedPreferences("tryourself_settings", Context.MODE_PRIVATE)
        val customKey = prefs.getString("custom_gemini_api_key", "") ?: ""

        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val apiKey = customKey.ifBlank { buildKey }.trim()

        val hasValidKey = apiKey.isNotBlank() &&
                !apiKey.equals("MY_GEMINI_API_KEY", ignoreCase = true) &&
                !apiKey.contains("TODO", ignoreCase = true)

        if (hasValidKey) {
            try {
                val aiResult = callGeminiApiWithFallbackModels(apiKey, questionText, subject, imageUri)
                if (aiResult != null) {
                    return@withContext aiResult.copy(
                        imageUri = imageUri?.toString(),
                        fileName = fileName
                    )
                }
            } catch (e: Exception) {
                Log.e("GeminiSolver", "Gemini API call failed, falling back to smart solver", e)
            }
        }

        // High-precision deterministic SmartProblemEngine for guaranteed accurate solutions
        return@withContext SmartProblemEngine.solve(
            questionText = questionText,
            subject = subject,
            imageUri = imageUri?.toString(),
            fileName = fileName
        )
    }

    private fun callGeminiApiWithFallbackModels(
        apiKey: String,
        questionText: String,
        subject: Subject,
        imageUri: Uri?
    ): ProblemDoubt? {
        val candidateModels = listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-flash-latest")

        for (model in candidateModels) {
            try {
                val result = executeGeminiRequest(model, apiKey, questionText, subject, imageUri)
                if (result != null) {
                    return result
                }
            } catch (e: Exception) {
                Log.w("GeminiSolver", "Model $model failed, trying next candidate: ${e.message}")
            }
        }
        return null
    }

    private fun executeGeminiRequest(
        modelName: String,
        apiKey: String,
        questionText: String,
        subject: Subject,
        imageUri: Uri?
    ): ProblemDoubt? {
        val prompt = buildPrompt(questionText, subject)
        val jsonPayload = JSONObject()
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()

        // Text prompt part
        val textPart = JSONObject().put("text", prompt)
        partsArray.put(textPart)

        // Optional image inlineData part
        if (imageUri != null) {
            val base64Image = readImageAsBase64(imageUri)
            if (base64Image != null) {
                val inlineData = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", base64Image)
                val imagePart = JSONObject().put("inlineData", inlineData)
                partsArray.put(imagePart)
            }
        }

        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        jsonPayload.put("contents", contentsArray)

        // System instructions enforcing 100% accuracy and structured pedagogical steps for ANY question
        val systemInstruction = JSONObject()
        val systemParts = JSONArray().put(
            JSONObject().put(
                "text",
                """
                You are TRyOURSELF, an elite, hyper-accurate STEM and homework tutor for students.
                YOUR TOP PRIORITY IS 100% FACTUAL AND MATHEMATICAL ACCURACY.
                You can answer ANY question the student asks: whether mathematics (arithmetic, algebra, geometry, trigonometry, calculus, word problems, statistics), physics, chemistry, biology, earth science, or any general study question.
                
                PEDAGOGICAL & ACCURACY RULES:
                1. Solve the exact numbers and exact question asked with complete mathematical and scientific precision. Double-check all arithmetic, algebra, unit conversions, and calculations.
                2. Break down the solution strictly into ordered sequential steps (typically 3 to 5 steps).
                3. Step 1: List the Given Information, identify what we need to solve for, and state any known constants.
                4. FORMULA DIRECTIVE: If a step relies on a specific formula or governing equation (e.g. v = u + at, PV = nRT, a² + b² = c², F = m · a, D = b² - 4ac, etc.), you MUST provide that clean mathematical formula in the "formula" field and its descriptive name in "formulaName".
                   IMPORTANT: DO NOT put square brackets in the JSON "formula" string itself; the client application will automatically wrap it in [ formula ] to test and encourage the student to think before revealing the step.
                5. If a step does NOT require a formula (e.g. listing given data, simplifying arithmetic, or final conclusion), set "formula" and "formulaName" to null.
                6. Ensure the "calculation" field shows the exact numerical steps with proper units.
                7. Return valid JSON only, without markdown wrapping or backticks, matching this exact schema:
                {
                  "subject": "MATHS" or "PHYSICS" or "CHEMISTRY",
                  "question": "rephrased or extracted question",
                  "givenValues": ["given 1", "given 2"],
                  "steps": [
                    {
                      "stepNumber": 1,
                      "title": "Short title of step",
                      "formula": "clean formula string or null",
                      "formulaName": "formula name or null",
                      "explanation": "Clear, student-friendly explanation",
                      "calculation": "Exact calculations or working details"
                    }
                  ],
                  "finalAnswer": "Accurate, concise final result with proper units"
                }
                """.trimIndent()
            )
        )
        systemInstruction.put("parts", systemParts)
        jsonPayload.put("systemInstruction", systemInstruction)

        val generationConfig = JSONObject()
            .put("temperature", 0.1) // Low temperature for deterministic, hyper-accurate mathematics
            .put("responseMimeType", "application/json")
        jsonPayload.put("generationConfig", generationConfig)

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return null

        val responseJson = JSONObject(responseBody)
        val candidates = responseJson.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val candidate = candidates.getJSONObject(0)
        val content = candidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null
        val text = parts.getJSONObject(0).optString("text") ?: return null

        return parseGeminiResponse(text, subject, questionText)
    }

    private fun parseGeminiResponse(rawJson: String, defaultSubject: Subject, originalPrompt: String): ProblemDoubt? {
        return try {
            val cleaned = rawJson.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val obj = JSONObject(cleaned)

            val subjStr = obj.optString("subject", defaultSubject.name)
            val detectedSubject = try {
                Subject.valueOf(subjStr.uppercase())
            } catch (e: Exception) {
                defaultSubject
            }

            val question = obj.optString("question", originalPrompt.ifBlank { "Solve this homework problem" })
            val givenList = mutableListOf<String>()
            val givenArr = obj.optJSONArray("givenValues")
            if (givenArr != null) {
                for (i in 0 until givenArr.length()) {
                    givenList.add(givenArr.getString(i))
                }
            }

            val stepsList = mutableListOf<SolutionStep>()
            val stepsArr = obj.optJSONArray("steps")
            if (stepsArr != null) {
                for (i in 0 until stepsArr.length()) {
                    val stepObj = stepsArr.getJSONObject(i)
                    val formulaVal = if (stepObj.isNull("formula")) null else stepObj.optString("formula").takeIf { it.isNotBlank() }
                    val formulaNameVal = if (stepObj.isNull("formulaName")) null else stepObj.optString("formulaName").takeIf { it.isNotBlank() }
                    stepsList.add(
                        SolutionStep(
                            stepNumber = stepObj.optInt("stepNumber", i + 1),
                            title = stepObj.optString("title", "Step ${i + 1}"),
                            formula = formulaVal,
                            formulaName = formulaNameVal,
                            explanation = stepObj.optString("explanation", ""),
                            calculation = stepObj.optString("calculation", "")
                        )
                    )
                }
            }

            val finalAnswer = obj.optString("finalAnswer", "Solution complete.")

            ProblemDoubt(
                id = UUID.randomUUID().toString(),
                subject = detectedSubject,
                question = question,
                givenValues = givenList,
                steps = stepsList,
                finalAnswer = finalAnswer,
                isSample = false
            )
        } catch (e: Exception) {
            Log.e("GeminiSolver", "Error parsing response JSON: $rawJson", e)
            null
        }
    }

    private fun buildPrompt(question: String, subject: Subject): String {
        return "Student doubt in ${subject.displayName}: ${question.ifBlank { "Please solve the problem in the attached photo/file step-by-step." }}"
    }

    private fun readImageAsBase64(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream) ?: return null
                // Resize if oversized
                val maxDim = 1024
                val scale = if (bitmap.width > maxDim || bitmap.height > maxDim) {
                    val ratio = maxDim.toFloat() / maxOf(bitmap.width, bitmap.height)
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * ratio).toInt(),
                        (bitmap.height * ratio).toInt(),
                        true
                    )
                } else {
                    bitmap
                }
                val outputStream = ByteArrayOutputStream()
                scale.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            Log.e("GeminiSolver", "Failed to read image as base64", e)
            null
        }
    }
}
