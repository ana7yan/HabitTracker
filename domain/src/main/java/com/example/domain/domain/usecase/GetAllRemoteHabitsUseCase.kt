package com.example.domain.domain.usecase


import com.example.domain.domain.model.Habit
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.UserAuthRepository
import javax.inject.Inject

class GetAllRemoteHabitsUseCase @Inject constructor(
    private val remoteRepository: HabitRemoteRepository,
    private val authRepository: UserAuthRepository
)  {
    suspend operator fun invoke(): List<Habit>{
        val uid = authRepository.getCurrentUserId()
        if(uid == null) return emptyList()
        return remoteRepository.getHabits(uid)
    }
}