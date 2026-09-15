package com.example.data.model

enum class Subject(val displayName: String, val emoji: String, val primaryColorHex: Long) {
    ALL("All Subjects", "🎒", 0xFF0284C7), // Sky Blue Dark
    MATHS("Maths", "📐", 0xFFEAB308), // Muted Yellow
    PHYSICS("Physics", "⚡", 0xFF0EA5E9), // Sky Blue
    CHEMISTRY("Chemistry", "🧪", 0xFF10B981) // Green
}

enum class DoubtStatus(val displayName: String, val colorHex: Long) {
    IN_PROGRESS("In Progress", 0xFFEAB308), // Muted Yellow
    SOLVED("Solved", 0xFF10B981), // Green
    REVISION_NEEDED("Needs Revision", 0xFF0284C7) // Sky Blue
}

data class SolutionStep(
    val stepNumber: Int,
    val title: String,
    val formula: String? = null, // e.g. "v = u + at" or "(a + b)² = a² + 2ab + b²"
    val formulaName: String? = null, // e.g. "First Equation of Motion"
    val explanation: String,
    val calculation: String? = null,
    // Graduated Hint & Pedagogical Evaluation
    val conceptualNudge: String = "What quantity are you isolating or solving for in this step? Check how the given quantities connect to your target variable.",
    val expectedKeyword: String? = null,
    val simplerExplanation: String? = null,
    val diagramType: String? = null, // "physics_vector", "chemistry_molecule", "math_graph"
    val diagramDescription: String? = null
)

data class ProblemDoubt(
    val id: String,
    val subject: Subject,
    val question: String,
    val imageUri: String? = null,
    val fileName: String? = null,
    val givenValues: List<String> = emptyList(),
    val steps: List<SolutionStep>,
    val finalAnswer: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSample: Boolean = false,
    val topic: String = "General STEM",
    val status: DoubtStatus = DoubtStatus.IN_PROGRESS,
    val difficulty: String = "Medium"
)

data class StepAttempt(
    val stepNumber: Int,
    val studentInput: String = "",
    val hintLevel: Int = 0, // 0 = None, 1 = Level 1 Nudge, 2 = Level 2 Formula, 3 = Level 3 Worked Step
    val isFormulaRevealed: Boolean = false,
    val isEvaluated: Boolean = false,
    val isCorrect: Boolean? = null,
    val feedback: String? = null,
    val isCompleted: Boolean = false,
    val scratchpadStrokes: Int = 0,
    val isSimplerExpanded: Boolean = false
)

data class UserProfile(
    val grade: String = "Class 10",
    val curriculumBoard: String = "CBSE",
    val selectedSubjects: List<Subject> = listOf(Subject.MATHS, Subject.PHYSICS, Subject.CHEMISTRY),
    val streakDays: Int = 5,
    val xp: Int = 420,
    val badges: List<String> = listOf("First Spark", "Formula Alchemist", "Persistent Thinker"),
    val parentLinked: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val language: String = "English",
    val isChildPrivacyActive: Boolean = true
)

data class TopicMastery(
    val subject: Subject,
    val topicName: String,
    val scorePercentage: Int, // 0 to 100
    val isWeakArea: Boolean = false,
    val doubtsCount: Int = 1
)

sealed interface SolverUiState {
    data object Idle : SolverUiState
    data class Loading(val message: String) : SolverUiState
    data class Solving(
        val doubt: ProblemDoubt,
        val visibleStepCount: Int = 1, // for backward compatibility: starts at 1
        val activeStepIndex: Int = 0, // 0-based index of current step being attempted
        val stepAttempts: Map<Int, StepAttempt> = emptyMap(),
        val isCompleted: Boolean = false,
        val isEvaluatingAttempt: Boolean = false,
        val isVoiceNarrating: Boolean = false
    ) : SolverUiState
    data class Error(val message: String) : SolverUiState
}

