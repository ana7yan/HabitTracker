package com.example.domain.domain.usecase


import com.example.domain.domain.model.User
import com.example.domain.domain.repository.GoogleAuthRepository
import javax.inject.Inject

class LoginViaGoogleUseCase @Inject constructor(
    private val googleRepo: GoogleAuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User>{
        return googleRepo.signIn(idToken)
    }
}

