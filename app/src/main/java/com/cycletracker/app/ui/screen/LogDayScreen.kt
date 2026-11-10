package com.cycletracker.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cycletracker.app.domain.model.FlowIntensity
import com.cycletracker.app.domain.model.Mood
import com.cycletracker.app.domain.model.Symptom
import com.cycletracker.app.ui.viewmodel.LogDayViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDayScreen(
    epochDay: Long,
    onBack: () -> Unit,
    viewModel: LogDayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val date = viewModel.date

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Period toggle
            PeriodSection(
                isPeriod = state.isPeriod,
                flow = state.flow,
                onTogglePeriod = viewModel::togglePeriod,
                onFlowSelected = viewModel::setFlow
            )

            HorizontalDivider()

            // Symptoms
            SymptomsSection(
                selected = state.selectedSymptoms,
                onToggle = viewModel::toggleSymptom
            )

            HorizontalDivider()

            // Mood
            MoodSection(
                selectedMood = state.mood,
                onMoodSelected = viewModel::setMood
            )

            HorizontalDivider()

            // Pain level
            PainSection(
                painLevel = state.painLevel,
                onLevelChanged = viewModel::setPainLevel
            )

            HorizontalDivider()

            // Notes
            NotesSection(
                notes = state.notes,
                onNotesChanged = viewModel::setNotes
            )

            // Save button
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Entry")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PeriodSection(
    isPeriod: Boolean,
    flow: FlowIntensity,
    onTogglePeriod: () -> Unit,
    onFlowSelected: (FlowIntensity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Period", style = MaterialTheme.typography.titleMedium)
                Text("Mark this day as a period day", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = isPeriod, onCheckedChange = { onTogglePeriod() })
        }
        if (isPeriod) {
            Text("Flow intensity", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FlowIntensity.entries.filter { it != FlowIntensity.NONE }.forEach { level ->
                    FilterChip(
                        selected = flow == level,
                        onClick = { onFlowSelected(level) },
                        label = { Text(level.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SymptomsSection(
    selected: Set<Symptom>,
    onToggle: (Symptom) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Symptoms", style = MaterialTheme.typography.titleMedium)
        // Two-column chip grid
        val symptoms = Symptom.entries
        val chunked = symptoms.chunked(2)
        chunked.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { symptom ->
                    FilterChip(
                        selected = symptom in selected,
                        onClick = { onToggle(symptom) },
                        label = { Text(symptom.label, maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size < 2) Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MoodSection(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Mood", style = MaterialTheme.typography.titleMedium)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Mood.entries.forEach { mood ->
                FilterChip(
                    selected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) },
                    label = { Text(mood.emoji) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        selectedMood?.let {
            Text(it.label, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun PainSection(painLevel: Int, onLevelChanged: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Pain level", style = MaterialTheme.typography.titleMedium)
            Text(
                when (painLevel) {
                    0 -> "None"
                    1 -> "Mild"
                    2 -> "Moderate"
                    3 -> "Noticeable"
                    4 -> "Severe"
                    else -> "Very severe"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = painLevel.toFloat(),
            onValueChange = { onLevelChanged(it.toInt()) },
            valueRange = 0f..5f,
            steps = 4
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0", style = MaterialTheme.typography.labelSmall)
            Text("5", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun NotesSection(notes: String, onNotesChanged: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Notes", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChanged,
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            placeholder = { Text("How are you feeling today?") },
            minLines = 4,
            maxLines = Int.MAX_VALUE
        )
    }
}
