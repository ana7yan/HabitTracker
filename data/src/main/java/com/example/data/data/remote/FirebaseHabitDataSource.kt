package com.example.data.data.remote

import android.util.Log
import com.example.data.data.model.FirebaseHabitUnit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseHabitDataSource @Inject constructor(
    private val db: FirebaseDatabase
) : HabitRemoteDataSource{

    override suspend fun addHabit(userId: String, habit: FirebaseHabitUnit): String? {
        val ref = db.getReference("users")
            .child(userId)
            .child("habits")
            .push()
        val remoteId = ref.key
        if(remoteId != null){
             val updatedHabit = habit.copy(
                 remoteId = remoteId
             )
            ref.setValue(updatedHabit).await()
        }
        return remoteId
    }
    override suspend fun updateHabitsInRTDB(uid: String, habitsToSync: List<FirebaseHabitUnit>) {
        val updateMap = mutableMapOf<String, Any?>()
        habitsToSync.forEach { habit ->
            val path = "$uid/habits/${habit.remoteId}"
            val habitData = mapOf(
                "streak" to habit.streak,
                "name" to habit.name,
                "remoteId" to habit.remoteId,
                "checkedDates" to habit.checkedDates,
                "creationDate" to habit.creationDate,
                "hasReminder" to habit.hasReminder,
                "reminderHour" to habit.reminderHour,
                "reminderMinute" to habit.reminderMinute
            )
            updateMap[path] = habitData
        }

        if (updateMap.isNotEmpty()) {
            db.getReference("users").updateChildren(updateMap).await()
        }
    }

    override suspend fun deleteHabit(userId: String, habitId: String) {
        db.getReference("users")
            .child(userId)
            .child("habits")
            .child(habitId)
            .removeValue()
            .await()
    }

    override fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>> = callbackFlow {
        val ref = db.getReference("users")
            .child(userId)
            .child("habits")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val habits = snapshot.children.mapNotNull {
                    it.getValue(FirebaseHabitUnit::class.java)
                }
                trySend(habits)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    override suspend fun updateHabit(
        userId: String,
        habit: FirebaseHabitUnit
    ) {
        val updates = mapOf(
            "streak" to habit.streak,
            "checkedDates" to habit.checkedDates,
            "remoteId" to habit.remoteId,
            "hasReminder" to habit.hasReminder,
            "reminderHour" to habit.reminderHour,
            "reminderMinute" to habit.reminderMinute
        )

        db.getReference("users")
            .child(userId)
            .child("habits")
            .child(habit.remoteId)
            .updateChildren(updates)
            .await()
    }

}