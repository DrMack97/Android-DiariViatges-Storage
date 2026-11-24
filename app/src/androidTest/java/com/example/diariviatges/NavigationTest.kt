package com.example.diariviatges

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/** Test end-to-end: omplir el formulari -> Generar -> la postal mostra les dades. */
class NavigationTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun generar_navega_a_la_postal_amb_les_dades() {
        rule.onNodeWithText("Nom de la ruta *").performTextInput("Camí de Ronda")
        rule.onNodeWithText("Destí *").performTextInput("Begur")
        rule.onNodeWithText("Moderada").performClick()
        rule.onNodeWithText("Pressupost estimat (€) *").performTextInput("45,5")
        rule.onNodeWithText("Notes de l'experiència").performScrollTo().performTextInput("Cales precioses")
        rule.onNodeWithText("Generar Postal / Crònica").performScrollTo().performClick()

        rule.waitForIdle()
        rule.onNodeWithText("Postal de viatge").assertExists()
        rule.onNodeWithText("BEGUR").assertExists()
        rule.onNodeWithText("Dificultat: Moderada").assertExists()
        rule.onNodeWithText("45.50 €", substring = true).assertExists()
        rule.onNodeWithText("Cales precioses").assertExists()
    }
}
