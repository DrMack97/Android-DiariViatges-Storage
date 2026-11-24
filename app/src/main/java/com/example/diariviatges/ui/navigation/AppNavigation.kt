package com.example.diariviatges.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.diariviatges.model.RouteEntry
import com.example.diariviatges.ui.screens.FormScreen
import com.example.diariviatges.ui.screens.HistoryScreen
import com.example.diariviatges.ui.screens.PostalScreen
import com.example.diariviatges.viewmodel.TravelDiaryViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/*
 * DESTINACIONS DE NAVEGACIÓ (Navigation Compose "type-safe").
 * Cada pantalla és un objecte/classe @Serializable: Navigation el converteix
 * en una ruta i, si té paràmetres, els passa com a arguments de navegació.
 */
@Serializable object FormDestination
@Serializable object HistoryDestination

/**
 * La postal rep la ruta com a TEXT JSON (serialitzat amb kotlinx.serialization).
 * Així les dades viatgen com a argument de navegació i la pantalla de destí
 * reconstrueix l'objecte RouteEntry amb Json.decodeFromString.
 */
@Serializable data class PostalDestination(val routeJson: String)

/**
 * NavHost: el "contenidor" que canvia de pantalla segons la ruta actual.
 * El ViewModel es crea aquí UNA vegada i es comparteix entre pantalles,
 * així el formulari conserva les dades quan tornem enrere.
 */
@Composable
fun AppNavigation(vm: TravelDiaryViewModel = viewModel(factory = TravelDiaryViewModel.Factory)) {
    val navController = rememberNavController()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = FormDestination) {

        composable<FormDestination> {
            FormScreen(
                uiState = uiState,
                onRouteNameChange = vm::onRouteNameChange,
                onDestinationChange = vm::onDestinationChange,
                onDifficultyChange = vm::onDifficultyChange,
                onBudgetChange = vm::onBudgetChange,
                onDateSelected = vm::onDateSelected,
                onSoloChange = vm::onSoloChange,
                onNotesChange = vm::onNotesChange,
                onGenerateClick = {
                    // 1) El ViewModel valida, crea el RouteEntry i el desa a filesDir
                    val route = vm.generateChronicle() ?: return@FormScreen
                    // 2) Serialitzem a JSON i naveguem passant-lo com a argument
                    navController.navigate(PostalDestination(Json.encodeToString(route)))
                },
                onHistoryClick = { navController.navigate(HistoryDestination) }
            )
        }

        composable<PostalDestination> { backStackEntry ->
            // toRoute() llegeix els arguments de navegació
            val args = backStackEntry.toRoute<PostalDestination>()
            // Deserialitzem el JSON per recuperar l'objecte original
            val route = Json.decodeFromString<RouteEntry>(args.routeJson)
            PostalScreen(
                route = route,
                onBack = { navController.popBackStack() }
            )
        }

        composable<HistoryDestination> {
            HistoryScreen(
                history = uiState.history,
                // Reutilitzem la mateixa pantalla de postal passant la ruta en JSON
                onRouteClick = { route ->
                    navController.navigate(PostalDestination(Json.encodeToString(route)))
                },
                onDeleteClick = vm::deleteRoute,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
