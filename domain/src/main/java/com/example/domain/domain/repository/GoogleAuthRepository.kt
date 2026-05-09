package com.example.domain.domain.repository

import com.example.domain.domain.model.User

interface GoogleAuthRepository {
    suspend fun signIn(idToken: String): Result<User>
}