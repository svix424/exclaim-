package com.cycletracker.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One row per calendar day. isPeriod marks period days; the prediction engine
 * groups consecutive period days into cycle events.
 */
@Entity(tableName = "daily_entries")
data class DailyEntry(
    @PrimaryKey val date: Long,         // LocalDate.toEpochDay()
    val isPeriod: Boolean = false,
    val flowIntensity: Int = 0,         // 0=None 1=Spotting 2=Light 3=Medium 4=Heavy
    val symptoms: String = "",          // comma-separated Symptom enum names
    val mood: Int = -1,                 // -1=not set, else Mood.ordinal
    val painLevel: Int = 0,             // 0–5
    val notes: String = ""
)
