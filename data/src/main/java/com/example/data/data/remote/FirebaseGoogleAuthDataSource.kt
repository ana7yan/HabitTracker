package com.example.data.data.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.example.domain.domain.model.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseGoogleAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : GoogleAuthDataSource {

    override suspend fun logIn(idToken: String): Result<User> {
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            return try {
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                Result.success(
                    User(
                        id = authResult.user?.uid,
                        userName = authResult.user?.displayName,
                        email = authResult.user?.email,
                        isVerified = null
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        } catch (e: NoCredentialException) {
            return Result.failure(e)
        } catch (e: Exception){
            Log.d("Auth", e.toString())
            return Result.failure(e)
        }
    }
}
