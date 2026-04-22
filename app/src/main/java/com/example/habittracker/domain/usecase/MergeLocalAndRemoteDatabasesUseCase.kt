package com.example.habittracker.domain.usecase


import com.example.habittracker.data.model.FirebaseHabitUnit
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitDate
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class MergeLocalAndRemoteDatabasesUseCase @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val auth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository,
) {
    suspend operator fun invoke() {
        /*val uid = auth.currentUser?.uid ?: return
        val localHabits = habitRepository.getAllHabits()
        val remoteHabits = habitRemoteRepository.observeHabits(uid).first()
        localHabits.forEach { localHabit ->
            remoteHabits.forEach { remoteHabit ->
                if(remoteHabit.name == localHabit.name){
                    val streak = if(remoteHabit.streak > localHabit.streak) remoteHabit.streak else localHabit.streak
                    val localDates = habitDateRepository.getAllDates(localHabit.id)
                    val dates = (localDates + remoteHabit.checkedDates).distinct()
                    val lastCompletedDate = dates.maxOfOrNull {
                        LocalDate.parse(it)
                    }.toString()
                    val isCompletedToday = dates.contains(LocalDate.now().toString())
                    val creationDate =
                        if(
                            LocalDate.parse(remoteHabit.creationDate) > LocalDate.parse(localHabit.creationDate)
                            ) remoteHabit.creationDate
                        else localHabit.creationDate

                    val habit = Habit(
                        id = localHabit.id,
                        remoteId = remoteHabit.remoteId,
                        name = localHabit.name,
                        streak = streak,
                        lastCompletedDate = lastCompletedDate,
                        isCompletedToday = isCompletedToday,
                        creationDate = creationDate,
                        checkedDates = dates
                    )
                    habitRepository.updateHabit(habit)
                    habitRemoteRepository.updateHabitInDB(uid,remoteHabit.remoteId,streak,dates)
                }
            }
        }*/
        val uid = auth.currentUser?.uid ?: return
        val localHabits = habitRepository.getAllHabits().associateBy { it.name }
        val remoteHabits = habitRemoteRepository.observeHabits(uid).first().associateBy { it.name }
        val updatedHabits = mutableListOf<Habit>()
        val updatedDates = mutableListOf<HabitDate>()
        localHabits.forEach { localHabitPair ->
            val remoteHabit = remoteHabits[localHabitPair.key]
            val localHabit = localHabitPair.value
            if (remoteHabit != null) {
                val streak =
                    if (remoteHabit.streak > localHabit.streak) remoteHabit.streak else localHabit.streak
                val localDates = habitDateRepository.getAllDates(localHabit.id)
                val dates = (localDates + remoteHabit.checkedDates).distinct()
                val newDates = (dates - localDates)
                newDates.forEach {
                    updatedDates.add(HabitDate(habitId = localHabit.id, date =  LocalDate.parse(it)))
                }
                val lastCompletedDate = dates.maxOfOrNull {
                    LocalDate.parse(it)
                }?.toString() ?: ""
                val isCompletedToday = dates.contains(LocalDate.now().toString())
                val creationDate =
                    if (
                        LocalDate.parse(remoteHabit.creationDate) < LocalDate.parse(localHabit.creationDate)
                    ) remoteHabit.creationDate
                    else localHabit.creationDate

                val habit = Habit(
                    id = localHabit.id,
                    remoteId = remoteHabit.remoteId,
                    name = localHabitPair.key,
                    streak = streak,
                    lastCompletedDate = lastCompletedDate,
                    isCompletedToday = isCompletedToday,
                    creationDate = creationDate,
                    checkedDates = dates
                )
                updatedHabits.add(habit)
            } else {
                val dates = habitDateRepository.getAllDates(localHabit.id)
                val firebaseHabit = FirebaseHabitUnit(
                    name = localHabitPair.key,
                    streak = localHabit.streak,
                    creationDate = localHabit.creationDate,
                    checkedDates = dates,
                )
                val remoteId = habitRemoteRepository.addHabitToDB(uid,firebaseHabit)
                habitRepository.updateHabit(localHabit.copy(remoteId = remoteId))

            }
        }
        remoteHabits.forEach { remoteHabitPair ->
            val localHabit = localHabits[remoteHabitPair.key]
            val remoteHabit = remoteHabitPair.value
            if(localHabit == null) {
                val lastCompetedDates = remoteHabit.checkedDates.maxOfOrNull { LocalDate.parse(it) }.toString()
                val isCompletedToday = remoteHabit.checkedDates.contains(LocalDate.now().toString())
                val habit = Habit(
                    remoteId = remoteHabit.remoteId,
                    name = remoteHabit.name,
                    streak = remoteHabit.streak,
                    lastCompletedDate = lastCompetedDates,
                    isCompletedToday = isCompletedToday,
                    creationDate = remoteHabit.creationDate,
                    checkedDates = remoteHabit.checkedDates
                )
                val habitId = habitRepository.insertHabit(habit)
                val dates = remoteHabit.checkedDates.map {
                    HabitDate(habitId = habitId, date = LocalDate.parse(it))
                }
                habitDateRepository.upsertDates(dates)
            }
        }
        if(!updatedHabits.isEmpty()){
            habitRepository.updateHabits(updatedHabits)
            habitRemoteRepository.updateHabitsInRTDB(uid, updatedHabits)
            habitDateRepository.upsertDates(updatedDates)
        }

    }
}
