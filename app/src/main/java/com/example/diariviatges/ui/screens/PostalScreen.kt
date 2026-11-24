package com.example.diariviatges.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diariviatges.model.Difficulty
import com.example.diariviatges.model.RouteEntry

/**
 * PANTALLA 2: la postal / crònica.
 * Rep un RouteEntry (que ha arribat per navegació) i el mostra
 * maquetat com una targeta de viatge.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostalScreen(
    route: RouteEntry,
    onBack: () -> Unit,
    actions: @Composable () -> Unit = {}   // botons extra (Exportar / Compartir) a sota la targeta
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postal de viatge") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tornar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TravelCard(route)
            actions()
        }
    }
}

/** La targeta visual de viatge. */
@Composable
fun TravelCard(route: RouteEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        // Capçalera de color amb el DESTÍ destacat
        Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = route.destination.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = route.routeName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DifficultyBadge(route.difficulty)
            InfoRow(Icons.Default.CalendarMonth, "Data", route.date)
            InfoRow(Icons.Default.Place, "Destí", route.destination)
            InfoRow(Icons.Default.Euro, "Pressupost", "%.2f €".format(route.estimatedBudget))
            InfoRow(
                if (route.isSolo) Icons.Default.Person else Icons.Default.Group,
                "Viatge",
                if (route.isSolo) "En solitari" else "Acompanyat"
            )
            HorizontalDivider()
            Text("Relat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = route.notes.ifBlank { "Sense notes." },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Insígnia de dificultat: cada nivell té un color diferent.
 * Fem servir els colors del tema (containers) perquè funcioni en mode clar i fosc.
 * El "when" és exhaustiu sobre l'enum: si afegim un nivell nou, el compilador avisa.
 */
@Composable
fun DifficultyBadge(difficulty: Difficulty) {
    val (bg: Color, fg: Color) = when (difficulty) {
        Difficulty.FACIL -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        Difficulty.MODERADA -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        Difficulty.EXIGENT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(color = bg, shape = MaterialTheme.shapes.large) {
        Text(
            text = "Dificultat: ${difficulty.label}",
            color = fg,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** Fila d'informació: icona + etiqueta + valor. */
@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(
            text = "  $label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
