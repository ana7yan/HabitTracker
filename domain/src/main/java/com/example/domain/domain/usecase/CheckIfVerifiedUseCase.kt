package com.example.domain.domain.usecase

import com.example.domain.domain.repository.FacebookAuthRepository
import com.example.domain.domain.repository.UserAuthRepository
import javax.inject.Inject

class CheckIfVerifiedUseCase @Inject constructor(
    private val authRepository: UserAuthRepository
) {
    suspend operator fun invoke(): Boolean? {
        val user = authRepository.getCurrentUser() ?: return false
        return user.isVerified
    }
}