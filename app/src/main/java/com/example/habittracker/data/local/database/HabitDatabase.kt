package com.example.habittracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.habittracker.data.model.HabitDateEntity
import com.example.habittracker.data.model.HabitEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Database(entities = [HabitEntity::class, HabitDateEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao
    abstract val dateDao: HabitDateDao
}

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromTimestampToDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.format(formatter)
    }
}