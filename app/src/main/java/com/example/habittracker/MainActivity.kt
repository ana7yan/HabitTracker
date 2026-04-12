package com.example.habittracker


import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.habittracker.presentation.screen.AccountScreen
import com.example.habittracker.presentation.screen.HabitDateScreen
import com.example.habittracker.presentation.screen.HabitScreen
import com.example.habittracker.presentation.screen.UserLoginScreen
import com.example.habittracker.presentation.screen.UserSignupScreen
import com.example.habittracker.presentation.viewmodel.AuthViewModel
import com.example.habittracker.presentation.viewmodel.HabitViewModel
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HabitTrackerTheme {

                val navController = rememberNavController()

                val viewModel = hiltViewModel<HabitViewModel>()
                val state by viewModel.state.collectAsState()
                val dateState by viewModel.dateState.collectAsState()


                val authViewModel = hiltViewModel<AuthViewModel>()
                val loginState by authViewModel.loginState.collectAsState()
                val registerState by authViewModel.registerState.collectAsState()


                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable(route = "main") {
                        val isLoggedIn = auth.currentUser != null
                        HabitScreen(
                            state = state,
                            onEvent = viewModel::onEvent,
                            navController = navController,
                            context = applicationContext,
                            isLoggedIn = isLoggedIn
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
                    composable(route = "login") {
                        UserLoginScreen(
                            state = loginState,
                            onEvent = authViewModel::onLoginEvent,
                            navController = navController
                        )
                    }
                    composable(route = "register") {
                        UserSignupScreen(
                            state = registerState,
                            onEvent = authViewModel::onRegisterEvent,
                            navController = navController
                        )
                    }
                    composable ("account"){
                        val email = auth.currentUser?.email
                        val userName = auth.currentUser?.displayName
                        AccountScreen(
                            onEvent = authViewModel::onAccountEvent,
                            email = email.toString(),
                            username = userName.toString(),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}


@Serializable
data class HabitId(val habitId: Int)
