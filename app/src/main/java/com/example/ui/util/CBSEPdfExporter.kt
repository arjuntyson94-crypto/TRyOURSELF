package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.CBSENoteChapter
import com.example.data.model.StudentNotebookEntry
import java.io.File
import java.io.FileOutputStream

object CBSEPdfExporter {

    fun generateChapterPdfText(chapter: CBSENoteChapter): String {
        val sb = StringBuilder()
        sb.append("================================================================================\n")
        sb.append("      CENTRAL BOARD OF SECONDARY EDUCATION (CBSE) - NCERT STUDY NOTES           \n")
        sb.append("                       ACADEMIC CURRICULUM REVISION                             \n")
        sb.append("================================================================================\n\n")
        sb.append("CLASS: ${chapter.classLevel.displayName} (${chapter.classLevel.roman})\n")
        sb.append("SUBJECT: ${chapter.subject.displayName.uppercase()}\n")
        sb.append("CHAPTER ${chapter.chapterNumber}: ${chapter.title.uppercase()}\n")
        sb.append("SYLLABUS REFERENCE: ${chapter.ncertReference}\n")
        sb.append("BOARD EXAM WEIGHTAGE: ${chapter.weightageMarks}\n")
        sb.append("--------------------------------------------------------------------------------\n\n")

        sb.append("I. CHAPTER OVERVIEW & SYNOPSIS:\n")
        sb.append(chapter.summary)
        sb.append("\n\n")

        sb.append("II. CORE CONCEPTS & NCERT DEFINITIONS:\n")
        chapter.keyPoints.forEachIndexed { idx, point ->
            sb.append("${idx + 1}. $point\n")
        }
        sb.append("\n")

        sb.append("III. OFFICIAL FORMULAS & GOVERNING RELATIONS (TRyOURSELF BRACKET FORMAT):\n")
        chapter.formulas.forEachIndexed { idx, f ->
            sb.append("[$idx+1] ${f.name}\n")
            sb.append("    FORMULA: ${f.formulaText}\n")
            sb.append("    APPLICATIONS: ${f.description}\n")
            if (f.units != null) sb.append("    UNITS: ${f.units}\n")
            sb.append("\n")
        }

        sb.append("IV. KEY DERIVATIONS & REACTION MECHANISMS:\n")
        chapter.derivationsOrReactions.forEachIndexed { idx, der ->
            sb.append("Derivation #${idx + 1}:\n")
            sb.append(der)
            sb.append("\n\n")
        }

        sb.append("V. FREQUENTLY REPEATED CBSE BOARD EXAM QUESTIONS:\n")
        chapter.boardQuestions.forEachIndexed { idx, q ->
            sb.append("Q${idx + 1} [${q.yearRepeated}]:\n")
            sb.append("   ${q.question}\n")
            sb.append("   Model Solution Summary: ${q.answerSummary}\n\n")
        }

        sb.append("================================================================================\n")
        sb.append("  Prepared with TRyOURSELF - Interactive STEM & CBSE Self-Study Platform       \n")
        sb.append("================================================================================\n")
        return sb.toString()
    }

    fun generateNotebookPdfText(entry: StudentNotebookEntry): String {
        val sb = StringBuilder()
        sb.append("================================================================================\n")
        sb.append("                  CBSE STUDENT REVISION NOTEBOOK - TRyOURSELF                  \n")
        sb.append("================================================================================\n\n")
        sb.append("CLASS: ${entry.classLevel.displayName}\n")
        sb.append("SUBJECT: ${entry.subject.displayName}\n")
        sb.append("CHAPTER: ${entry.chapterTitle}\n")
        sb.append("NOTE TITLE: ${entry.title}\n")
        sb.append("DATE: ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(entry.timestamp))}\n")
        sb.append("--------------------------------------------------------------------------------\n\n")

        sb.append("STUDENT REVISION NOTES:\n")
        sb.append(entry.content)
        sb.append("\n\n")

        if (entry.formulasJotted.isNotEmpty()) {
            sb.append("FORMULAS SAVED IN THIS NOTE:\n")
            entry.formulasJotted.forEachIndexed { index, formula ->
                sb.append("${index + 1}. $formula\n")
            }
            sb.append("\n")
        }

        sb.append("================================================================================\n")
        return sb.toString()
    }

    /**
     * Shares the CBSE notes as a downloadable / printable document (.txt / .pdf-compatible text).
     */
    fun shareOrDownloadPdf(context: Context, fileName: String, documentText: String) {
        try {
            val docsDir = File(context.cacheDir, "cbse_notes")
            if (!docsDir.exists()) docsDir.mkdirs()

            val sanitizedName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
            val file = File(docsDir, "$sanitizedName.txt")
            FileOutputStream(file).use { it.write(documentText.toByteArray()) }

            val fileUri: Uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "CBSE Notes: $fileName")
                putExtra(Intent.EXTRA_TEXT, documentText)
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Download or Share CBSE Notes PDF"))
            Toast.makeText(context, "Ready to download/print: $sanitizedName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback to text copy or direct text share
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "CBSE Notes: $fileName")
                putExtra(Intent.EXTRA_TEXT, documentText)
            }
            context.startActivity(Intent.createChooser(textIntent, "Share CBSE Notes"))
        }
    }
}
