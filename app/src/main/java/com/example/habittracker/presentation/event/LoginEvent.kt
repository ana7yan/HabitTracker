package com.example.habittracker.presentation.event

sealed interface LoginEvent{
    data class InputEmail(val email: String): LoginEvent
    data class InputPassword(val password: String): LoginEvent
    object Login: LoginEvent
}