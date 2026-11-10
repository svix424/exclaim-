package com.cycletracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycletracker.app.data.repository.CycleRepository
import com.cycletracker.app.domain.CyclePredictionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CycleRepository,
    private val engine: CyclePredictionEngine
) : ViewModel() {

    data class UiState(
        val periodEvents: List<CyclePredictionEngine.PeriodEvent> = emptyList(),
        val avgCycleLength: Int? = null,
        val avgPeriodLength: Int = 5
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                val events = engine.extractPeriodEvents(entries)
                val prediction = engine.predict(entries)
                _state.value = UiState(
                    periodEvents = events,
                    avgCycleLength = prediction?.avgCycleLength,
                    avgPeriodLength = prediction?.avgPeriodLength ?: 5
                )
            }
        }
    }
}
