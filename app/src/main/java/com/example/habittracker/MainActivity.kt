package com.example.habittracker


import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.example.habittracker.data.local.HabitDatabase
import com.example.habittracker.presentation.screen.HabitDateScreen
import com.example.habittracker.presentation.screen.HabitScreen
import com.example.habittracker.presentation.viewmodel.HabitViewModel
import com.example.habittracker.ui.theme.HabitTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /*private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            HabitDatabase::class.java,
            "habits.db"
        ).build()
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                val navController = rememberNavController()
                val prefs = PreferencesManager(applicationContext)
                val viewModel = hiltViewModel<HabitViewModel>()
                /*val viewModel by viewModels<HabitViewModel>(
                    factoryProducer = {
                        object : ViewModelProvider.Factory{
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return HabitViewModel(db.habitDao, db.dateDao, prefs) as T
                            }
                        }
                    }
                )*/
                val state by viewModel.state.collectAsState()
                val dateState by viewModel.dateState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable(route = "main") {
                        HabitScreen(
                            state = state,
                            onEvent = viewModel::onEvent,
                            navController = navController,
                            applicationContext
                        )
                    }
                    composable<HabitId> { backStackEntry ->
                        val habit: HabitId = backStackEntry.toRoute()
                        HabitDateScreen(
                            state = dateState,
                            habitId = habit.habitId,
                            navController = navController,
                            viewModel = viewModel,
                            onEvent = viewModel::onDateEvent
                        )
                    }
                }
            }
        }
    }
}

@Serializable
data class HabitId(val habitId: Int)
