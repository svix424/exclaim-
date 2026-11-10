package com.cycletracker.app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cycletracker.app.domain.Tip
import com.cycletracker.app.domain.TipCategory
import com.cycletracker.app.domain.TipsContent
import com.cycletracker.app.domain.model.CyclePhase
import com.cycletracker.app.ui.viewmodel.HomeViewModel

@Composable
fun TipsScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val phase = state.summary.phase
    val tips = TipsContent.tipsFor(phase)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Tips & Guidance", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary)
        }
        item {
            PhaseHeaderChip(phase)
        }
        items(tips) { tip ->
            TipCard(tip)
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun PhaseHeaderChip(phase: CyclePhase) {
    SuggestionChip(
        onClick = {},
        label = {
            Text("${phase.label} tips", style = MaterialTheme.typography.bodyMedium)
        },
        icon = {
            Icon(phaseIcon(phase), contentDescription = null, modifier = Modifier.size(18.dp))
        }
    )
}

@Composable
private fun TipCard(tip: Tip) {
    val uriHandler = LocalUriHandler.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                categoryIcon(tip.category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp).padding(top = 2.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tip.title, style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(tip.category.label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
                Text(tip.body, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (tip.url.isNotEmpty()) {
                    Text(
                        "Learn more →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { uriHandler.openUri(tip.url) }
                    )
                }
            }
        }
    }
}

private fun phaseIcon(phase: CyclePhase): ImageVector = when (phase) {
    CyclePhase.MENSTRUAL -> Icons.Default.WaterDrop
    CyclePhase.FOLLICULAR -> Icons.Default.LocalFlorist
    CyclePhase.OVULATION -> Icons.Default.Brightness7
    CyclePhase.LUTEAL -> Icons.Default.Nightlight
    else -> Icons.Default.Info
}

private fun categoryIcon(category: TipCategory): ImageVector = when (category) {
    TipCategory.NUTRITION -> Icons.Default.Restaurant
    TipCategory.WELLNESS -> Icons.Default.SelfImprovement
    TipCategory.EXERCISE -> Icons.Default.FitnessCenter
    TipCategory.HEALTH -> Icons.Default.HealthAndSafety
    TipCategory.MINDFULNESS -> Icons.Default.Spa
}
