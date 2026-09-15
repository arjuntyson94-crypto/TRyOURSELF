package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cbse.CBSENotesRepository
import com.example.data.model.CacheStatus
import com.example.data.model.ProblemDoubt
import com.example.data.model.SolverUiState
import com.example.data.model.StudentNotebookEntry
import com.example.data.model.Subject
import com.example.data.network.GeminiSolver
import com.example.data.sample.SampleDoubts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val emoji: String) {
    SOLVER("Solve Doubts", "⚡"),
    HISTORY("History", "📋"),
    INSIGHTS("Insights", "📊"),
    NOTEBOOK("Notebook", "📝"),
    SETTINGS("Settings", "⚙️"),
    CLASS_11("Class 11", "📚"),
    CLASS_12("Class 12", "📖")
}

data class HomeUiState(
    val currentTab: AppTab = AppTab.SOLVER,
    val queryText: String = "",
    val selectedSubject: Subject = Subject.MATHS,
    val attachedImageUri: Uri? = null,
    val attachedFileName: String? = null,
    val isAttachmentSheetOpen: Boolean = false,
    val filterSubject: Subject = Subject.ALL,
    val doubtsTable: List<ProblemDoubt> = SampleDoubts.list,
    val isSearching: Boolean = false,
    val notebookNotes: List<StudentNotebookEntry> = emptyList(),
    val cacheStatus: CacheStatus = CacheStatus(),
    val userProfile: com.example.data.model.UserProfile = com.example.data.model.UserProfile(),
    val isOnboardingOpen: Boolean = false
)

class TryYourselfViewModel(application: Application) : AndroidViewModel(application) {

    private val solver = GeminiSolver(application.applicationContext)

    private val _homeState = MutableStateFlow(
        HomeUiState(
            notebookNotes = CBSENotesRepository.loadStudentNotes(application.applicationContext)
        )
    )
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _solverState = MutableStateFlow<SolverUiState>(SolverUiState.Idle)
    val solverState: StateFlow<SolverUiState> = _solverState.asStateFlow()

    init {
        // Initialize Room local cache for offline availability
        viewModelScope.launch {
            val status = CBSENotesRepository.ensureCachePopulated(application.applicationContext)
            _homeState.update { it.copy(cacheStatus = status) }
        }

        // Reactively observe notebook notes from Room database
        viewModelScope.launch {
            CBSENotesRepository.getStudentNotesFlow(application.applicationContext).collectLatest { notes ->
                _homeState.update { it.copy(notebookNotes = notes) }
            }
        }
    }

