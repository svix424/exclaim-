package com.cycletracker.app.domain

import com.cycletracker.app.data.db.entity.DailyEntry
import com.cycletracker.app.domain.model.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

// Reference new moon: 6 January 2000 (astronomically verified)
private val REF_NEW_MOON = LocalDate.of(2000, 1, 6)
private const val LUNAR_CYCLE = 29.53059

internal fun moonPhaseForDate(date: LocalDate): MoonPhase {
    val days = ChronoUnit.DAYS.between(REF_NEW_MOON, date).toDouble()
    val phase = ((days % LUNAR_CYCLE) + LUNAR_CYCLE) % LUNAR_CYCLE
    return when {
        phase < 1.85  -> MoonPhase.NEW
        phase < 7.38  -> MoonPhase.WAXING_CRESCENT
        phase < 9.22  -> MoonPhase.FIRST_QUARTER
        phase < 14.77 -> MoonPhase.WAXING_GIBBOUS
        phase < 16.61 -> MoonPhase.FULL
        phase < 22.15 -> MoonPhase.WANING_GIBBOUS
        phase < 23.99 -> MoonPhase.LAST_QUARTER
        else          -> MoonPhase.WANING_CRESCENT
    }
}

@Singleton
class CyclePredictionEngine @Inject constructor() {

    companion object {
        const val DEFAULT_CYCLE = 28
        const val DEFAULT_PERIOD = 5
        const val LUTEAL_LENGTH = 14
        // Days gap before a new period event is recognised
        const val PERIOD_GAP_DAYS = 3L
    }

    data class PeriodEvent(val start: LocalDate, val end: LocalDate) {
        val length: Int get() = ChronoUnit.DAYS.between(start, end).toInt() + 1
    }

    data class Prediction(
        val nextPeriodStart: LocalDate,
        val nextPeriodEnd: LocalDate,
        val ovulationDate: LocalDate,
        val fertileStart: LocalDate,
        val fertileEnd: LocalDate,
        val avgCycleLength: Int,
        val avgPeriodLength: Int,
        val confidence: Confidence
    )

    /** Groups consecutive period-marked days into discrete period events. */
    fun extractPeriodEvents(entries: List<DailyEntry>): List<PeriodEvent> {
        val days = entries
            .filter { it.isPeriod }
            .map { LocalDate.ofEpochDay(it.date) }
            .sorted()

        if (days.isEmpty()) return emptyList()

        val events = mutableListOf<PeriodEvent>()
        var start = days.first()
        var prev = days.first()

        for (day in days.drop(1)) {
            if (ChronoUnit.DAYS.between(prev, day) > PERIOD_GAP_DAYS) {
                events += PeriodEvent(start, prev)
                start = day
            }
            prev = day
        }
        events += PeriodEvent(start, prev)
        return events
    }

    fun predict(entries: List<DailyEntry>): Prediction? {
        val events = extractPeriodEvents(entries)
        if (events.isEmpty()) return null

        val cycleLengths = events
            .zipWithNext { a, b -> ChronoUnit.DAYS.between(a.start, b.start).toInt() }
            .filter { it in 15..90 }

        val avgCycle = if (cycleLengths.isEmpty()) DEFAULT_CYCLE
        else cycleLengths.takeLast(6).average().roundToInt().coerceIn(15, 90)

        val avgPeriod = events
            .map { it.length }
            .filter { it in 1..14 }
            .let { if (it.isEmpty()) DEFAULT_PERIOD else it.average().roundToInt() }

        val lastStart = events.last().start
        val nextStart = lastStart.plusDays(avgCycle.toLong())
        val nextEnd = nextStart.plusDays((avgPeriod - 1).toLong())
        val ovulation = nextStart.minusDays(LUTEAL_LENGTH.toLong())

        val confidence = when {
            cycleLengths.size >= 3 -> Confidence.HIGH
            cycleLengths.size >= 1 -> Confidence.MEDIUM
            else -> Confidence.LOW
        }

        return Prediction(
            nextPeriodStart = nextStart,
            nextPeriodEnd = nextEnd,
            ovulationDate = ovulation,
            fertileStart = ovulation.minusDays(5),
            fertileEnd = ovulation.plusDays(1),
            avgCycleLength = avgCycle,
            avgPeriodLength = avgPeriod,
            confidence = confidence
        )
    }

