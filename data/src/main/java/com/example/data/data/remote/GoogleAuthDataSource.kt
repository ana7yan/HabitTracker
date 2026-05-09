package com.example.data.data.remote

import com.example.domain.domain.model.User

interface GoogleAuthDataSource {
    suspend fun logIn(idToken: String): Result<User>
}