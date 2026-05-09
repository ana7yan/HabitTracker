package com.example.data.data.remote

import android.util.Log
import com.example.domain.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRemoteDataSource {
    override suspend fun logIn(email: String, password: String): Result<User> {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user
        return if (user != null) {
            Result.success(
                User(
                    id = user.uid,
                    userName = user.displayName,
                    email = user.email,
                    isVerified = user.isEmailVerified
                )
            )
        } else {
            Result.failure(Exception("User is null"))
        }

    }
    override suspend fun signUp(email: String, userName: String, password: String): String {

        val result = auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Log.d("Auth", "Verification email sent")
                        }
                        ?.addOnFailureListener {
                            Log.e("Auth", it.message ?: "Unknown error")
                        }
                } else {
                    Log.e("Auth", task.exception?.message ?: "Registration failed")
                }
            }.await()

        val user = result.user ?: throw Exception("User is null")
        val profileUpdates = userProfileChangeRequest {
            displayName = userName
        }

        user.updateProfile(profileUpdates).await()
        return user.uid

    }

    override suspend fun logOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        val username = auth.currentUser?.displayName
        val uid = auth.currentUser?.uid
        val email = auth.currentUser?.email
        val isVerified = auth.currentUser?.isEmailVerified
        return User(uid, username, email, isVerified)
    }

}