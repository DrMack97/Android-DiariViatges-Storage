package com.example.diariviatges.data

import com.example.diariviatges.model.RouteEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

/**
 * REPOSITORI: s'encarrega de llegir i escriure l'històric de rutes.
 *
 * EMMAGATZEMATGE INTERN (context.filesDir):
 *  - És una carpeta PRIVADA de l'app (/data/data/<paquet>/files/).
 *  - No cal cap permís i cap altra app (ni l'usuari) hi pot accedir.
 *  - S'ESBORRA quan es desinstal·la l'app.
 *
 * Rep la carpeta (File) en lloc del Context perquè així es pot provar amb
 * tests unitaris fent servir una carpeta temporal.
 */
class RouteRepository(filesDir: File) {

    // Fitxer JSON on es guarda tot l'històric
    private val file = File(filesDir, FILE_NAME)

    // Configuració del parser JSON:
    //  - prettyPrint: el fitxer és llegible si l'obrim (Device Explorer)
    //  - ignoreUnknownKeys: si en el futur traiem un camp, els fitxers vells es poden llegir igual
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    /**
     * Llegeix l'històric. Si el fitxer no existeix (primera vegada) o està
     * corrupte, retorna una llista buida en lloc de petar.
     * withContext(Dispatchers.IO): l'accés a disc es fa fora del fil principal.
     */
    suspend fun loadRoutes(): List<RouteEntry> = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext emptyList()
        try {
            json.decodeFromString<List<RouteEntry>>(file.readText())
        } catch (e: SerializationException) {
            emptyList()
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    /** Sobreescriu el fitxer amb la llista completa serialitzada a JSON. */
    suspend fun saveRoutes(routes: List<RouteEntry>) = withContext(Dispatchers.IO) {
        file.writeText(json.encodeToString(routes))
    }

    companion object {
        const val FILE_NAME = "diari_viatges.json"
    }
}
