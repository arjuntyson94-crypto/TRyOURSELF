package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CBSEClass
import com.example.data.model.SolverUiState
import com.example.ui.components.OnboardingDialog
import com.example.ui.components.TryYourselfBrandIcon
import com.example.ui.screens.CBSENotebookScreen
import com.example.ui.screens.CBSENotesScreen
import com.example.ui.screens.DoubtHistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProgressInsightsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SolverScreen
import com.example.ui.theme.Emerald500
import com.example.ui.theme.FormulaBracketGold
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TryYourselfViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TryYourselfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    TryYourselfApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TryYourselfApp(
    viewModel: TryYourselfViewModel,
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val solverState by viewModel.solverState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = solverState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "AppScreenNavigation",
        modifier = modifier
    ) { state ->
        when (state) {
            is SolverUiState.Idle -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF082F49))
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (homeState.currentTab) {
                            AppTab.SOLVER -> {
                                HomeScreen(
                                    state = homeState,
                                    onQueryChange = viewModel::onQueryTextChange,
                                    onSubjectSelect = viewModel::onSelectSubject,
                                    onFilterSubject = viewModel::onFilterSubject,
                                    onOpenAttachmentSheet = { viewModel.setAttachmentSheetVisible(true) },
                                    onCloseAttachmentSheet = { viewModel.setAttachmentSheetVisible(false) },
                                    onImageAttached = viewModel::onAttachImage,
                                    onFileAttached = viewModel::onAttachFile,
                                    onRemoveAttachment = viewModel::onRemoveAttachment,
                                    onSubmit = viewModel::submitQuery,
                                    onSelectDoubt = viewModel::selectDoubt,
                                    onNavigateToTab = viewModel::selectTab
                                )
                            }
                            AppTab.HISTORY -> {
                                DoubtHistoryScreen(
                                    doubts = homeState.doubtsTable,
                                    onSelectDoubt = viewModel::selectDoubt
                                )
                            }
                            AppTab.INSIGHTS -> {
                                ProgressInsightsScreen(
                                    userProfile = homeState.userProfile,
                                    onPracticeTopic = viewModel::practiceTopic
                                )
                            }
                            AppTab.SETTINGS -> {
                                SettingsScreen(
                                    userProfile = homeState.userProfile,
                                    onUpdateProfile = viewModel::updateUserProfile,
                                    onOpenOnboarding = { viewModel.setOnboardingOpen(true) }
                                )
                            }
                            AppTab.CLASS_11 -> {
                                CBSENotesScreen(
                                    initialClass = CBSEClass.CLASS_11,
                                    onSaveToNotebook = viewModel::saveNotebookNote,
                                    cacheStatus = homeState.cacheStatus,
                                    onRefreshCache = viewModel::refreshLocalCache,
                                    onToggleChapterBookmark = viewModel::toggleChapterBookmark,
                                    onToggleFormulaFavorite = viewModel::toggleFormulaFavorite
                                )
                            }
                            AppTab.CLASS_12 -> {
                                CBSENotesScreen(
                                    initialClass = CBSEClass.CLASS_12,
                                    onSaveToNotebook = viewModel::saveNotebookNote,
                                    cacheStatus = homeState.cacheStatus,
                                    onRefreshCache = viewModel::refreshLocalCache,
                                    onToggleChapterBookmark = viewModel::toggleChapterBookmark,
                                    onToggleFormulaFavorite = viewModel::toggleFormulaFavorite
                                )
                            }
                            AppTab.NOTEBOOK -> {
                                CBSENotebookScreen(
                                    notes = homeState.notebookNotes,
                                    onSaveNote = viewModel::saveNotebookNote,
                                    onDeleteNote = viewModel::deleteNotebookNote
                                )
                            }
                        }
                    }

                    if (homeState.isOnboardingOpen) {
                        OnboardingDialog(
                            initialProfile = homeState.userProfile,
                            onSaveProfile = viewModel::updateUserProfile,
                            onDismiss = { viewModel.setOnboardingOpen(false) }
                        )
                    }

                    AppBottomNavBar(
                        currentTab = homeState.currentTab,
                        onSelectTab = viewModel::selectTab
                    )
                }
            }
            is SolverUiState.Loading -> {
                SolverLoadingView(message = state.message)
            }
            is SolverUiState.Solving -> {
                SolverScreen(
                    doubt = state.doubt,
                    visibleStepCount = state.visibleStepCount,
                    isCompleted = state.isCompleted,
                    onNextStep = viewModel::advanceToNextStep,
                    onRestart = viewModel::restartCurrentDoubt,
                    onBack = viewModel::backToHome,
                    onStepMastered = viewModel::onStepMastered
                )
            }
            is SolverUiState.Error -> {
                SolverErrorView(
                    message = state.message,
                    onBack = viewModel::backToHome
                )
            }
        }
    }
}

@Composable
fun AppBottomNavBar(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_bottom_nav_bar"),
        color = Color(0xFF0A3858),
        border = BorderStroke(1.dp, Color(0xFF0284C7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val primaryTabs = listOf(
                AppTab.SOLVER,
                AppTab.HISTORY,
                AppTab.INSIGHTS,
                AppTab.NOTEBOOK,
                AppTab.SETTINGS
            )
            primaryTabs.forEach { tab ->
                val isSelected = currentTab == tab ||
                        (tab == AppTab.NOTEBOOK && (currentTab == AppTab.CLASS_11 || currentTab == AppTab.CLASS_12))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF064E3B) else Color.Transparent,
                    border = if (isSelected) BorderStroke(1.dp, Color(0xFF10B981)) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectTab(tab) }
                        .testTag("bottom_tab_${tab.name}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tab.emoji,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            color = if (isSelected) Color(0xFFBAE6FD) else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SolverLoadingView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
            .padding(24.dp)
            .testTag("solver_loading_view"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TryYourselfBrandIcon(
                size = 64.dp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            CircularProgressIndicator(
                color = FormulaBracketGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TRyOURSELF",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = Color(0xFFBAE6FD),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0C4A6E),
                border = BorderStroke(1.dp, Color(0xFF0284C7))
            ) {
                Text(
                    text = "Preparing step-by-step guidance and formulas...",
                    color = Color(0xFFE0F2FE),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SolverErrorView(message: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Oops!",
                color = Color(0xFFF87171),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0EA5E9),
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("error_back_button")
            ) {
                Text(text = "Back to Query Table")
            }
        }
    }
}

// Retained for backward compatibility with GreetingScreenshotTest
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = Color(0xFF082F49)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "what's my task today ",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "TRyOURSELF $name",
                color = Color(0xFF38BDF8),
                fontSize = 14.sp
            )
        }
    }
}
