package com.example.diariviatges

import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.RouteEntry
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests del MODEL: serialització JSON i text de la crònica. */
class RouteEntryTest {
    private val route = RouteEntry(
        id = 1L, routeName = "Camí de Ronda", destination = "Begur",
        difficulty = Difficulty.MODERADA, estimatedBudget = 45.5,
        date = "24/11/2026", isSolo = true, notes = "Cales precioses"
    )

    @Test
    fun json_anada_i_tornada_dona_el_mateix_objecte() {
        val json = Json.encodeToString(route)
        assertEquals(route, Json.decodeFromString<RouteEntry>(json))
    }

    @Test
    fun copy_no_modifica_l_original() {
        val altra = route.copy(destination = "Pals")
        assertEquals("Begur", route.destination)
        assertEquals("Pals", altra.destination)
    }

    @Test
    fun cronica_conte_les_dades() {
        val text = route.toChronicleText()
        assertTrue(text.contains("# Camí de Ronda"))
        assertTrue(text.contains("Moderada"))
        assertTrue(text.contains("Sí"))
    }
}
