package com.example.diariviatges.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.diariviatges.data.RouteRepository
import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.RouteEntry
import com.example.diariviatges.model.RouteValidator
import com.example.diariviatges.model.TravelDiaryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * VIEWMODEL: fa de pont entre el Model i les Vistes (Compose).
 *
 * - _uiState (MutableStateFlow, PRIVAT): només el ViewModel el pot modificar.
 * - uiState  (StateFlow, PÚBLIC, només lectura): les pantalles l'observen.
 * - Cada canvi es fa amb _uiState.update { it.copy(...) }: es crea un estat
 *   NOU a partir de l'anterior (immutabilitat), mai es modifica el vell.
 * - Sobreviu als canvis de configuració (girar la pantalla) i no es perden les dades.
 *
 * Rep el repositori pel constructor (injecció de dependències): el ViewModel no
 * sap COM es guarden les dades, només demana "carrega" i "desa".
 */
class TravelDiaryViewModel(
    private val repository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        // Per defecte proposem la data d'avui
        TravelDiaryUiState(date = formatDate(System.currentTimeMillis()))
    )
    val uiState: StateFlow<TravelDiaryUiState> = _uiState.asStateFlow()

    init {
        // En obrir l'app: llegim l'històric de filesDir i el posem a l'estat
        viewModelScope.launch {
            val saved = repository.loadRoutes()
            _uiState.update { it.copy(history = saved) }
        }
    }

    // ---------- Esdeveniments del formulari (validació en temps real) ----------

    fun onRouteNameChange(value: String) = _uiState.update {
        it.copy(routeName = value, routeNameError = RouteValidator.validateRouteName(value))
    }

    fun onDestinationChange(value: String) = _uiState.update {
        it.copy(destination = value, destinationError = RouteValidator.validateDestination(value))
    }

    fun onDifficultyChange(value: Difficulty) = _uiState.update { it.copy(difficulty = value) }

    fun onBudgetChange(value: String) = _uiState.update {
        it.copy(budget = value, budgetError = RouteValidator.validateBudget(value))
    }

    /** Rep els mil·lisegons que retorna el DatePicker i els guarda formatats. */
    fun onDateSelected(millis: Long) {
        val text = formatDate(millis)
        _uiState.update { it.copy(date = text, dateError = RouteValidator.validateDate(text)) }
    }

    fun onSoloChange(value: Boolean) = _uiState.update { it.copy(isSolo = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    // ---------- Creació de la ruta ----------

    /**
     * Valida TOTS els camps (per si l'usuari no n'ha tocat algun) i, si tot és
     * correcte, construeix un RouteEntry immutable amb les dades del formulari.
     * Retorna null si hi ha errors (i els deixa visibles a la UI).
     */
    fun buildRoute(): RouteEntry? {
        val s = _uiState.value
        val validated = s.copy(
            routeNameError = RouteValidator.validateRouteName(s.routeName),
            destinationError = RouteValidator.validateDestination(s.destination),
            budgetError = RouteValidator.validateBudget(s.budget),
            dateError = RouteValidator.validateDate(s.date)
        )
        _uiState.value = validated
        if (!validated.isFormValid) return null

        return RouteEntry(
            id = System.currentTimeMillis(),
            routeName = s.routeName.trim(),
            destination = s.destination.trim(),
            difficulty = s.difficulty,
            estimatedBudget = RouteValidator.parseBudget(s.budget) ?: 0.0,
            date = s.date,
            isSolo = s.isSolo,
            notes = s.notes.trim()
        )
    }

    /**
     * Acció del botó "Generar Postal / Crònica":
     * valida, crea la ruta i la DESA automàticament a l'històric (filesDir).
     * Retorna la ruta creada (per navegar a la postal) o null si hi ha errors.
     */
    fun generateChronicle(): RouteEntry? {
        val route = buildRoute() ?: return null
        // Llista nova (immutable) amb la ruta al principi: la més recent primer
        val newHistory = listOf(route) + _uiState.value.history
        _uiState.update { it.copy(history = newHistory) }
        viewModelScope.launch { repository.saveRoutes(newHistory) }
        return route
    }

    /** Esborra una ruta de l'històric i torna a desar el fitxer. */
    fun deleteRoute(route: RouteEntry) {
        val newHistory = _uiState.value.history.filterNot { it.id == route.id }
        _uiState.update { it.copy(history = newHistory) }
        viewModelScope.launch { repository.saveRoutes(newHistory) }
    }

    /** Buida el formulari (mantenint l'històric) per introduir una ruta nova. */
    fun resetForm() = _uiState.update {
        TravelDiaryUiState(date = formatDate(System.currentTimeMillis()), history = it.history)
    }

    /** Esborra el missatge del Snackbar un cop mostrat. */
    fun messageShown() = _uiState.update { it.copy(userMessage = null) }

    companion object {
        /**
         * Factory: explica a Android COM crear aquest ViewModel, ja que té un
         * paràmetre al constructor. Agafem l'Application per obtenir filesDir.
         */
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                TravelDiaryViewModel(RouteRepository(app.filesDir))
            }
        }

        /**
         * El DatePicker de Material 3 retorna la data en mil·lisegons UTC,
         * per això formatem en zona UTC (si no, podria sortir el dia anterior).
         */
        fun formatDate(millis: Long): String =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(Date(millis))
    }
}
