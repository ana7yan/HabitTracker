package com.example.data.data.mapper

import com.example.data.data.model.FirebaseHabitUnit
import com.example.data.data.model.HabitDateEntity
import com.example.data.data.model.HabitEntity
import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.HabitDate

class HabitMapper {

}
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        remoteId = remoteId,
        name = name,
        streak = streak,
        lastCompletedDate = lastCompletedDate.toString(),
        isCompletedToday = isCompletedToday,
        creationDate = creationDate,
        hasReminder = hasReminder,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}
fun Habit.toData(): HabitEntity {
    return HabitEntity(
        id = id,
        remoteId = remoteId,
        name = name,
        streak = streak,
        lastCompletedDate = lastCompletedDate.toString(),
        isCompletedToday = isCompletedToday,
        creationDate = creationDate,
        hasReminder = hasReminder,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}
fun HabitDateEntity.toDomain(): HabitDate{
    return HabitDate(
        id = id,
        habitId = habitId,
        date = date
    )
}

fun HabitDate.toData(): HabitDateEntity{
    return HabitDateEntity(
        id = id,
        habitId = habitId,
        date = date
    )
}

fun FirebaseHabitUnit.toDomainRemote() : Habit{
    return Habit(
        remoteId = remoteId,
        name = name,
        streak = streak,
        creationDate = creationDate,
        checkedDates = checkedDates,
        hasReminder = hasReminder,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}

fun Habit.toDataRemote(): FirebaseHabitUnit{
    return FirebaseHabitUnit(
        name = name,
        streak = streak,
        remoteId = remoteId.toString(),
        creationDate = creationDate,
        checkedDates = checkedDates,
        hasReminder = hasReminder,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}