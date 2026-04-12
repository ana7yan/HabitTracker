package com.example.habittracker.presentation.event

sealed interface AccountEvent {
    object LogOut: AccountEvent
}