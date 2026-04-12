package com.example.habittracker.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittracker.presentation.event.HabitEvent
import com.example.habittracker.presentation.state.HabitState

@Composable
fun AddHabitDialog(
    state: HabitState,
    onEvent: (HabitEvent) -> Unit,
    modifier: Modifier = Modifier
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