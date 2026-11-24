package com.example.diariviatges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariviatges.ui.screens.FormScreen
import com.example.diariviatges.ui.theme.DiariViatgesTheme
import com.example.diariviatges.viewmodel.TravelDiaryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiariViatgesTheme {
                val vm: TravelDiaryViewModel = viewModel()
                val uiState by vm.uiState.collectAsStateWithLifecycle()
                FormScreen(
                    uiState = uiState,
                    onRouteNameChange = vm::onRouteNameChange,
                    onDestinationChange = vm::onDestinationChange,
                    onDifficultyChange = vm::onDifficultyChange,
                    onBudgetChange = vm::onBudgetChange,
                    onDateSelected = vm::onDateSelected,
                    onSoloChange = vm::onSoloChange,
                    onNotesChange = vm::onNotesChange,
                    onGenerateClick = { vm.buildRoute() },
                    onHistoryClick = {}
                )
            }
        }
    }
}
