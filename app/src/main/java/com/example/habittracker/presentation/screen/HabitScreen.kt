package com.example.habittracker.presentation.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.SortType
import com.example.habittracker.HabitId
import com.example.habittracker.presentation.component.AddHabitDialog
import com.example.habittracker.presentation.event.HabitEvent
import com.example.habittracker.presentation.event.UiEvent
import com.example.habittracker.presentation.state.HabitState
import com.example.habittracker.presentation.state.UiState
import com.example.habittracker.presentation.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(
    state: HabitState,
    viewModel: HabitViewModel,
    isLoggedIn: Boolean,
    onEvent: (HabitEvent) -> Unit,
    navController: NavController,
    context: Context,
    uiState: UiState,
    onUiEvent: (UiEvent) -> Unit,
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
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Text("Habit Tracker")
                },
                actions = {
                    IconButton(onClick = {
                        if (isLoggedIn) {
                            navController.navigate("account")
                        } else {
                            navController.navigate("login")
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(HabitEvent.ShowDialog) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add a habit",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->

        if (state.isAddingHabit) {
            AddHabitDialog(
                state = state,
                onEvent = onEvent,
                uiState = uiState,
                onUiEvent = onUiEvent
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            HabitSortLine(state, onEvent)

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "Your Habits",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!uiState.areNotificationsOn) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (state.habits.isEmpty()) {
                    Text(
                        text = "No Habits Yet",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.habits,
                            key = { habit -> habit.id }
                        ) { habit ->
                            SingleHabitBox(habit, onEvent, navController, context)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun HabitSortLine(
    state: HabitState,
    onEvent: (HabitEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Sort by:",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(8.dp))


        SortType.entries.forEach { sortType ->

            Row(
                modifier = Modifier
                    .clickable {
                        onEvent(HabitEvent.SortHabits(sortType))
                    }
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = state.sortType == sortType,
                    onClick = {
                        onEvent(HabitEvent.SortHabits(sortType))
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = sortType.name,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun SingleHabitBox(
    habit: Habit,
    onEvent: (HabitEvent) -> Unit,
    navController: NavController,
    context: Context,
) {
    val swipeToDismissBoxState = remember(habit.id) {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            density = Density(context = context),
            positionalThreshold = { fullWidthPx: Float -> fullWidthPx * 0.25f }, // 25% swipe
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onEvent(HabitEvent.DeleteHabit(habit))
                }
                it != SwipeToDismissBoxValue.EndToStart
            }
        )
    }
    val surfacePadding by animateDpAsState(
        targetValue = if (habit.isCompletedToday) 8.dp else 0.dp,
        animationSpec = tween(500)
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .padding(surfacePadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(15.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (habit.isCompletedToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surface,
            animationSpec = tween(500)
        )

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp,
            color = backgroundColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = surfacePadding)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        navController.navigate(HabitId(habit.id))
                    }
                    .padding(16.dp)
            ) {

                Text(
                    text = habit.streak.toString(),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = habit.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )

                Checkbox(
                    checked = habit.isCompletedToday,
                    onCheckedChange = {
                        onEvent(HabitEvent.CheckOutHabit(habit))
                    },
                    enabled = !habit.isCompletedToday,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
