package com.cycletracker.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycletracker.app.data.db.entity.DailyEntry
import com.cycletracker.app.data.repository.CycleRepository
import com.cycletracker.app.domain.model.FlowIntensity
import com.cycletracker.app.domain.model.Mood
import com.cycletracker.app.domain.model.Symptom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogDayViewModel @Inject constructor(
    private val repository: CycleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val date: LocalDate = LocalDate.ofEpochDay(
        savedStateHandle.get<Long>("epochDay") ?: LocalDate.now().toEpochDay()
    )

    data class UiState(
        val isPeriod: Boolean = false,
        val flow: FlowIntensity = FlowIntensity.NONE,
        val selectedSymptoms: Set<Symptom> = emptySet(),
        val mood: Mood? = null,
        val painLevel: Int = 0,
        val notes: String = "",
        val saved: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getEntryForDate(date)
                .filterNotNull()
                .take(1)
                .collect { entry ->
                    _state.value = UiState(
                        isPeriod = entry.isPeriod,
                        flow = FlowIntensity.entries.getOrElse(entry.flowIntensity) { FlowIntensity.NONE },
                        selectedSymptoms = entry.symptoms
                            .split(",")
                            .mapNotNull { name ->
                                runCatching { Symptom.valueOf(name) }.getOrNull()
                            }
                            .toSet(),
                        mood = if (entry.mood >= 0) Mood.entries.getOrNull(entry.mood) else null,
                        painLevel = entry.painLevel,
                        notes = entry.notes
                    )
                }
        }
    }

    fun togglePeriod() = _state.update {
        val newIsPeriod = !it.isPeriod
        it.copy(
            isPeriod = newIsPeriod,
            flow = if (newIsPeriod && it.flow == FlowIntensity.NONE) FlowIntensity.MEDIUM else it.flow
        )
    }

    fun setFlow(flow: FlowIntensity) = _state.update {
        it.copy(flow = flow, isPeriod = flow != FlowIntensity.NONE || it.isPeriod)
    }

    fun toggleSymptom(symptom: Symptom) = _state.update {
        val updated = if (symptom in it.selectedSymptoms)
            it.selectedSymptoms - symptom else it.selectedSymptoms + symptom
        it.copy(selectedSymptoms = updated)
    }

    fun setMood(mood: Mood) = _state.update {
        it.copy(mood = if (it.mood == mood) null else mood)
    }

    fun setPainLevel(level: Int) = _state.update { it.copy(painLevel = level) }

    fun setNotes(text: String) = _state.update { it.copy(notes = text) }

    fun save() {
        viewModelScope.launch {
            val s = _state.value
            val entry = DailyEntry(
                date = date.toEpochDay(),
                isPeriod = s.isPeriod,
                flowIntensity = s.flow.ordinal,
                symptoms = s.selectedSymptoms.joinToString(",") { it.name },
                mood = s.mood?.ordinal ?: -1,
                painLevel = s.painLevel,
                notes = s.notes
            )
            repository.saveEntry(entry)
            _state.update { it.copy(saved = true) }
        }
    }
}
