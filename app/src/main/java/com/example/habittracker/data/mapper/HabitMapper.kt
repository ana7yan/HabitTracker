package com.example.habittracker.data.mapper

import com.example.habittracker.data.model.HabitDateEntity
import com.example.habittracker.data.model.HabitEntity
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitDate

class HabitMapper {

}
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        streak = streak,
        lastCompletedDate = lastCompletedDate.toString(),
        isCompletedToday = isCompletedToday,
        creationDate = creationDate
    )
}
fun Habit.toData(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        streak = streak,
        lastCompletedDate = lastCompletedDate.toString(),
        isCompletedToday = isCompletedToday,
        creationDate = creationDate
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