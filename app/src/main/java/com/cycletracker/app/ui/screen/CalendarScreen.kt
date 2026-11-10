package com.cycletracker.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cycletracker.app.ui.components.CycleCalendar
import com.cycletracker.app.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    onDayClick: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Calendar", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)

        Card {
            CycleCalendar(
                month = state.displayMonth,
                dayInfoMap = state.dayInfoMap,
                selectedDate = state.selectedDate,
                onDayClick = { date ->
                    viewModel.selectDate(date)
                },
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                modifier = Modifier.padding(12.dp)
            )
        }

        // Selected day detail
        state.selectedDate?.let { date ->
            SelectedDayCard(
                date = date,
                info = state.dayInfoMap[date],
                onLogClick = { onDayClick(date.toEpochDay()) }
            )
        }
    }
}

@Composable
private fun SelectedDayCard(
    date: LocalDate,
    info: com.cycletracker.app.domain.model.DayInfo?,
    onLogClick: () -> Unit
) {
    val fmt = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(date.format(fmt), style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = onLogClick) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Log")
                }
            }
            if (info != null) {
                val tags = buildList {
                    if (info.isPeriod) add("Period")
                    if (info.isPredictedPeriod) add("Predicted period")
                    if (info.isOvulation) add("Ovulation")
                    if (info.isFertileWindow) add("Fertile window")
                    if (info.hasLog) add("Has log entry")
                }
                if (tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { tag ->
                            SuggestionChip(onClick = {}, label = { Text(tag) })
                        }
                    }
                } else {
                    Text("No data for this day.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
