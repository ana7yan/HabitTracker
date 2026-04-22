package com.example.habittracker.presentation.state

data class LoginState(
    val email: String = "",
    val password: String = "",
    val userName: String? = "",
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val option: Int = -1,
    val shouldShowDialog: Boolean = false,
    val isLoading: Boolean = false
)
