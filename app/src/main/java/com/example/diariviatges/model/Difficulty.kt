package com.example.diariviatges.model

import kotlinx.serialization.Serializable

/**
 * Nivell de dificultat d'una ruta.
 * Fem servir un enum (i no un String) perquè només hi ha 3 valors vàlids:
 * així el compilador ens impedeix escriure valors incorrectes.
 * [label] és el text que es mostra a la UI (amb accents en català).
 * @Serializable permet guardar-lo dins del JSON de l'històric.
 */
@Serializable
enum class Difficulty(val label: String) {
    FACIL("Fàcil"),
    MODERADA("Moderada"),
    EXIGENT("Exigent")
}
