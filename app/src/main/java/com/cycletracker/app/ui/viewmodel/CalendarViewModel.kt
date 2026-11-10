package com.cycletracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycletracker.app.data.repository.CycleRepository
import com.cycletracker.app.domain.CyclePredictionEngine
import com.cycletracker.app.domain.model.DayInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CycleRepository,
    private val engine: CyclePredictionEngine
) : ViewModel() {

    data class UiState(
        val displayMonth: YearMonth = YearMonth.now(),
        val dayInfoMap: Map<LocalDate, DayInfo> = emptyMap(),
        val selectedDate: LocalDate? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllEntries(),
                _state.map { it.displayMonth }.distinctUntilChanged()
            ) { entries, month ->
                val prediction = engine.predict(entries)
                val dayInfo = engine.buildMonthDayInfo(month.year, month.monthValue, entries, prediction)
                _state.value.copy(dayInfoMap = dayInfo)
            }.collect { _state.value = it }
        }
    }

    fun previousMonth() {
        _state.update { it.copy(displayMonth = it.displayMonth.minusMonths(1)) }
    }

    fun nextMonth() {
        _state.update { it.copy(displayMonth = it.displayMonth.plusMonths(1)) }
    }

    fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = if (it.selectedDate == date) null else date) }
    }
}
