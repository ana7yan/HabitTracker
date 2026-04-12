package com.example.habittracker.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.presentation.event.HabitDateEvent
import com.example.habittracker.presentation.state.HabitDateState
import com.example.habittracker.presentation.event.HabitEvent
import com.example.habittracker.presentation.state.HabitState
import com.example.habittracker.domain.model.SortType
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.usecase.AddHabitUseCase
import com.example.habittracker.domain.usecase.CheckOutHabitUseCase
import com.example.habittracker.domain.usecase.DeleteHabitUseCase
import com.example.habittracker.domain.usecase.GetAllHabitsUseCase
import com.example.habittracker.domain.usecase.CheckAndResetHabitForNewDayUseCase
import com.example.habittracker.domain.usecase.GetHabitDatesAsFlowUseCase
import com.example.habittracker.domain.usecase.GetHabitDatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel @Inject constructor(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val checkOutHabitUseCase: CheckOutHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val checkAndResetHabitForNewDayUseCase: CheckAndResetHabitForNewDayUseCase,
    private val getHabitDatesUseCase: GetHabitDatesUseCase,
    private val getHabitDatesAsFlowUseCase: GetHabitDatesAsFlowUseCase,
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _habits = _sortType
        .flatMapLatest { sortType ->
            getAllHabitsUseCase(sortType)
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
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val now = LocalDateTime.now()
                val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()

                val delayMillis = Duration.between(now, nextMidnight).toMillis()

                delay(delayMillis)

                checkAndResetHabitForNewDayUseCase()
            }
        }
    }

    fun checkForNewDay() {
        viewModelScope.launch {
            checkAndResetHabitForNewDayUseCase()
        }
    }


    fun onDateEvent(event: HabitDateEvent) {
        when (event) {
            is HabitDateEvent.ThisMonth -> {
                val currentState = _dateState.value
                viewModelScope.launch(Dispatchers.IO) {
                    _dateState.update {
                        it.copy(
                            month = currentState.month,
                            year = currentState.year,
                            today = currentState.today,
                            habitDates = getHabitDatesUseCase(
                                id = event.habitId,
                                fromDate = getMonthRange(
                                    currentState.year,
                                    currentState.month
                                ).first,
                                toDate = getMonthRange(currentState.year, currentState.month).second
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
                viewModelScope.launch(Dispatchers.IO) {
                    _dateState.update {
                        it.copy(
                            month = nextMonth,
                            year = nextYear,
                            today = nextToday,
                            habitDates = getHabitDatesUseCase(
                                id = event.habitId,
                                fromDate = getMonthRange(nextYear, nextMonth).first,
                                toDate = getMonthRange(nextYear, nextMonth).second
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
                viewModelScope.launch(Dispatchers.IO) {
                    _dateState.update {
                        it.copy(
                            month = previousMonth,
                            year = previousYear,
                            today = previousToday,
                            habitDates = getHabitDatesUseCase(
                                id = event.habitId,
                                fromDate = getMonthRange(previousYear, previousMonth).first,
                                toDate = getMonthRange(previousYear, previousMonth).second
                            )
                        )
                    }
                }

            }
        }
    }

    private fun getMonthRange(year: Int, month: Month): Pair<LocalDate, LocalDate> {
        val from = LocalDate.of(year, month, 1)
        val to = from.withDayOfMonth(from.lengthOfMonth())
        return from to to
    }

    fun onEvent(event: HabitEvent) {
        when (event) {
            is HabitEvent.DeleteHabit -> {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteHabitUseCase(event.habit)
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
                    addHabitUseCase(habit)
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
                    checkOutHabitUseCase(
                        habit = event.habit
                    )
                }
            }
        }
    }

    fun getHabitById(id: Int): Habit? {
        return state.value.habits.find { it.id == id }
    }

    fun getLastSevenDaysFlow(id: Int): Flow<List<LocalDate>> {
        return getHabitDatesAsFlowUseCase(id)
    }
}