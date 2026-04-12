package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.repository.UserAuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor (
    private val repository: UserAuthRepository
) {
    suspend operator fun invoke(){
        repository.logOut()
    }
}