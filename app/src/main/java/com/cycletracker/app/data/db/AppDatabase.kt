package com.cycletracker.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cycletracker.app.data.db.dao.DailyEntryDao
import com.cycletracker.app.data.db.entity.DailyEntry

@Database(
    entities = [DailyEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyEntryDao(): DailyEntryDao
}
