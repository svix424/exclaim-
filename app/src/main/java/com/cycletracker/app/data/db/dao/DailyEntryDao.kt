package com.cycletracker.app.data.db.dao

import androidx.room.*
import com.cycletracker.app.data.db.entity.DailyEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {

    @Query("SELECT * FROM daily_entries WHERE isPeriod = 1 ORDER BY date ASC")
    fun getPeriodDays(): Flow<List<DailyEntry>>

    @Query("SELECT * FROM daily_entries WHERE date = :epochDay")
    fun getEntryByDate(epochDay: Long): Flow<DailyEntry?>

    @Query(
        "SELECT * FROM daily_entries WHERE date >= :startEpoch AND date <= :endEpoch ORDER BY date ASC"
    )
    fun getEntriesInRange(startEpoch: Long, endEpoch: Long): Flow<List<DailyEntry>>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DailyEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DailyEntry)

    @Query("DELETE FROM daily_entries WHERE date = :epochDay")
    suspend fun deleteByDate(epochDay: Long)
}
