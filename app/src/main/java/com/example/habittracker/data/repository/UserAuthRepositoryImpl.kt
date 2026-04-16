package com.example.habittracker.data.repository

import com.example.habittracker.data.remote.FirebaseAuthDataSource
import com.example.habittracker.domain.model.User
import com.example.habittracker.domain.repository.UserAuthRepository

class UserAuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
): UserAuthRepository {
    override suspend fun logIn(
        email: String,
        password: String,
    ): Result<User> {
        return try {
            val uid = firebaseAuthDataSource.logIn(email, password)
            Result.success(User(
                id = uid,
                userName = null,
                email = email,
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        userName: String,
        password: String,
    ): Result<User> {
        return try {
            val uid = firebaseAuthDataSource.signUp(email, userName,password)
            Result.success(User(
                id = uid,
                userName = userName,
                email = email,
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logOut() {
        firebaseAuthDataSource.logOut()
    }

    override suspend fun getCurrentUser(): User? {
        return firebaseAuthDataSource.getCurrentUser()
    }

}