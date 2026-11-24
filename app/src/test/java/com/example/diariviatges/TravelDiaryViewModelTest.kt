package com.example.diariviatges

import com.example.diariviatges.data.RouteRepository
import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.RouteValidator
import com.example.diariviatges.viewmodel.TravelDiaryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Tests de la lògica de validació i de l'estat del ViewModel. */
@OptIn(ExperimentalCoroutinesApi::class)
class TravelDiaryViewModelTest {

    @get:Rule val tmp = TemporaryFolder()

    // viewModelScope fa servir Dispatchers.Main, que no existeix en tests JVM:
    // el substituïm per un dispatcher de test.
    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun newVm() = TravelDiaryViewModel(RouteRepository(tmp.root))

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
        val vm = newVm()
        assertFalse(vm.uiState.value.isFormValid)
        assertNull(vm.buildRoute())
        // després d'intentar-ho, els errors queden visibles
        assertNotNull(vm.uiState.value.routeNameError)
    }

    @Test
    fun error_en_temps_real_apareix_i_desapareix() {
        val vm = newVm()
        vm.onRouteNameChange("")
        assertNotNull(vm.uiState.value.routeNameError)
        vm.onRouteNameChange("Ruta")
        assertNull(vm.uiState.value.routeNameError)
    }

    @Test
    fun formulari_complet_crea_route_entry() {
        val vm = newVm()
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

    @Test
    fun generar_desa_a_l_historic_i_es_recupera_en_reobrir() {
        val vm = newVm()
        vm.onRouteNameChange("Ruta 1")
        vm.onDestinationChange("Girona")
        vm.onBudgetChange("20")
        val route = vm.generateChronicle()!!
        assertEquals(listOf(route), vm.uiState.value.history)

        // Simulem tornar a obrir l'app: ViewModel nou sobre la mateixa carpeta
        Thread.sleep(300) // l'escriptura es fa a Dispatchers.IO
        val reopened = newVm()
        Thread.sleep(300)
        assertEquals(listOf(route), reopened.uiState.value.history)
    }
}
