package com.example.habittracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.presentation.state.HabitDateEvent
import com.example.habittracker.presentation.state.HabitDateState
import com.example.habittracker.presentation.state.HabitEvent
import com.example.habittracker.presentation.state.HabitState
import com.example.habittracker.SortType
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitDate
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository
): ViewModel() {


    private val _todayFull = MutableStateFlow(LocalDate.now())


    private val _sortType = MutableStateFlow(SortType.NAME)

    private val _habits = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.NAME -> repository.getHabitsOrderedByName()
                SortType.STREAK -> repository.getHabitsOrderedByStreak()
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )




    private val _state = MutableStateFlow(HabitState())

    val state = combine(_state, _sortType, _habits) { state, sortType, habits ->
        state.copy(
            habits = habits,
            sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        HabitState()
    )

    private val _dateState = MutableStateFlow(HabitDateState())
    val dateState = _dateState.asStateFlow()


    init {
        checkForNewDay()
        observeDayChange()
    }

    private fun observeDayChange() {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                val now = LocalDate.now()
                if (now != _todayFull.value) {
                    _todayFull.value = now
                    resetHabitsForNewDay(now)
                }
            }
        }
    }


    private fun checkForNewDay() {
        viewModelScope.launch(Dispatchers.IO) {
            val today = LocalDate.now().toEpochDay()
            val lastReset = repository.getLastResetDate()
            Log.d("Prefs", "lastResetDate = ${lastReset.toString()}")
            if (lastReset != today) {
                resetHabitsForNewDay(LocalDate.now())
                repository.saveLastResetDate(today)
            }
        }
    }

    private fun resetHabitsForNewDay(today: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllHabits().forEach { habit ->
                val lastCompleted = habit.lastCompletedDate

                if (lastCompleted == today.minusDays(2).toString()) {
                    repository.updateHabit(habit.copy(
                        isCompletedToday = false,
                        streak = 0
                    ))
                } else {
                    repository.updateHabit(
                        habit.copy(isCompletedToday = false)
                    )
                }
            }
        }
    }

    fun onDateEvent(event: HabitDateEvent) {
        when (event) {
            is HabitDateEvent.ThisMonth -> {
                val currentState = _dateState.value
                val fromDate = LocalDate.of(currentState.year, currentState.month, 1)
                val toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth())
                viewModelScope.launch(Dispatchers.IO) {
                    _dateState.update {
                        it.copy(
                            month = currentState.month,
                            year = currentState.year,
                            today = currentState.today,
                            habitDates = dateRepository.getDatesOfHabitInRange(
                                habitId = event.habitId,
                                fromDate = fromDate,
                                toDate = toDate
                            )
                        )
                    }
                }
            }

            is HabitDateEvent.NextMonth -> {
                val currentState = _dateState.value
                val nextMonth = currentState.month.plus(1)
                val nextYear =
                    if (nextMonth == Month.JANUARY) currentState.year + 1 else currentState.year
                val nextToday =
                    if (nextMonth == LocalDate.now().month && nextYear == LocalDate.now().year) LocalDate.now().dayOfMonth else 0
                val fromDate = LocalDate.of(nextYear, nextMonth, 1)
                val toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth())
                viewModelScope.launch(Dispatchers.IO) {
                    _dateState.update {
                        it.copy(
                            month = nextMonth,
                            year = nextYear,
                            today = nextToday,
                            habitDates = dateRepository.getDatesOfHabitInRange(
                                habitId = event.habitId,
                                fromDate = fromDate,
                                toDate = toDate
                            )
                        )
                    }
                }

            }

            is HabitDateEvent.PreviousMonth -> {
                val currentState = _dateState.value
                val previousMonth = currentState.month.minus(1)
                val previousYear =
                    if (previousMonth == Month.DECEMBER) currentState.year - 1 else currentState.year
                val previousToday =
                    if (previousMonth == LocalDate.now().month && previousYear == LocalDate.now().year) LocalDate.now().dayOfMonth else 0
                val fromDate = LocalDate.of(previousYear, previousMonth, 1)
                val toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth())
                viewModelScope.launch(Dispatchers.IO) {
                    _dateState.update {
                        it.copy(
                            month = previousMonth,
                            year = previousYear,
                            today = previousToday,
                            habitDates = dateRepository.getDatesOfHabitInRange(
                                habitId = event.habitId,
                                fromDate = fromDate,
                                toDate = toDate
                            )
                        )
                    }
                }

            }
        }
    }

    fun onEvent(event: HabitEvent) {
        when (event) {
            is HabitEvent.DeleteHabit -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.deleteHabit(event.habit)
                }
            }

            HabitEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingHabit = false
                    )
                }
            }

            HabitEvent.SaveHabit -> {
                val name = state.value.name
                if (name.isBlank()) {
                    return
                }
                val habit = Habit(
                    name = name,
                    creationDate = LocalDate.now().toString()
                )
                viewModelScope.launch(Dispatchers.IO) {
                    repository.upsertHabit(habit)
                }
                _state.update {
                    it.copy(
                        isAddingHabit = false,
                        name = ""
                    )
                }

            }

            is HabitEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }

            HabitEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingHabit = true
                    )
                }
            }

            is HabitEvent.SortHabits -> {
                _sortType.value = event.sortType
            }

            is HabitEvent.CheckOutHabit -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val streak = event.habit.streak
                    repository.upsertHabit(
                        event.habit.copy(
                        streak = streak + 1,
                        lastCompletedDate = _todayFull.value.toString(),
                        isCompletedToday = true
                    ))
                    dateRepository.upsertDate(
                        habitDate = HabitDate(
                            habitId = event.habit.id,
                            date = _todayFull.value
                        )
                    )
                }
            }
        }
    }

    fun getHabitById(id: Int): Habit? {
        return state.value.habits.find { it.id == id }
    }

     fun getLastSevenDaysFlow(id: Int): Flow<List<LocalDate>> {
        val sevenDaysAgo = LocalDate.now().minusDays(7)
        val today = LocalDate.now()

        return dateRepository.getDatesOfHabitInRangeAsFlow(id,sevenDaysAgo,today)
    }
}