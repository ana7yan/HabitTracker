package com.example.habittracker.presentation.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.habittracker.presentation.event.HabitEvent
import com.example.habittracker.presentation.event.UiEvent
import com.example.habittracker.presentation.state.HabitState
import com.example.habittracker.presentation.state.UiState

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitDialog(
    state: HabitState,
    onEvent: (HabitEvent) -> Unit,
    modifier: Modifier = Modifier,
    uiState: UiState,
    onUiEvent: (UiEvent) -> Unit
) {

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(HabitEvent.HideDialog)
        },

        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,

        title = {
            Text(
                text = "Add a Habit",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },

        text = {

            Column {
                OutlinedTextField(
                    value = state.name,
                    maxLines = 1,
                    singleLine = true,
                    onValueChange = { input ->
                        val formatted = input.split(" ").joinToString(" ") { word ->
                            word.lowercase().replaceFirstChar { it.uppercase() }
                        }
                        if (formatted.length <= 30) {
                            onEvent(HabitEvent.SetName(formatted))
                        }
                    },

                    placeholder = {
                        Text(
                            "Habit name",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),

                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
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
                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(
                        checked = state.hasReminder,
                        onCheckedChange = {
                            if(!state.hasReminder && !uiState.areNotificationsOn){
                                onUiEvent(UiEvent.RequestNotificationPermission)
                                onEvent(HabitEvent.CheckReminder)
                            }else {
                                onEvent(HabitEvent.CheckReminder)
                            }
                        },
                        enabled = true
                    )
                    Text(
                        text = "Daily Reminder",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(
                    visible = state.hasReminder
                ) {
                    val timePickerState =
                        rememberTimePickerState(initialHour = state.hour, initialMinute = state.minute, is24Hour = true)
                    TextButton(
                        onClick = {
                            onEvent(HabitEvent.ShowTimePicker)
                        }
                    ) {
                        Text(
                            text = String.format(
                                "Time: %02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (state.isTimePickerVisible) {
                        TimePickerDialog(
                            timePickerState = timePickerState,
                            onEvent = onEvent
                        )
                    }

                }
                state.addingError?.let {
                    Text(text = state.addingError, color = Color.Red)
                }
            }

        },

        confirmButton = {

            Button(
                onClick = { onEvent(HabitEvent.SaveHabit) },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Save",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = { onEvent(HabitEvent.HideDialog) }
            ) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
fun ReminderTimeCard(x0: Any) {
    TODO("Not yet implemented")
}