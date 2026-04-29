package com.example.domain.domain.repository

import com.example.domain.domain.model.User

interface UserAuthRepository {
    suspend fun logIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, userName: String, password: String): Result<User>
    suspend fun logOut()
    suspend fun getCurrentUser(): User?
    suspend fun getCurrentUserId(): String?
}