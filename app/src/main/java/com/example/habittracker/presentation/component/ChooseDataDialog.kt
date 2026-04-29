package com.example.habittracker.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habittracker.presentation.event.LoginEvent
import com.example.habittracker.presentation.state.LoginState

@Composable
fun ChooseDataDialog(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState
) {
    AlertDialog(
        modifier = Modifier,
        onDismissRequest = {
        },
        title = {
            Text(
                text = "Choose which data to keep",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        confirmButton = {
            Button(onClick = {
                onEvent(LoginEvent.ChangeLoginState)
                onEvent(LoginEvent.HideDialog)
            }) {
                Text(
                    "Save",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        text = {
            Column {
                Text("Select how you want to handle your habit data:")
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        onEvent(LoginEvent.ChooseOption(0))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        containerColor = if (state.option == 0)
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.background,
                        contentColor = if (state.option == 0)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = Color.LightGray
                    )

                ) {
                    Text("Merge Everywhere")
                }
                OutlinedButton(
                    onClick = {
                        onEvent(LoginEvent.ChooseOption(1))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        containerColor = if (state.option == 1)
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.background,
                        contentColor = if (state.option == 1)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text("Keep Cloud Only")
                }
                OutlinedButton(
                    onClick = {
                        onEvent(LoginEvent.ChooseOption(2))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        containerColor = if (state.option == 2)
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.background,
                        contentColor = if (state.option == 2)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text("Keep This Device Only")
                }
            }
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
