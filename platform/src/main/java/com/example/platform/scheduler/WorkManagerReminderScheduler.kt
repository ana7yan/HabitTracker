package com.example.platform.scheduler

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.domain.domain.model.Habit
import com.example.domain.domain.sceduler.ReminderScheduler
import com.example.platform.worker.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerReminderScheduler @Inject constructor(
    private val workManager: WorkManager
) : ReminderScheduler {

    override fun scheduleReminder(
        habit: Habit
    ) {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, habit.reminderHour)
        calendar.set(Calendar.MINUTE, habit.reminderMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val delay = (calendar.timeInMillis - System.currentTimeMillis())
            .coerceAtLeast(0)
        val inputData = Data.Builder()
            .putLong(HabitReminderWorker.KEY_REMINDER_ID, habit.id.toLong())
            .putInt(HabitReminderWorker.KEY_HOUR, habit.reminderHour)
            .putInt(HabitReminderWorker.KEY_MINUTE, habit.reminderMinute)
            .build()

        val request = PeriodicWorkRequestBuilder<HabitReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            habit.id.toString(),
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    override fun cancelReminder(reminderId: Long) {
        workManager.cancelUniqueWork(reminderId.toString())
    }
}