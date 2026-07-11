package com.example.habittracker.presentation.event

sealed interface UiEvent {
    object RequestNotificationPermission : UiEvent
}