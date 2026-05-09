package com.example.domain.domain.usecase

import com.example.domain.domain.model.User
import com.example.domain.domain.repository.FacebookAuthRepository
import javax.inject.Inject

class LogInViaFacebookUseCase @Inject constructor(
    private val facebookRepo: FacebookAuthRepository
) {
    suspend operator fun invoke(token: String): Result<User>{
        return facebookRepo.login(token)
    }
}