package com.cycletracker.app.data.repository

import com.cycletracker.app.data.db.dao.DailyEntryDao
import com.cycletracker.app.data.db.entity.DailyEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CycleRepository @Inject constructor(
    private val dao: DailyEntryDao
) {
    fun getPeriodDays(): Flow<List<DailyEntry>> = dao.getPeriodDays()

    fun getAllEntries(): Flow<List<DailyEntry>> = dao.getAllEntries()

    fun getEntryForDate(date: LocalDate): Flow<DailyEntry?> =
        dao.getEntryByDate(date.toEpochDay())

    fun getEntriesInRange(start: LocalDate, end: LocalDate): Flow<List<DailyEntry>> =
        dao.getEntriesInRange(start.toEpochDay(), end.toEpochDay())

    suspend fun saveEntry(entry: DailyEntry) = dao.insertOrUpdate(entry)

    suspend fun deleteEntry(date: LocalDate) = dao.deleteByDate(date.toEpochDay())
}
