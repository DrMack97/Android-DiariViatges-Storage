package com.example.diariviatges.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.TravelDiaryUiState

/**
 * PANTALLA 1: formulari de nova ruta.
 *
 * És una pantalla "stateless": no guarda dades pròpies, només rep l'estat
 * (uiState) i avisa el ViewModel dels canvis amb callbacks (onXxxChange).
 * Així tota la lògica i la validació queden al ViewModel (MVVM).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    uiState: TravelDiaryUiState,
    onRouteNameChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onBudgetChange: (String) -> Unit,
    onDateSelected: (Long) -> Unit,
    onSoloChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    // Estat NOMÉS visual (obrir/tancar el diàleg): no és una dada del model,
    // per això es pot quedar a la UI amb remember.
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova ruta") },
                actions = {
                    // Accés a l'històric de rutes desades
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, contentDescription = "Històric")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()), // el formulari és llarg: permet fer scroll
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---- Nom de la ruta (obligatori) ----
            // isError pinta el camp en vermell i supportingText mostra el missatge.
            OutlinedTextField(
                value = uiState.routeName,
                onValueChange = onRouteNameChange,
                label = { Text("Nom de la ruta *") },
                isError = uiState.routeNameError != null,
                supportingText = { uiState.routeNameError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Destí (obligatori) ----
            OutlinedTextField(
                value = uiState.destination,
                onValueChange = onDestinationChange,
                label = { Text("Destí *") },
                isError = uiState.destinationError != null,
                supportingText = { uiState.destinationError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Dificultat: FilterChips d'opció única ----
            // Cada chip està "selected" només si coincideix amb la dificultat de l'estat,
            // així només n'hi pot haver un de marcat alhora.
            Text("Nivell de dificultat", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.entries.forEach { option ->
                    val selected = uiState.difficulty == option
                    FilterChip(
                        selected = selected,
                        onClick = { onDifficultyChange(option) },
                        label = { Text(option.label) },
                        leadingIcon = if (selected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.width(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }

            // ---- Pressupost: teclat numèric decimal ----
            OutlinedTextField(
                value = uiState.budget,
                onValueChange = onBudgetChange,
                label = { Text("Pressupost estimat (€) *") },
                isError = uiState.budgetError != null,
                supportingText = { uiState.budgetError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, // mostra el teclat amb números i decimal
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Data: camp de només lectura que obre el DatePickerDialog ----
            OutlinedTextField(
                value = uiState.date,
                onValueChange = {},          // no s'escriu a mà: es tria al calendari
                readOnly = true,
                label = { Text("Data de la ruta *") },
                isError = uiState.dateError != null,
                supportingText = { uiState.dateError?.let { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Triar data")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(onClick = { showDatePicker = true }) { Text("Seleccionar data") }

            // ---- Viatge en solitari: Switch + etiqueta ----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ruta feta en solitari", modifier = Modifier.weight(1f))
                Switch(checked = uiState.isSolo, onCheckedChange = onSoloChange)
            }

            // ---- Notes: camp multilínia ----
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = { Text("Notes de l'experiència") },
                placeholder = { Text("Relat, punts d'interès de la jornada...") },
                minLines = 4,                // multilínia: com a mínim 4 línies visibles
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Acció principal: desactivada si el formulari no és vàlid ----
            Button(
                onClick = onGenerateClick,
                enabled = uiState.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Generar Postal / Crònica") }

            TextButton(onClick = onHistoryClick, modifier = Modifier.fillMaxWidth()) {
                Text("Consultar històric de rutes (${uiState.history.size})")
            }
            Spacer(Modifier.padding(8.dp))
        }
    }

    // ---- DatePickerDialog de Material 3 ----
    if (showDatePicker) {
        // rememberDatePickerState guarda el dia seleccionat dins del calendari
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // selectedDateMillis és null si l'usuari no ha triat cap dia
                    datePickerState.selectedDateMillis?.let(onDateSelected)
                    showDatePicker = false
                }) { Text("Acceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel·lar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
