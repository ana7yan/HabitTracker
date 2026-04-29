package com.example.domain.domain.usecase

import com.example.domain.domain.repository.UserAuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor (
    private val repository: UserAuthRepository
) {
    suspend operator fun invoke(){
        repository.logOut()
    }
}