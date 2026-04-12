package com.example.habittracker.presentation.state

data class RegisterState (
    val email: String = "",
    val userName: String = "",
    val password: String = "",
    val isRegistered: Boolean = false,
    val error: String = ""
)