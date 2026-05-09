package com.example.data.data.repository

import com.example.data.data.remote.GoogleAuthDataSource
import com.example.domain.domain.model.User
import com.example.domain.domain.repository.GoogleAuthRepository
import javax.inject.Inject

class GoogleAuthRepositoryImpl @Inject constructor(
    private val googleAuthDataSource: GoogleAuthDataSource
): GoogleAuthRepository{
    override suspend fun signIn(idToken: String): Result<User> {
        return googleAuthDataSource.logIn(idToken)
    }
}