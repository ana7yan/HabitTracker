package com.example.habittracker.presentation.viewmodel


import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.usecase.DownloadHabitsFromFirebaseUseCase
import com.example.habittracker.domain.usecase.LogInUseCase
import com.example.habittracker.domain.usecase.LogOutUseCase
import com.example.habittracker.domain.usecase.SignUpUseCase
import com.example.habittracker.presentation.event.AccountEvent
import com.example.habittracker.presentation.event.LoginEvent
import com.example.habittracker.presentation.event.RegisterEvent
import com.example.habittracker.presentation.state.LoginState
import com.example.habittracker.presentation.state.RegisterState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val logInUseCase: LogInUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val downloadHabitsFromFirebaseUseCase: DownloadHabitsFromFirebaseUseCase,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        LoginState()
    )

    fun onLoginEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.InputEmail -> {
                _loginState.update {
                    it.copy(
                        email = event.email
                    )
                }
            }

            is LoginEvent.InputPassword -> {
                _loginState.update {
                    it.copy(
                        password = event.password
                    )
                }
            }

            LoginEvent.Login -> {
                val email = loginState.value.email
                val password = loginState.value.password
                if (email.isBlank() || password.isBlank()) {
                    return
                }
                viewModelScope.launch(Dispatchers.IO) {
                    val result = logInUseCase(email, password)

                    result.onSuccess { user ->
                        _loginState.update {
                            it.copy(
                                isLoggedIn = true,
                                userName = user?.userName
                            )
                        }
                        downloadHabitsFromFirebaseUseCase()
                    }.onFailure { exception ->
                        _loginState.update {
                            it.copy(
                                error = exception.message ?: "Unknown Error"
                            )
                        }
                    }
                }
            }
        }
    }

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        RegisterState()
    )

    fun onRegisterEvent(event: RegisterEvent) {
        when (event) {
            RegisterEvent.Register -> {
                val email = registerState.value.email
                val userName = registerState.value.userName
                val password = registerState.value.password
                if (email.isBlank() || userName.isBlank() || password.isBlank()) {
                    return
                }
                viewModelScope.launch {
                    val result = signUpUseCase(email, userName, password)

                    result.onSuccess {
                        _registerState.update { it.copy(isRegistered = true) }
                        _loginState.update {
                            it.copy(
                                isLoggedIn = true
                            )
                        }
                    }
                        .onFailure { exception ->
                            _registerState.update {
                                it.copy(
                                    error = exception.message ?: "Unknown Error"
                                )
                            }
                        }
                }
            }

            is RegisterEvent.SetEmail -> {
                _registerState.update {
                    it.copy(
                        email = event.email
                    )
                }
            }

            is RegisterEvent.SetPassword -> {
                _registerState.update {
                    it.copy(
                        password = event.password
                    )
                }
            }

            is RegisterEvent.SetUserName -> {
                _registerState.update {
                    it.copy(
                        userName = event.userName
                    )
                }
            }
        }
    }

    fun onAccountEvent(event: AccountEvent) {
        when (event) {
            AccountEvent.LogOut -> {
                viewModelScope.launch {
                    logOutUseCase()
                    val isLoggedIn = auth.currentUser != null
                    _loginState.update {
                        it.copy(
                            isLoggedIn = isLoggedIn,
                            password = "",
                            userName = ""
                        )
                    }
                }
            }
        }
    }
}