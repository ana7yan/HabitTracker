package com.example.data.data.remote

import com.example.domain.domain.model.User

interface FacebookAuthDataSource {
    suspend fun logIn(token: String): Result<User>
}