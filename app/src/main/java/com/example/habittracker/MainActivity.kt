package com.example.habittracker


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.BuildConfig
import com.example.habittracker.presentation.screen.AccountScreen
import com.example.habittracker.presentation.screen.EmailVerificationScreen
import com.example.habittracker.presentation.screen.HabitDateScreen
import com.example.habittracker.presentation.screen.HabitScreen
import com.example.habittracker.presentation.screen.UserLoginScreen
import com.example.habittracker.presentation.screen.UserSignupScreen
import com.example.habittracker.presentation.viewmodel.AuthViewModel
import com.example.habittracker.presentation.viewmodel.HabitViewModel
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.facebook.CallbackManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth: FirebaseAuth
//    val callbackManager = CallbackManager.Factory.create()
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()
    val credentialManager = CredentialManager.create(this)
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
                            navController = navController,
//                            activity = this@MainActivity,
//                            callbackManager = callbackManager,
                            context = this@MainActivity,
                            credentialManager = credentialManager,
                            googleIdOption = googleIdOption,
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
                    composable("verification"){
                        val email = auth.currentUser?.email
                        EmailVerificationScreen(
                            email = email.toString(),
                            onEvent = authViewModel::onVerificationEvent,
                            state = loginState,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
}


@Serializable
data class HabitId(val habitId: Int)
