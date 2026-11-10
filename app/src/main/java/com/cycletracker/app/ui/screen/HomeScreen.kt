package com.cycletracker.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cycletracker.app.domain.model.*
import com.cycletracker.app.ui.theme.Macchiato
import com.cycletracker.app.ui.theme.Latte
import com.cycletracker.app.ui.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onLogDay: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val summary = state.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App title
        Text(
            "Exclamation Mark",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Phase card — expanded with body detail and quick wins
        PhaseCard(summary = summary)

        // Cycle stats row
        if (summary.phase != CyclePhase.UNKNOWN) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Cycle Day",
                    value = summary.dayOfCycle?.toString() ?: "–",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Avg. Cycle",
                    value = "${summary.averageCycleLength}d",
                    modifier = Modifier.weight(1f)
                )
                val daysLabel = when {
                    summary.daysUntilNextPeriod == null -> "–"
                    summary.daysUntilNextPeriod < 0 -> "Late"
                    summary.daysUntilNextPeriod == 0 -> "Today"
                    else -> "in ${summary.daysUntilNextPeriod}d"
                }
                StatCard(
                    label = "Next Period",
                    value = daysLabel,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Today's period toggle
        TodayPeriodCard(
            isPeriod = state.todayEntry?.isPeriod ?: false,
            onToggle = { viewModel.togglePeriodToday() }
        )

        // Log button
        Button(
            onClick = { onLogDay(LocalDate.now().toEpochDay()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Log Today's Symptoms & Notes")
        }

        // Upcoming dates
        if (summary.nextPeriodDate != null || summary.ovulationDate != null) {
            UpcomingCard(summary)
        }

        // Previous period summary
        val lastPeriod = state.lastPeriodSummary
        if (lastPeriod != null) {
            LastPeriodCard(lastPeriod)
        }

        // Privacy notice
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "All data is encrypted and stored only on this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PhaseCard(summary: CycleSummary) {
    val (gradStart, gradEnd) = phaseGradient(summary.phase)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(gradStart, gradEnd)))
            .padding(20.dp)
    ) {
        val textMain  = Macchiato.Crust
        val textMuted = Macchiato.Crust.copy(alpha = 0.72f)
        val divider   = Macchiato.Crust.copy(alpha = 0.20f)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                summary.phase.label,
                style = MaterialTheme.typography.headlineMedium,
                color = textMain,
                fontWeight = FontWeight.Bold
            )
            Text(
                summary.phase.description,
                style = MaterialTheme.typography.titleSmall,
                color = textMuted
            )

            HorizontalDivider(color = divider, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp))

            Text("What's happening", style = MaterialTheme.typography.labelMedium,
                color = textMuted, fontWeight = FontWeight.SemiBold)
            Text(summary.phase.bodyDetail, style = MaterialTheme.typography.bodySmall,
                color = textMain)

            Spacer(Modifier.height(4.dp))

            Text("What helps", style = MaterialTheme.typography.labelMedium,
                color = textMuted, fontWeight = FontWeight.SemiBold)
            summary.phase.quickWins.forEach { tip ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("•", color = textMuted, style = MaterialTheme.typography.bodySmall)
                    Text(tip.text, color = textMain, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (summary.confidence == Confidence.LOW && summary.phase != CyclePhase.UNKNOWN) {
                Spacer(Modifier.height(4.dp))
                Text("Log more cycles for better predictions",
                    style = MaterialTheme.typography.labelSmall, color = textMuted)
            }
        }
    }
}

@Composable
private fun LastPeriodCard(summary: HomeViewModel.LastPeriodSummary) {
    Card {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Previous Period", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${summary.durationDays} days",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                summary.dateRange,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Stats row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStat("Flow", summary.dominantFlow, Modifier.weight(1f))
                MiniStat("Pain", summary.avgPainLabel, Modifier.weight(1f))
                if (summary.noteCount > 0) {
                    MiniStat(
                        "Notes",
                        "${summary.noteCount}",
                        Modifier.weight(1f)
                    )
                }
            }

            // Symptoms
            if (summary.topSymptoms.isNotEmpty()) {
                Text(
                    "Symptoms logged",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    summary.topSymptoms.forEach { s ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(s, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            } else {
                Text(
                    "No symptoms were logged for that period.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun phaseGradient(phase: CyclePhase): Pair<Color, Color> = when (phase) {
    // Macchiato colours — rich, saturated, cohesive
    CyclePhase.MENSTRUAL  -> Macchiato.Maroon  to Macchiato.Red
    CyclePhase.FOLLICULAR -> Macchiato.Blue    to Macchiato.Lavender
    CyclePhase.OVULATION  -> Macchiato.Mauve   to Macchiato.Pink
    CyclePhase.LUTEAL     -> Macchiato.Sapphire to Macchiato.Teal
    CyclePhase.LATE       -> Macchiato.Peach   to Macchiato.Yellow
    CyclePhase.UNKNOWN    -> Macchiato.Overlay0 to Macchiato.Surface2
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TodayPeriodCard(isPeriod: Boolean, onToggle: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isPeriod) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Period today?", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (isPeriod) "Marked — tap to unmark" else "Tap to mark period",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = isPeriod, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun UpcomingCard(summary: CycleSummary) {
    val fmt = DateTimeFormatter.ofPattern("MMM d")
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Upcoming", style = MaterialTheme.typography.titleMedium)
            summary.nextPeriodDate?.let {
                UpcomingRow(Icons.Default.WaterDrop, "Next period", it.format(fmt))
            }
            summary.ovulationDate?.let {
                UpcomingRow(Icons.Default.Brightness7, "Estimated ovulation", it.format(fmt))
            }
        }
    }
}

@Composable
private fun UpcomingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    date: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(date, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    }
}
