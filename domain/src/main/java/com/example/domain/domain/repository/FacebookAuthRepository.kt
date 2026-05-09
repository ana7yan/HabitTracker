package com.example.domain.domain.repository

import com.example.domain.domain.model.User

interface FacebookAuthRepository {
    suspend fun login(token: String): Result<User>
}