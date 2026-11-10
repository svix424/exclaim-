package com.cycletracker.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cycletracker.app.data.db.entity.DailyEntry
import com.cycletracker.app.domain.CyclePredictionEngine
import com.cycletracker.app.ui.viewmodel.HistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Cycle History", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary)
        }

        if (state.periodEvents.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))
                        Text("No cycles logged yet", style = MaterialTheme.typography.titleMedium)
                        Text("Start by marking period days on the Home or Calendar screens.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            val avgCycle = state.avgCycleLength
            if (avgCycle != null) {
                item {
                    StatsRow(
                        avgCycle = avgCycle,
                        avgPeriod = state.avgPeriodLength,
                        totalCycles = state.periodEvents.size
                    )
                }
            }

            itemsIndexed(state.periodEvents.reversed()) { index, event ->
                val cycleLength = if (index < state.periodEvents.size - 1) {
                    val next = state.periodEvents.reversed()[index + 1]
                    ChronoUnit.DAYS.between(event.start, next.start).toInt()
                } else null
                PeriodEventCard(event = event, cycleLength = cycleLength)
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun StatsRow(avgCycle: Int, avgPeriod: Int, totalCycles: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Avg. Cycle", "${avgCycle}d", Modifier.weight(1f))
        StatCard("Avg. Period", "${avgPeriod}d", Modifier.weight(1f))
        StatCard("Cycles", totalCycles.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PeriodEventCard(event: CyclePredictionEngine.PeriodEvent, cycleLength: Int?) {
    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy")
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text("${event.start.format(fmt)} – ${event.end.format(fmt)}",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Period: ${event.length} day${if (event.length != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            cycleLength?.let {
                Column(horizontalAlignment = Alignment.End) {
                    Text("${it}d", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text("cycle", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
