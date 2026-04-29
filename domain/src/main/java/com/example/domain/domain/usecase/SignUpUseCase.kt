package com.example.domain.domain.usecase

import com.example.domain.domain.model.User
import com.example.domain.domain.repository.UserAuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor (
    private val repository: UserAuthRepository
) {
    suspend operator fun invoke(email: String,username: String, password: String): Result<User?>{
        return repository.signUp(email, username,password)
    }
}