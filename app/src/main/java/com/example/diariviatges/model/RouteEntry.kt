package com.example.diariviatges.model

import kotlinx.serialization.Serializable

/**
 * MODEL: una ruta / escapada guardada al diari.
 *
 * - És una data class IMMUTABLE (totes les propietats són val): si volem canviar
 *   alguna dada, en creem una còpia nova amb .copy(...).
 * - @Serializable (kotlinx.serialization) genera automàticament el codi per
 *   convertir l'objecte a JSON i tornar-lo a llegir (històric a filesDir i
 *   pas de dades entre pantalles).
 */
@Serializable
data class RouteEntry(
    val id: Long,                 // identificador únic (timestamp de creació)
    val routeName: String,        // nom de la ruta
    val destination: String,      // destí
    val difficulty: Difficulty,   // Fàcil / Moderada / Exigent
    val estimatedBudget: Double,  // pressupost estimat en euros
    val date: String,             // data de la ruta (format dd/MM/yyyy)
    val isSolo: Boolean,          // true si s'ha fet en solitari
    val notes: String             // relat / punts d'interès
) {
    /**
     * Converteix la ruta en un text Markdown llegible (la "crònica").
     * El fem servir per exportar a fitxer (.md/.txt) i per compartir.
     */
    fun toChronicleText(): String = buildString {
        appendLine("# ${routeName}")
        appendLine()
        appendLine("- **Destí:** ${destination}")
        appendLine("- **Data:** ${date}")
        appendLine("- **Dificultat:** ${difficulty.label}")
        appendLine("- **Pressupost:** ${"%.2f".format(estimatedBudget)} €")
        appendLine("- **Viatge en solitari:** ${if (isSolo) "Sí" else "No"}")
        appendLine()
        appendLine("## Relat")
        appendLine()
        appendLine(notes.ifBlank { "(Sense notes)" })
    }
}
