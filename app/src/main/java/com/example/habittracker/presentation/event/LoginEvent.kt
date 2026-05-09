package com.example.habittracker.presentation.event

sealed interface LoginEvent{
    data class InputEmail(val email: String): LoginEvent
    data class InputPassword(val password: String): LoginEvent
    object Login: LoginEvent
    object ChangeLoginState: LoginEvent
    object StartLoading: LoginEvent
    object StopLoading: LoginEvent
    object HideDialog: LoginEvent
    data class ChooseOption(val option: Int): LoginEvent
    data class LoginViaGoogle(val idToken: String): LoginEvent
    data class LoginViaFacebook(val token: String): LoginEvent
}