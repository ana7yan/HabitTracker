package com.example.habittracker.domain.usecase

import com.example.habittracker.data.model.FirebaseHabitUnit
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetAllRemoteHabitsUseCase @Inject constructor(
    private val remoteRepository: HabitRemoteRepository,
    private val auth: FirebaseAuth
)  {
    suspend operator fun invoke(): List<Habit>{
        val uid = auth.currentUser?.uid ?: return emptyList()
        return remoteRepository.getHabits(uid)
    }
}