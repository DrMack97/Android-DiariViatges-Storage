package com.example.diariviatges

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.example.diariviatges.data.RouteRepository
import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.ui.screens.FormScreen
import com.example.diariviatges.viewmodel.TravelDiaryViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Test d'interfície del formulari: components, validació visual i botó. */
class FormScreenTest {
    @get:Rule val rule = createComposeRule()
    private val vm = TravelDiaryViewModel(
        RouteRepository(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir)
    )

    @Before
    fun setUp() {
        rule.setContent {
            val s by vm.uiState.collectAsStateWithLifecycle()
            FormScreen(
                uiState = s,
                onRouteNameChange = vm::onRouteNameChange,
                onDestinationChange = vm::onDestinationChange,
                onDifficultyChange = vm::onDifficultyChange,
                onBudgetChange = vm::onBudgetChange,
                onDateSelected = vm::onDateSelected,
                onSoloChange = vm::onSoloChange,
                onNotesChange = vm::onNotesChange,
                onGenerateClick = {},
                onHistoryClick = {}
            )
        }
    }

    @Test
    fun boto_desactivat_amb_formulari_buit() {
        rule.onNodeWithText("Generar Postal / Crònica").assertIsNotEnabled()
    }

    @Test
    fun error_visible_si_el_nom_queda_buit() {
        rule.onNodeWithText("Nom de la ruta *").performTextInput("x")
        rule.onNodeWithText("Nom de la ruta *").performTextClearance()
        rule.onNodeWithText("El nom de la ruta és obligatori").assertExists()
    }

    @Test
    fun error_si_pressupost_no_numeric() {
        rule.onNodeWithText("Pressupost estimat (€) *").performTextInput("abc")
        rule.onNodeWithText("Introdueix un número vàlid").assertExists()
    }

    @Test
    fun formulari_complet_activa_el_boto_i_chips_switch_funcionen() {
        rule.onNodeWithText("Nom de la ruta *").performTextInput("Camí de Ronda")
        rule.onNodeWithText("Destí *").performTextInput("Begur")
        rule.onNodeWithText("Exigent").performClick()
        rule.onNodeWithText("Exigent").assertIsSelected()
        rule.onNodeWithText("Pressupost estimat (€) *").performTextInput("45,5")
        rule.onNode(isToggleable()).performScrollTo().performClick()
        rule.onNode(isToggleable()).assertIsOn()
        rule.onNodeWithText("Generar Postal / Crònica").performScrollTo().assertIsEnabled()
        assertEquals(Difficulty.EXIGENT, vm.uiState.value.difficulty)
    }

    @Test
    fun boto_data_obre_el_datepicker() {
        rule.onNodeWithText("Seleccionar data").performScrollTo().performClick()
        rule.onNodeWithText("Acceptar").assertExists()
        rule.onNodeWithText("Cancel·lar").performClick()
    }
}
