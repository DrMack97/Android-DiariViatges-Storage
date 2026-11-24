package com.example.diariviatges.model

/**
 * ESTAT DE LA UI (el que la pantalla ha de pintar en cada moment).
 *
 * És immutable: el ViewModel no el modifica mai directament, sinó que en crea
 * una còpia nova amb .copy() i l'emet pel StateFlow. Compose detecta el canvi
 * i torna a dibuixar (recomposició) només el que ha canviat.
 *
 * Conté tres grups de dades:
 *  1) Camps del formulari actual (tot String/Boolean, tal com els escriu l'usuari).
 *  2) Errors de validació (null = sense error).
 *  3) Històric de rutes desades (llegit de filesDir).
 */
data class TravelDiaryUiState(
    // --- 1) Camps del formulari ---
    val routeName: String = "",
    val destination: String = "",
    val difficulty: Difficulty = Difficulty.FACIL,
    val budget: String = "",          // String perquè l'usuari pot escriure "12," a mitges
    val date: String = "",
    val isSolo: Boolean = false,
    val notes: String = "",

    // --- 2) Errors de validació (es mostren sota cada camp) ---
    val routeNameError: String? = null,
    val destinationError: String? = null,
    val budgetError: String? = null,
    val dateError: String? = null,

    // --- 3) Històric de rutes guardades ---
    val history: List<RouteEntry> = emptyList(),

    // Missatge puntual per a l'usuari (ex: "Crònica exportada"), es mostra en un Snackbar
    val userMessage: String? = null
) {
    /**
     * Propietat calculada: el formulari és vàlid si els camps obligatoris
     * estan omplerts i no hi ha cap error. Controla si el botó està actiu.
     */
    val isFormValid: Boolean
        get() = routeName.isNotBlank() &&
                destination.isNotBlank() &&
                budget.isNotBlank() &&
                date.isNotBlank() &&
                routeNameError == null &&
                destinationError == null &&
                budgetError == null &&
                dateError == null
}
