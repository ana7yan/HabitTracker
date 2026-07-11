package com.example.habittracker.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.SortType
import com.example.domain.domain.sceduler.NotificationPermissionChecker
import com.example.domain.domain.usecase.AddHabitUseCase
import com.example.domain.domain.usecase.AddReminderUseCase
import com.example.domain.domain.usecase.CheckAndResetHabitForNewDayUseCase
import com.example.domain.domain.usecase.CheckOutHabitUseCase
import com.example.domain.domain.usecase.DeleteHabitUseCase
import com.example.domain.domain.usecase.DeleteReminderUseCase
import com.example.domain.domain.usecase.GetAllHabitsUseCase
import com.example.domain.domain.usecase.GetHabitDatesAsFlowUseCase
import com.example.domain.domain.usecase.GetHabitDatesUseCase
import com.example.habittracker.presentation.event.HabitDateEvent
import com.example.habittracker.presentation.event.HabitEvent
import com.example.habittracker.presentation.event.UiEvent
import com.example.habittracker.presentation.state.HabitDateState
import com.example.habittracker.presentation.state.HabitState
import com.example.habittracker.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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
    private val addReminderUseCase: AddReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val notificationPermissionChecker: NotificationPermissionChecker,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
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
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()


    init {
        _uiState.update {
            it.copy(
                areNotificationsOn = notificationPermissionChecker.areNotificationsEnabled()
            )
        }
        observeDayChange()
    }

    private fun observeDayChange() {
        viewModelScope.launch(Dispatchers.IO) {
            checkAndResetHabitForNewDayUseCase()
            while (isActive) {
                val now = LocalDateTime.now()
                val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()

                val delayMillis = Duration.between(now, nextMidnight).toMillis()

                delay(delayMillis)

                checkAndResetHabitForNewDayUseCase()
            }
        }
    }
    fun onUiEvent(event: UiEvent) {
        when (event) {
            UiEvent.RequestNotificationPermission -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.RequestNotificationPermission)
                }
            }
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        if (granted) {
            _uiState.update {
                it.copy(
                    areNotificationsOn = true
                )
            }
        }
    }

    fun onDateEvent(event: HabitDateEvent) {
        when (event) {
            is HabitDateEvent.LoadHabit -> {
                viewModelScope.launch {
                    val habit = getHabitById(event.habitId) ?: return@launch
                    _dateState.update {
                        it.copy(
                            hasReminder = habit.hasReminder,
                            reminderHour = habit.reminderHour,
                            reminderMinute = habit.reminderMinute
                        )
                    }
                }
            }

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

            HabitDateEvent.CloseTimePicker -> {
                _dateState.update {
                    it.copy(
                        isTimePickerVisible = false
                    )
                }
            }

            HabitDateEvent.OpenTimePicker -> {
                _dateState.update {
                    it.copy(
                        isTimePickerVisible = true
                    )
                }
            }

            is HabitDateEvent.DeleteReminder -> {
                _dateState.update {
                    it.copy(
                        hasReminder = false,
                        reminderHour = 0,
                        reminderMinute = 0
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    deleteReminderUseCase(event.habitId)
                }

            }

            is HabitDateEvent.SaveReminder -> {
                _dateState.update {
                    it.copy(
                        isTimePickerVisible = false,
                        hasReminder = true,
                        reminderHour = event.reminderHour,
                        reminderMinute = event.reminderMinute
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    addReminderUseCase(event.habitId, event.reminderHour, event.reminderMinute)
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
                        isAddingHabit = false,
                        name = "",
                        addingError = null,
                        hasReminder = false,
                        hour = 0,
                        minute = 0
                    )
                }
            }

            HabitEvent.SaveHabit -> {
                val name = state.value.name
                if (name.isBlank()) {
                    return
                }
                val hasReminder = state.value.hasReminder
                val hour = state.value.hour
                val minute = state.value.minute
                val habit = Habit(
                    name = name,
                    creationDate = LocalDate.now().toString(),
                    hasReminder = hasReminder,
                    reminderHour = hour,
                    reminderMinute = minute
                )
                viewModelScope.launch(Dispatchers.IO) {
                    var doesContainName = false
                    state.value.habits.forEach { habit ->
                        if (habit.name == name) {
                            doesContainName = true
                        }
                    }
                    if (doesContainName) {
                        _state.update {
                            it.copy(
                                addingError = "That habit already exists"
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isAddingHabit = false,
                                name = "",
                                addingError = null,
                                hasReminder = false,
                                hour = 0,
                                minute = 0
                            )
                        }
                        addHabitUseCase(habit)
                    }


                }

            }

            is HabitEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }

            HabitEvent.CheckReminder -> {
                if(notificationPermissionChecker.areNotificationsEnabled()){
                    _state.update {
                        it.copy(
                            hasReminder = !it.hasReminder
                        )
                    }
                }else{
                    _uiState.update {
                        it.copy(
                            areNotificationsOn = false
                        )
                    }
                    viewModelScope.launch {
                        _uiEvent.emit(UiEvent.RequestNotificationPermission)
                    }
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

            HabitEvent.HideTimePicker -> {
                _state.update {
                    it.copy(
                        isTimePickerVisible = false
                    )
                }
            }

            is HabitEvent.SaveReminder -> {
                _state.update {
                    it.copy(
                        isTimePickerVisible = false,
                        hour = event.hour,
                        minute = event.minute
                    )
                }
            }

            HabitEvent.ShowTimePicker -> {
                _state.update {
                    it.copy(
                        isTimePickerVisible = true
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