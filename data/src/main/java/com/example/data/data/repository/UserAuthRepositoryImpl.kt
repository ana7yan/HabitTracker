package com.example.data.data.repository

import com.example.data.data.remote.FirebaseAuthDataSource
import com.example.domain.domain.model.User
import com.example.domain.domain.repository.UserAuthRepository

class UserAuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
): UserAuthRepository {
    override suspend fun logIn(
        email: String,
        password: String,
    ): Result<User> {
        return try {
            firebaseAuthDataSource.logIn(email, password)
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
                isVerified = false
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

    override suspend fun getCurrentUserId(): String? {
        return  firebaseAuthDataSource.getCurrentUser()?.id
    }


}