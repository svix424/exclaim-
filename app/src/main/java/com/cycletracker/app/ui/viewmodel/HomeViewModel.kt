package com.cycletracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycletracker.app.data.db.entity.DailyEntry
import com.cycletracker.app.data.repository.CycleRepository
import com.cycletracker.app.domain.CyclePredictionEngine
import com.cycletracker.app.domain.model.CycleSummary
import com.cycletracker.app.domain.model.FlowIntensity
import com.cycletracker.app.domain.model.Symptom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CycleRepository,
    private val engine: CyclePredictionEngine
) : ViewModel() {

    data class LastPeriodSummary(
        val dateRange: String,
        val durationDays: Int,
        val topSymptoms: List<String>,
        val avgPainLabel: String,
        val dominantFlow: String,
        val noteCount: Int
    )

    data class UiState(
        val summary: CycleSummary = CycleSummary(),
        val todayEntry: DailyEntry? = null,
        val lastPeriodSummary: LastPeriodSummary? = null,
        val loading: Boolean = true
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllEntries(),
                repository.getEntryForDate(LocalDate.now())
            ) { allEntries, todayEntry ->
                val summary = engine.buildSummary(allEntries)
                val events = engine.extractPeriodEvents(allEntries)
                val lastPeriodSummary = computeLastPeriodSummary(allEntries, events)
                UiState(
                    summary = summary,
                    todayEntry = todayEntry,
                    lastPeriodSummary = lastPeriodSummary,
                    loading = false
                )
            }.collect { _state.value = it }
        }
    }

    fun togglePeriodToday() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val current = _state.value.todayEntry
            val updated = (current ?: DailyEntry(date = today.toEpochDay())).copy(
                isPeriod = !(current?.isPeriod ?: false),
                flowIntensity = if (current?.isPeriod == false) 3 else 0
            )
            repository.saveEntry(updated)
        }
    }

    private fun computeLastPeriodSummary(
        allEntries: List<DailyEntry>,
        events: List<CyclePredictionEngine.PeriodEvent>
    ): LastPeriodSummary? {
        val today = LocalDate.now()
        // Find the most recent period that has already ended (not today's ongoing period)
        val lastCompleted = events
            .filter { it.end.isBefore(today) }
            .lastOrNull() ?: return null

        val periodEntries = allEntries.filter { e ->
            val d = LocalDate.ofEpochDay(e.date)
            !d.isBefore(lastCompleted.start) && !d.isAfter(lastCompleted.end)
        }

        // Top symptoms
        val counts = mutableMapOf<String, Int>()
        periodEntries.forEach { e ->
            if (e.symptoms.isNotEmpty()) {
                e.symptoms.split(",").forEach { raw ->
                    val name = raw.trim()
                    if (name.isNotEmpty()) counts[name] = (counts[name] ?: 0) + 1
                }
            }
        }
        val topSymptoms = counts.entries
            .sortedByDescending { it.value }
            .take(4)
            .mapNotNull { (name, _) -> Symptom.entries.find { it.name == name }?.label }

        // Average pain
        val avgPain = if (periodEntries.isEmpty()) 0f
        else periodEntries.map { it.painLevel }.average().toFloat()
        val avgPainLabel = when {
            avgPain < 0.5f -> "None"
            avgPain < 1.5f -> "Mild"
            avgPain < 2.5f -> "Moderate"
            avgPain < 3.5f -> "Noticeable"
            avgPain < 4.5f -> "Severe"
            else -> "Very Severe"
        }

        // Dominant flow
        val avgFlow = if (periodEntries.isEmpty()) 0f
        else periodEntries.map { it.flowIntensity }.average().toFloat()
        val dominantFlow = FlowIntensity.entries
            .getOrElse(avgFlow.roundToInt()) { FlowIntensity.MEDIUM }
            .label

        // Date range label
        val fmt = DateTimeFormatter.ofPattern("MMM d")
        val dateRange = "${lastCompleted.start.format(fmt)} – ${lastCompleted.end.format(fmt)}"

        return LastPeriodSummary(
            dateRange = dateRange,
            durationDays = lastCompleted.length,
            topSymptoms = topSymptoms,
            avgPainLabel = avgPainLabel,
            dominantFlow = dominantFlow,
            noteCount = periodEntries.count { it.notes.isNotEmpty() }
        )
    }

}
