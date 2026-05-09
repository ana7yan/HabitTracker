package com.example.data.data.remote

import com.example.domain.domain.model.User
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFacebookAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
): FacebookAuthDataSource {
    override suspend fun logIn(token: String): Result<User> {
        return try {
            val credential = FacebookAuthProvider.getCredential(token)

            val authResult = auth.signInWithCredential(credential).await()

            val user = authResult.user ?: return Result.failure(Exception("No user"))

            Result.success(
                User(
                    id = user.uid,
                    email = user.email,
                    userName = user.displayName,
                    isVerified = null
                )
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}