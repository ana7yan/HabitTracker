package com.example.habittracker.data.remote

import com.example.habittracker.domain.model.User

interface AuthRemoteDataSource {
    suspend fun logIn(email: String, password: String): String
    suspend fun signUp(email: String,userName: String, password: String): String
    suspend fun logOut()
    fun getCurrentUser(): User?
}