package com.example.data.data.remote

import com.example.domain.domain.model.User

interface AuthRemoteDataSource {
    suspend fun logIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String,userName: String, password: String): String
    suspend fun logOut()
    fun getCurrentUser(): User?
}