    fun refreshLocalCache() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val status = CBSENotesRepository.forceRefreshCache(context)
            _homeState.update { it.copy(cacheStatus = status) }
        }
    }

    fun toggleChapterBookmark(chapterId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            CBSENotesRepository.toggleChapterBookmark(context, chapterId, isBookmarked)
        }
    }

    fun toggleFormulaFavorite(formulaId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            CBSENotesRepository.toggleFormulaFavorite(context, formulaId, isFavorite)
        }
    }

    fun selectTab(tab: AppTab) {
        _homeState.update { it.copy(currentTab = tab) }
    }

    fun saveNotebookNote(note: StudentNotebookEntry) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            CBSENotesRepository.insertStudentNoteRoom(context, note)
        }
    }

    fun deleteNotebookNote(noteId: String) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            CBSENotesRepository.deleteStudentNoteRoom(context, noteId)
        }
    }

    fun onQueryTextChange(newText: String) {
        _homeState.update { it.copy(queryText = newText) }
    }

    fun onSelectSubject(subject: Subject) {
        _homeState.update { it.copy(selectedSubject = subject) }
    }

    fun onFilterSubject(subject: Subject) {
        _homeState.update { it.copy(filterSubject = subject) }
    }

    fun setAttachmentSheetVisible(visible: Boolean) {
        _homeState.update { it.copy(isAttachmentSheetOpen = visible) }
    }

    fun onAttachImage(uri: Uri) {
        _homeState.update {
            it.copy(
                attachedImageUri = uri,
                attachedFileName = "Photo attachment (${uri.lastPathSegment?.takeLast(12) ?: "image.jpg"})",
                isAttachmentSheetOpen = false
            )
        }
    }

    fun onAttachFile(uri: Uri, fileName: String?) {
        _homeState.update {
            it.copy(
                attachedImageUri = null,
                attachedFileName = fileName ?: "Document attachment (${uri.lastPathSegment?.takeLast(12) ?: "file"})",
                isAttachmentSheetOpen = false
            )
        }
    }

    fun onRemoveAttachment() {
        _homeState.update {
            it.copy(
                attachedImageUri = null,
                attachedFileName = null
            )
        }
    }

    fun submitQuery() {
        val query = _homeState.value.queryText.trim()
        val imageUri = _homeState.value.attachedImageUri
        val fileName = _homeState.value.attachedFileName
        val subject = _homeState.value.selectedSubject

        if (query.isBlank() && imageUri == null && fileName == null) {
            return
        }

        viewModelScope.launch {
            _solverState.value = SolverUiState.Loading("TRyOURSELF is analyzing your question step-by-step...")
            try {
                val solved = solver.solveDoubt(
                    questionText = query,
                    subject = subject,
                    imageUri = imageUri,
                    fileName = fileName
                )

                // Add to query history table
                _homeState.update { state ->
                    state.copy(
                        doubtsTable = listOf(solved) + state.doubtsTable,
                        queryText = "",
                        attachedImageUri = null,
                        attachedFileName = null
                    )
                }

                // Initial state: Step 1 revealed, remaining hidden until user taps "Next"
                _solverState.value = SolverUiState.Solving(
                    doubt = solved,
                    visibleStepCount = 1,
                    isCompleted = solved.steps.size <= 1
                )
            } catch (e: Exception) {
                _solverState.value = SolverUiState.Error("Could not solve doubt: ${e.message}")
            }
        }
    }

    fun selectDoubt(doubt: ProblemDoubt) {
        _solverState.value = SolverUiState.Solving(
            doubt = doubt,
            visibleStepCount = 1,
            isCompleted = doubt.steps.size <= 1
        )
    }

    /**
     * Advances to the next step.
     * The answer / next step should ONLY be visible when student gives the command for the next step.
     */
    fun advanceToNextStep() {
        val current = _solverState.value as? SolverUiState.Solving ?: return
        val nextCount = current.visibleStepCount + 1
        val isDone = nextCount >= current.doubt.steps.size
        _solverState.value = current.copy(
            visibleStepCount = nextCount.coerceAtMost(current.doubt.steps.size),
            isCompleted = isDone
        )
    }

    fun restartCurrentDoubt() {
        val current = _solverState.value as? SolverUiState.Solving ?: return
        _solverState.value = current.copy(
            visibleStepCount = 1,
            isCompleted = current.doubt.steps.size <= 1
        )
    }

    fun backToHome() {
        _solverState.value = SolverUiState.Idle
    }

    fun updateUserProfile(newProfile: com.example.data.model.UserProfile) {
        _homeState.update { it.copy(userProfile = newProfile, isOnboardingOpen = false) }
    }

    fun setOnboardingOpen(open: Boolean) {
        _homeState.update { it.copy(isOnboardingOpen = open) }
    }

    fun onStepMastered(stepNumber: Int, xpGained: Int) {
        _homeState.update { state ->
            val updatedProfile = state.userProfile.copy(
                xp = state.userProfile.xp + xpGained
            )
            state.copy(userProfile = updatedProfile)
        }
    }

    fun practiceTopic(topicName: String, subject: Subject) {
        // Find existing sample or create practice doubt
        val matchingDoubt = _homeState.value.doubtsTable.firstOrNull { it.subject == subject }
            ?: SampleDoubts.list.firstOrNull { it.subject == subject }
            ?: SampleDoubts.list.first()

        selectDoubt(matchingDoubt)
    }
}
