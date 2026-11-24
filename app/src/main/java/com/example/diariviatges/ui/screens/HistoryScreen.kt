package com.example.diariviatges.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diariviatges.model.RouteEntry

/**
 * PANTALLA D'HISTÒRIC: llista de rutes guardades a filesDir.
 * LazyColumn només dibuixa els elements visibles (eficient amb llistes llargues).
 * En tocar una ruta, s'obre la seva postal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<RouteEntry>,
    onRouteClick: (RouteEntry) -> Unit,
    onDeleteClick: (RouteEntry) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Històric de rutes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tornar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Encara no has desat cap ruta.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // key = id: Compose identifica cada element encara que la llista canviï
                items(history, key = { it.id }) { route ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onRouteClick(route) }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(route.routeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${route.destination} · ${route.date}", style = MaterialTheme.typography.bodyMedium)
                                DifficultyBadge(route.difficulty)
                            }
                            IconButton(onClick = { onDeleteClick(route) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Esborrar ${route.routeName}")
                            }
                        }
                    }
                }
            }
        }
    }
}
