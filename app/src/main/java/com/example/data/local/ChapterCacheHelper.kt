package com.example.data.local

import com.example.data.local.entity.CachedChapterEntity
import com.example.data.local.entity.CachedFormulaEntity
import com.example.data.local.entity.NotebookEntryEntity
import com.example.data.model.CBSEBoardQuestion
import com.example.data.model.CBSEClass
import com.example.data.model.CBSEFormula
import com.example.data.model.CBSEFormulaHandbookItem
import com.example.data.model.CBSENoteChapter
import com.example.data.model.StudentNotebookEntry
import com.example.data.model.Subject
import org.json.JSONArray
import org.json.JSONObject

object ChapterCacheHelper {

    fun chapterToEntity(chapter: CBSENoteChapter, timestamp: Long = System.currentTimeMillis()): CachedChapterEntity {
        // Key points to JSON
        val keyPointsArr = JSONArray()
        chapter.keyPoints.forEach { keyPointsArr.put(it) }

        // Formulas to JSON
        val formulasArr = JSONArray()
        chapter.formulas.forEach { f ->
            val obj = JSONObject().apply {
                put("name", f.name)
                put("formulaText", f.formulaText)
                put("description", f.description)
                f.units?.let { put("units", it) }
            }
            formulasArr.put(obj)
        }

        // Derivations to JSON
        val derivationsArr = JSONArray()
        chapter.derivationsOrReactions.forEach { derivationsArr.put(it) }

        // Board Questions to JSON
        val questionsArr = JSONArray()
        chapter.boardQuestions.forEach { q ->
            val obj = JSONObject().apply {
                put("question", q.question)
                put("yearRepeated", q.yearRepeated)
                put("answerSummary", q.answerSummary)
            }
            questionsArr.put(obj)
        }

        return CachedChapterEntity(
            id = chapter.id,
            classLevel = chapter.classLevel.name,
            subject = chapter.subject.name,
            chapterNumber = chapter.chapterNumber,
            title = chapter.title,
            ncertReference = chapter.ncertReference,
            weightageMarks = chapter.weightageMarks,
            summary = chapter.summary,
            keyPointsJson = keyPointsArr.toString(),
            formulasJson = formulasArr.toString(),
            derivationsJson = derivationsArr.toString(),
            boardQuestionsJson = questionsArr.toString(),
            pdfEstimatedPages = chapter.pdfEstimatedPages,
            cachedTimestamp = timestamp,
            isBookmarked = chapter.isBookmarked
        )
    }

    fun entityToChapter(entity: CachedChapterEntity): CBSENoteChapter {
        val keyPoints = mutableListOf<String>()
        try {
            val arr = JSONArray(entity.keyPointsJson)
            for (i in 0 until arr.length()) {
                keyPoints.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val formulas = mutableListOf<CBSEFormula>()
        try {
            val arr = JSONArray(entity.formulasJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                formulas.add(
                    CBSEFormula(
                        name = obj.getString("name"),
                        formulaText = obj.getString("formulaText"),
                        description = obj.getString("description"),
                        units = if (obj.has("units")) obj.getString("units") else null
                    )
                )
            }
        } catch (_: Exception) {}

        val derivations = mutableListOf<String>()
        try {
            val arr = JSONArray(entity.derivationsJson)
            for (i in 0 until arr.length()) {
                derivations.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val questions = mutableListOf<CBSEBoardQuestion>()
        try {
            val arr = JSONArray(entity.boardQuestionsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                questions.add(
                    CBSEBoardQuestion(
                        question = obj.getString("question"),
                        yearRepeated = obj.getString("yearRepeated"),
                        answerSummary = obj.getString("answerSummary")
                    )
                )
            }
        } catch (_: Exception) {}

        val classLevel = try {
            CBSEClass.valueOf(entity.classLevel)
        } catch (_: Exception) {
            CBSEClass.CLASS_12
        }

        val subject = try {
            Subject.valueOf(entity.subject)
        } catch (_: Exception) {
            Subject.MATHS
        }

        return CBSENoteChapter(
            id = entity.id,
            classLevel = classLevel,
            subject = subject,
            chapterNumber = entity.chapterNumber,
            title = entity.title,
            ncertReference = entity.ncertReference,
            weightageMarks = entity.weightageMarks,
            summary = entity.summary,
            keyPoints = keyPoints,
            formulas = formulas,
            derivationsOrReactions = derivations,
            boardQuestions = questions,
            pdfEstimatedPages = entity.pdfEstimatedPages,
            isBookmarked = entity.isBookmarked,
            cachedTimestamp = entity.cachedTimestamp
        )
    }

    fun extractFormulasFromChapter(chapter: CBSENoteChapter, timestamp: Long = System.currentTimeMillis()): List<CachedFormulaEntity> {
        return chapter.formulas.mapIndexed { index, formula ->
            CachedFormulaEntity(
                id = "${chapter.id}_f${index}",
                chapterId = chapter.id,
                chapterTitle = chapter.title,
                classLevel = chapter.classLevel.name,
                subject = chapter.subject.name,
                name = formula.name,
                formulaText = formula.formulaText,
                description = formula.description,
                units = formula.units,
                isFavorite = false,
                cachedTimestamp = timestamp
            )
        }
    }

    fun entityToFormulaHandbookItem(entity: CachedFormulaEntity): CBSEFormulaHandbookItem {
        val classLevel = try {
            CBSEClass.valueOf(entity.classLevel)
        } catch (_: Exception) {
            CBSEClass.CLASS_12
        }
        val subject = try {
            Subject.valueOf(entity.subject)
        } catch (_: Exception) {
            Subject.MATHS
        }
        return CBSEFormulaHandbookItem(
            id = entity.id,
            chapterId = entity.chapterId,
            chapterTitle = entity.chapterTitle,
            classLevel = classLevel,
            subject = subject,
            formula = CBSEFormula(
                name = entity.name,
                formulaText = entity.formulaText,
                description = entity.description,
                units = entity.units
            ),
            isFavorite = entity.isFavorite
        )
    }

    fun notebookToEntity(entry: StudentNotebookEntry): NotebookEntryEntity {
        val arr = JSONArray()
        entry.formulasJotted.forEach { arr.put(it) }
        return NotebookEntryEntity(
            id = entry.id,
            classLevel = entry.classLevel.name,
            subject = entry.subject.name,
            chapterTitle = entry.chapterTitle,
            title = entry.title,
            content = entry.content,
            formulasJottedJson = arr.toString(),
            timestamp = entry.timestamp,
            isStarred = entry.isStarred
        )
    }

    fun entityToNotebook(entity: NotebookEntryEntity): StudentNotebookEntry {
        val formulas = mutableListOf<String>()
        try {
            val arr = JSONArray(entity.formulasJottedJson)
            for (i in 0 until arr.length()) {
                formulas.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val classLevel = try {
            CBSEClass.valueOf(entity.classLevel)
        } catch (_: Exception) {
            CBSEClass.CLASS_12
        }
        val subject = try {
            Subject.valueOf(entity.subject)
        } catch (_: Exception) {
            Subject.MATHS
        }
        return StudentNotebookEntry(
            id = entity.id,
            classLevel = classLevel,
            subject = subject,
            chapterTitle = entity.chapterTitle,
            title = entity.title,
            content = entity.content,
            formulasJotted = formulas,
            timestamp = entity.timestamp,
            isStarred = entity.isStarred
        )
    }
}