    fun buildSummary(entries: List<DailyEntry>): CycleSummary {
        val prediction = predict(entries) ?: return CycleSummary()
        val events = extractPeriodEvents(entries)
        val today = LocalDate.now()

        val todayIsPeriod = entries.any { it.date == today.toEpochDay() && it.isPeriod }
        val lastStart = events.lastOrNull()?.start
        val dayOfCycle = lastStart?.let {
            ChronoUnit.DAYS.between(it, today).toInt() + 1
        }

        val daysUntil = ChronoUnit.DAYS.between(today, prediction.nextPeriodStart).toInt()

        val phase = when {
            todayIsPeriod -> CyclePhase.MENSTRUAL
            lastStart == null -> CyclePhase.UNKNOWN
            else -> {
                val doc = dayOfCycle ?: 0
                val avgCycle = prediction.avgCycleLength
                val avgPeriod = prediction.avgPeriodLength
                when {
                    doc <= avgPeriod -> CyclePhase.MENSTRUAL
                    doc <= avgCycle - LUTEAL_LENGTH - 2 -> CyclePhase.FOLLICULAR
                    doc <= avgCycle - LUTEAL_LENGTH + 2 -> CyclePhase.OVULATION
                    doc <= avgCycle -> CyclePhase.LUTEAL
                    else -> CyclePhase.LATE
                }
            }
        }

        return CycleSummary(
            phase = phase,
            dayOfCycle = dayOfCycle,
            daysUntilNextPeriod = daysUntil,
            averageCycleLength = prediction.avgCycleLength,
            averagePeriodLength = prediction.avgPeriodLength,
            nextPeriodDate = prediction.nextPeriodStart,
            ovulationDate = prediction.ovulationDate,
            confidence = prediction.confidence
        )
    }

    /** Produces DayInfo for every day in a given year-month for the calendar view. */
    fun buildMonthDayInfo(
        year: Int,
        month: Int,
        entries: List<DailyEntry>,
        prediction: Prediction?
    ): Map<LocalDate, DayInfo> {
        val result = mutableMapOf<LocalDate, DayInfo>()
        val start = LocalDate.of(year, month, 1)
        val end = start.withDayOfMonth(start.lengthOfMonth())
        val today = LocalDate.now()

        val periodDays = entries.filter { it.isPeriod }.map { LocalDate.ofEpochDay(it.date) }.toSet()
        val loggedDays = entries.filter {
            it.symptoms.isNotEmpty() || it.notes.isNotEmpty() || it.mood >= 0
        }.map { LocalDate.ofEpochDay(it.date) }.toSet()

        var day = start
        while (!day.isAfter(end)) {
            val isPredictedPeriod = prediction != null &&
                    !day.isBefore(prediction.nextPeriodStart) &&
                    !day.isAfter(prediction.nextPeriodEnd)
            val isFertile = prediction != null &&
                    !day.isBefore(prediction.fertileStart) &&
                    !day.isAfter(prediction.fertileEnd)
            val isOvulation = prediction?.ovulationDate == day

            result[day] = DayInfo(
                isPeriod = day in periodDays,
                isPredictedPeriod = isPredictedPeriod,
                isFertileWindow = isFertile && !isOvulation,
                isOvulation = isOvulation,
                isToday = day == today,
                hasLog = day in loggedDays,
                moonPhase = moonPhaseForDate(day)
            )
            day = day.plusDays(1)
        }
        return result
    }
}
