package com.example.domain.domain.usecase



import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.HabitDate
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.example.domain.domain.sceduler.ReminderScheduler
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

class MergeLocalAndRemoteDatabasesUseCase @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val authRepository: UserAuthRepository,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        val uid = authRepository.getCurrentUserId()
        if(uid == null) return
        val localHabits = habitRepository.getAllHabits().associateBy { it.name.replace(" ", "") }
        val remoteHabits = habitRemoteRepository.observeHabits(uid).first().associateBy { it.name.replace(" ", "") }
        val updatedHabits = mutableListOf<Habit>()
        val updatedDates = mutableListOf<HabitDate>()
        localHabits.forEach { localHabitPair ->
            val remoteHabit = remoteHabits[localHabitPair.key]
            val localHabit = localHabitPair.value
            if (remoteHabit != null) {
                val localDates = habitDateRepository.getAllDates(localHabit.id)
                val dates = (localDates + remoteHabit.checkedDates).distinct()
                var streak = 0
                var currentDate = LocalDate.now().minusDays(1)
                while (dates.contains(currentDate.toString())) {
                    streak++
                    currentDate = currentDate.minusDays(1)
                }
                if(dates.contains(LocalDate.now().toString())){
                    streak++
                }
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
                val hasReminder = localHabit.hasReminder || remoteHabit.hasReminder
                val reminderHour = if(localHabit.hasReminder) localHabit.reminderHour else remoteHabit.reminderHour
                val reminderMinute = if(localHabit.hasReminder) localHabit.reminderMinute else remoteHabit.reminderMinute
                if(!localHabit.hasReminder && remoteHabit.hasReminder){
                    reminderScheduler.scheduleReminder(
                        localHabit.copy(
                            hasReminder = true,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute
                        )
                    )
                }
                val habit = Habit(
                    id = localHabit.id,
                    remoteId = remoteHabit.remoteId,
                    name = localHabit.name,
                    streak = streak,
                    lastCompletedDate = lastCompletedDate,
                    isCompletedToday = isCompletedToday,
                    creationDate = creationDate,
                    checkedDates = dates,
                    hasReminder = hasReminder,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute
                )
                updatedHabits.add(habit)
            } else {
                val dates = habitDateRepository.getAllDates(localHabit.id)
                val firebaseHabit = localHabit.copy(
                    checkedDates = dates
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
                val habit = remoteHabit.copy(
                    lastCompletedDate = lastCompetedDates,
                    isCompletedToday = isCompletedToday
                )
                val habitId = habitRepository.insertHabit(habit)
                val dates = remoteHabit.checkedDates.map {
                    HabitDate(habitId = habitId, date = LocalDate.parse(it))
                }
                habitDateRepository.upsertDates(dates)
                if(remoteHabit.hasReminder){
                    reminderScheduler.scheduleReminder(remoteHabit.copy(id = habitId))
                }
            }
        }
        if(!updatedHabits.isEmpty()){
            habitRepository.updateHabits(updatedHabits)
            habitRemoteRepository.updateHabitsInRTDB(uid, updatedHabits)
            habitDateRepository.upsertDates(updatedDates)
        }

    }
}
