package com.example.habittracker.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.model.User
import com.example.domain.domain.usecase.CheckIfVerifiedUseCase
import com.example.domain.domain.usecase.GetAllHabitsUseCase
import com.example.domain.domain.usecase.GetAllRemoteHabitsUseCase
import com.example.domain.domain.usecase.LogInUseCase
import com.example.domain.domain.usecase.LogInViaFacebookUseCase
import com.example.domain.domain.usecase.LogOutUseCase
import com.example.domain.domain.usecase.LoginViaGoogleUseCase
import com.example.domain.domain.usecase.MergeLocalAndRemoteDatabasesUseCase
import com.example.domain.domain.usecase.SignUpUseCase
import com.example.domain.domain.usecase.SyncLocalToRemoteUseCase
import com.example.domain.domain.usecase.SyncRemoteToLocalUseCase
import com.example.habittracker.presentation.event.AccountEvent
import com.example.habittracker.presentation.event.LoginEvent
import com.example.habittracker.presentation.event.RegisterEvent
import com.example.habittracker.presentation.event.VerificationEvent
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
    private val mergeLocalAndRemoteDatabasesUseCase: MergeLocalAndRemoteDatabasesUseCase,
    private val syncRemoteToLocalUseCase: SyncRemoteToLocalUseCase,
    private val syncLocalToRemoteUseCase: SyncLocalToRemoteUseCase,
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val getAllRemoteHabitsUseCase: GetAllRemoteHabitsUseCase,
    private val loginViaGoogleUseCase: LoginViaGoogleUseCase,
    private val logInViaFacebookUseCase: LogInViaFacebookUseCase,
    private val checkIfVerifiedUseCase: CheckIfVerifiedUseCase,
    private val auth: FirebaseAuth
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
            LoginEvent.ChangeLoginState ->{
                val user = auth.currentUser
                _loginState.update {
                    it.copy(isLoggedIn = user != null)
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
                    onLoginStateChange(result)
                }
            }

            is LoginEvent.LoginViaGoogle ->{
                viewModelScope.launch(Dispatchers.IO) {
                    val result = loginViaGoogleUseCase(event.idToken)
                    onLoginStateChange(result)
                }
            }

            is LoginEvent.LoginViaFacebook ->{
                viewModelScope.launch(Dispatchers.IO) {
                    val result = logInViaFacebookUseCase(event.token)
                    onLoginStateChange(result)
                }
            }

            LoginEvent.HideDialog -> {
                viewModelScope.launch(Dispatchers.IO) {
                    when (loginState.value.option) {
                        0 -> mergeLocalAndRemoteDatabasesUseCase()
                        1 -> syncRemoteToLocalUseCase()
                        2 -> syncLocalToRemoteUseCase()
                    }
                    _loginState.update {
                        it.copy(
                            isLoggedIn = true,
                            shouldShowDialog = false,
                            option = -1
                        )
                    }
                }
            }

            is LoginEvent.ChooseOption -> {
                _loginState.update {
                    it.copy(
                        option = event.option
                    )
                }
            }

            LoginEvent.StartLoading -> {
                _loginState.update {
                    it.copy(isLoading = true)
                }
            }
            LoginEvent.StopLoading -> {
                _loginState.update {
                    it.copy(isLoading = false)
                }
                viewModelScope.launch {
                    val isVerified = checkIfVerifiedUseCase() ?: false
                    auth.addAuthStateListener {
                        _loginState.update {
                            it.copy(
                                isVerified= isVerified
                            )
                        }
                    }
                }

            }
        }
    }
    suspend fun onLoginStateChange(result: Result<User?>){
        result.onSuccess { user ->
            val isRemoteDbEmpty = getAllRemoteHabitsUseCase().isEmpty()
            val isLocalDbEmpty = getAllHabitsUseCase().isEmpty()

            if (!isRemoteDbEmpty && !isLocalDbEmpty) {
                _loginState.update { it.copy(shouldShowDialog = true) }
            } else {
                if (!isRemoteDbEmpty){
                    syncRemoteToLocalUseCase()
                    _loginState.update { it.copy(isLoggedIn = true) }
                }
                if (!isLocalDbEmpty){
                    syncLocalToRemoteUseCase()
                    _loginState.update { it.copy(isLoggedIn = true) }
                }
            }
            _loginState.update { it.copy(userName = user?.userName) }
        }.onFailure { exception ->
            _loginState.update {
                it.copy(
                    error = exception.message ?: "Unknown Error"
                )
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
                viewModelScope.launch(Dispatchers.IO) {
                    val result = signUpUseCase(email, userName, password)

                    result.onSuccess {
                        syncLocalToRemoteUseCase()
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
                    auth.addAuthStateListener { firebaseAuth ->
                        _loginState.update {
                            it.copy(
                                isLoggedIn = firebaseAuth.currentUser != null,
                                password = "",
                                userName = "",
                                option = -1,
                                error = null,
                                shouldShowDialog = false
                            )
                        }
                    }

                }
            }
        }
    }

    fun onVerificationEvent(event: VerificationEvent){
        when(event){
            VerificationEvent.checkIfVerified -> {
                viewModelScope.launch {
                    auth.currentUser?.reload()
                    val isVerified = checkIfVerifiedUseCase()
                    _loginState.update {
                        it.copy(
                            isVerified= isVerified
                        )
                    }
                }
            }
            VerificationEvent.logOut -> {
                viewModelScope.launch {
                    _loginState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    logOutUseCase()
                    auth.addAuthStateListener { firebaseAuth ->
                        _loginState.update {
                            it.copy(
                                isLoggedIn = firebaseAuth.currentUser != null,
                                password = "",
                                userName = "",
                                option = -1,
                                error = null,
                                shouldShowDialog = false,
                                isLoading = false
                            )
                        }
                    }

                }
            }
        }
    }
}