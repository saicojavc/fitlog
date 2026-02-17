package com.saico.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var stepCounterDataStore: StepCounterDataStore

    @Inject
    lateinit var notificationScheduler: NotificationScheduler
    
    @Inject
    lateinit var userSettingsDataStore: UserSettingsDataStore

    override fun onReceive(context: Context, intent: Intent) {
        // Soporte para reprogramar al encender el teléfono o si se actualiza la app
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            notificationScheduler.rescheduleAll()
            return
        }

        val type = intent.getStringExtra("notification_type") ?: return

        when (type) {
            "daily_motivational" -> {
                showMotivational(context)
                notificationScheduler.scheduleDailyMotivationalNotification()
            }
            "workout_reminder" -> {
                showWorkoutReminder(context)
                runBlocking {
                    val settings = userSettingsDataStore.userData.first()
                    notificationScheduler.scheduleWorkoutReminder(settings.workoutReminderHour, settings.workoutReminderMinute)
                }
            }
            "daily_summary" -> {
                showSummary(context)
                notificationScheduler.scheduleDailySummaryNotification()
            }
        }
    }

    private fun showMotivational(context: Context) {
        val messages = listOf(
            context.getString(R.string.motivational_msg_1),
            context.getString(R.string.motivational_msg_2),
            context.getString(R.string.motivational_msg_3),
            context.getString(R.string.motivational_msg_4),
            context.getString(R.string.motivational_msg_5)
        )
        notificationHelper.showNotification(
            context.getString(R.string.daily_motivational_title),
            messages.random(),
            NotificationHelper.DAILY_CHANNEL_ID,
            1001
        )
    }

    private fun showWorkoutReminder(context: Context) {
        notificationHelper.showNotification(
            context.getString(R.string.workout_reminder_title),
            context.getString(R.string.workout_reminder_msg),
            NotificationHelper.DAILY_CHANNEL_ID,
            1003
        )
    }

    private fun showSummary(context: Context) {
        val steps = runBlocking {
            stepCounterDataStore.currentSteps.first()
        }
        val title = context.getString(R.string.daily_summary_title)
        
        val message = when {
            steps < 1000 -> context.getString(R.string.daily_summary_msg_low, steps)
            steps in 1000..<10000 -> context.getString(R.string.daily_summary_msg_incomplete, steps)
            else -> context.getString(R.string.daily_summary_msg_complete, steps)
        }

        notificationHelper.showNotification(
            title,
            message,
            NotificationHelper.SUMMARY_CHANNEL_ID,
            1002
        )
    }
}
