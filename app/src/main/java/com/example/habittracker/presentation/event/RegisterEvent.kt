package com.example.habittracker.presentation.event

sealed interface RegisterEvent {
    data class SetEmail(val email: String): RegisterEvent
    data class SetUserName(val userName: String): RegisterEvent
    data class SetPassword(val password: String): RegisterEvent
    object Register: RegisterEvent
}