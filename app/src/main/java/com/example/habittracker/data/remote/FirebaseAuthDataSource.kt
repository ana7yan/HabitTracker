package com.example.habittracker.data.remote

import com.example.habittracker.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRemoteDataSource {
    override suspend fun logIn(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("User is null")

    }

    override suspend fun signUp(email: String,userName: String, password: String): String {

        val result = auth.createUserWithEmailAndPassword(email, password).await()

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
        return User(uid,username,email)
    }

}