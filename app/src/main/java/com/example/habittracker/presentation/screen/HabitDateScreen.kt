package com.example.habittracker.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.habittracker.R
import com.example.habittracker.presentation.event.HabitDateEvent
import com.example.habittracker.presentation.event.UiEvent
import com.example.habittracker.presentation.state.HabitDateState
import com.example.habittracker.presentation.state.UiState
import com.example.habittracker.presentation.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDateScreen(
    state: HabitDateState,
    context : Context,
    habitId: Int,
    navController: NavController,
    viewModel: HabitViewModel,
    onEvent: (HabitDateEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState
) {
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UiEvent.RequestNotificationPermission -> {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
    LaunchedEffect(habitId) {
        onEvent(HabitDateEvent.LoadHabit(habitId))
    }

    Scaffold { padding ->

        val habit = viewModel.getHabitById(habitId)

        if (habit != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {

                    IconButton(onClick = {
                        navController.navigate("main")
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }

                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Created in ${habit.creationDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Last 7 days",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                val lastSevenDaysCompleted =
                    viewModel.getLastSevenDaysFlow(habitId)
                        .collectAsState(initial = emptyList())

                val lastSevenDays = remember {
                    (0..6).map { LocalDate.now().minusDays(it.toLong()) }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = true,
                    modifier = Modifier.padding(16.dp)
                ) {

                    items(lastSevenDays) { date ->

                        val isCompleted =
                            lastSevenDaysCompleted.value.contains(date)

                        DayBox(
                            date = date,
                            isCompleted = isCompleted
                        )
                    }
                }

                SimpleCalendar(
                    state.month,
                    state.year,
                    state.today,
                    state.habitDates,
                    onEvent,
                    habitId
                )

                if (state.hasReminder) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Reminder",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(
                                    "Time: %02d:%02d",
                                    state.reminderHour,
                                    state.reminderMinute
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            onEvent(HabitDateEvent.DeleteReminder(habitId))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_delete_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    if(!uiState.areNotificationsOn){
                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("Turn on Notifications.")
                            }
                            append("This reminder will not be sent!")
                        }
                        TextButton(
                            onClick = {
                                onUiEvent(UiEvent.RequestNotificationPermission)
                            }
                        ) {
                            Text(text = annotatedString)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                enabled = true,
                                onClick = {
                                    onEvent(HabitDateEvent.OpenTimePicker)
                                }
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            Text(
                                text = "Set Reminder",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                    }
                    if(!uiState.areNotificationsOn){
                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("Turn on Notifications")
                            }
                            append(" for receiving reminders!")
                        }
                        TextButton(
                            onClick = {
                                onUiEvent(UiEvent.RequestNotificationPermission)
                            }
                        ) {
                            Text(text = annotatedString)
                        }
                    }
                }
                if (state.isTimePickerVisible) {
                    val timePickerState =
                        rememberTimePickerState(initialHour = state.reminderHour, initialMinute = state.reminderMinute, is24Hour = true)
                    Dialog(onDismissRequest = { onEvent(HabitDateEvent.CloseTimePicker)}) {
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TimePicker(state = timePickerState)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { onEvent(HabitDateEvent.CloseTimePicker) }) {
                                        Text("Dismiss")
                                    }
                                    TextButton(onClick = {
                                        onEvent(HabitDateEvent.SaveReminder(habitId = habitId, reminderHour = timePickerState.hour, reminderMinute =  timePickerState.minute))
                                    }) {
                                        Text("Confirm")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleCalendar(
    month: Month,
    year: Int,
    today: Int,
    habitDates: List<LocalDate>,
    onEvent: (HabitDateEvent) -> Unit,
    habitId: Int,
) {

    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = {
                onEvent(HabitDateEvent.PreviousMonth(habitId))
            }) {
                Icon(Icons.Default.KeyboardArrowLeft, null)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                onEvent(HabitDateEvent.NextMonth(habitId))
            }) {
                Icon(Icons.Default.KeyboardArrowRight, null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            daysOfWeek.forEach { day ->

                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalDays = LocalDate.of(year, month, 1).lengthOfMonth()
        val firstDayOffset =
            LocalDate.of(year, month, 1).dayOfWeek.value - 1

        var dayCounter = 1

        for (row in 0 until 6) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                for (col in 0 until 7) {

                    if (row == 0 && col < firstDayOffset || dayCounter > totalDays) {

                        Box(modifier = Modifier.size(36.dp))

                    } else {

                        val date = LocalDate.of(year, month, dayCounter)

                        val isCompleted = habitDates.contains(date)

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isCompleted)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (dayCounter == today) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = dayCounter.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCompleted)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }

                        dayCounter++
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun DayBox(
    date: LocalDate,
    isCompleted: Boolean,
) {

    val dayName =
        date.dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )

    val dayNumber = date.dayOfMonth.toString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .height(72.dp)
            .background(
                if (isCompleted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 6.dp)
    ) {

        Text(
            text = dayName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isCompleted)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = dayNumber,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCompleted)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}