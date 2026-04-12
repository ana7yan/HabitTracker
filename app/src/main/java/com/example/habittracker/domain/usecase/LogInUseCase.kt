package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.User
import com.example.habittracker.domain.repository.UserAuthRepository
import javax.inject.Inject

class LogInUseCase @Inject constructor (
    private val repository: UserAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User?> {
        return repository.logIn(email, password)
    }
}