package com.example.diariviatges.model

/**
 * Regles de validació del formulari (lògica pura, sense Android).
 * Cada funció retorna el missatge d'error o null si el valor és correcte.
 * Al ser un object sense dependències, es pot provar amb tests unitaris normals.
 */
object RouteValidator {

    fun validateRouteName(value: String): String? =
        if (value.isBlank()) "El nom de la ruta és obligatori" else null

    fun validateDestination(value: String): String? =
        if (value.isBlank()) "El destí és obligatori" else null

    fun validateDate(value: String): String? =
        if (value.isBlank()) "Selecciona una data" else null

    /**
     * El pressupost ha de ser un número >= 0.
     * Acceptem coma o punt decimal ("12,5" o "12.5") perquè el teclat
     * decimal en català/espanyol sol mostrar la coma.
     */
    fun validateBudget(value: String): String? {
        if (value.isBlank()) return "El pressupost és obligatori"
        val number = parseBudget(value) ?: return "Introdueix un número vàlid"
        return if (number < 0) "El pressupost no pot ser negatiu" else null
    }

    /** Converteix el text del pressupost a Double (o null si no és un número). */
    fun parseBudget(value: String): Double? =
        value.trim().replace(',', '.').toDoubleOrNull()
}
