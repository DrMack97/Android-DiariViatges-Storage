package com.example.diariviatges

import com.example.diariviatges.data.RouteRepository
import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.RouteEntry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/** Tests de l'emmagatzematge intern: escriure i llegir el JSON. */
class RouteRepositoryTest {
    // Carpeta temporal que fa el paper de context.filesDir
    @get:Rule val tmp = TemporaryFolder()

    private fun route(id: Long) = RouteEntry(id, "Ruta $id", "Destí $id", Difficulty.FACIL, 10.0, "01/01/2026", false, "")

    @Test
    fun sense_fitxer_retorna_llista_buida() = runBlocking {
        assertTrue(RouteRepository(tmp.root).loadRoutes().isEmpty())
    }

    @Test
    fun desa_i_torna_a_llegir_diari_viatges_json() = runBlocking {
        val routes = listOf(route(1), route(2))
        RouteRepository(tmp.root).saveRoutes(routes)
        assertTrue(File(tmp.root, "diari_viatges.json").exists())
        // Un repositori NOU (com si haguéssim tornat a obrir l'app) llegeix les mateixes dades
        assertEquals(routes, RouteRepository(tmp.root).loadRoutes())
    }

    @Test
    fun fitxer_corrupte_no_peta() = runBlocking {
        File(tmp.root, "diari_viatges.json").writeText("això no és JSON")
        assertTrue(RouteRepository(tmp.root).loadRoutes().isEmpty())
    }
}
