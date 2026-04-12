package com.example.habittracker.presentation.state

data class LoginState(
    val email: String = "",
    val password: String = "",
    val userName: String? = "",
    val isLoggedIn: Boolean = false,
    val error: String = ""
)
