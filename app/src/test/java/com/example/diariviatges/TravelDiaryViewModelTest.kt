package com.example.diariviatges

import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.RouteValidator
import com.example.diariviatges.viewmodel.TravelDiaryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests de la lògica de validació i de l'estat del ViewModel. */
class TravelDiaryViewModelTest {

    @Test
    fun validator_detecta_camps_buits_i_pressupost_incorrecte() {
        assertNotNull(RouteValidator.validateRouteName("  "))
        assertNotNull(RouteValidator.validateDestination(""))
        assertNotNull(RouteValidator.validateBudget("abc"))
        assertNotNull(RouteValidator.validateBudget("-3"))
        assertNull(RouteValidator.validateBudget("12,5"))
        assertEquals(12.5, RouteValidator.parseBudget("12,5")!!, 0.0)
    }

    @Test
    fun formulari_buit_no_es_valid_i_buildRoute_retorna_null() {
        val vm = TravelDiaryViewModel()
        assertFalse(vm.uiState.value.isFormValid)
        assertNull(vm.buildRoute())
        // després d'intentar-ho, els errors queden visibles
        assertNotNull(vm.uiState.value.routeNameError)
    }

    @Test
    fun error_en_temps_real_apareix_i_desapareix() {
        val vm = TravelDiaryViewModel()
        vm.onRouteNameChange("")
        assertNotNull(vm.uiState.value.routeNameError)
        vm.onRouteNameChange("Ruta")
        assertNull(vm.uiState.value.routeNameError)
    }

    @Test
    fun formulari_complet_crea_route_entry() {
        val vm = TravelDiaryViewModel()
        vm.onRouteNameChange("Volta al Montseny")
        vm.onDestinationChange("Montseny")
        vm.onDifficultyChange(Difficulty.EXIGENT)
        vm.onBudgetChange("30")
        vm.onSoloChange(true)
        vm.onNotesChange("Boira i faigs")
        assertTrue(vm.uiState.value.isFormValid)
        val route = vm.buildRoute()!!
        assertEquals("Montseny", route.destination)
        assertEquals(Difficulty.EXIGENT, route.difficulty)
        assertEquals(30.0, route.estimatedBudget, 0.0)
        assertTrue(route.isSolo)
    }

    @Test
    fun datepicker_formata_en_utc() {
        // 0 ms = 01/01/1970 en UTC
        assertEquals("01/01/1970", TravelDiaryViewModel.formatDate(0L))
    }
}
