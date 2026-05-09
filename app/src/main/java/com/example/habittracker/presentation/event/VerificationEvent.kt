package com.example.habittracker.presentation.event

sealed interface VerificationEvent {
    object logOut: VerificationEvent
    object checkIfVerified: VerificationEvent
}