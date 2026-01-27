package com.saico.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.saico.core.datastore.UserSettingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserSettingsDataStore
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleDailyMotivationalNotification() {
        val intent = createIntent("daily_motivational", 2001)
        val calendar = getCalendarTime(8, 30)
        scheduleAlarm(calendar.timeInMillis, intent)
    }

    fun scheduleWorkoutReminder(hour: Int, minute: Int) {
        val intent = createIntent("workout_reminder", 2003)
        val calendar = getCalendarTime(hour, minute)
        scheduleAlarm(calendar.timeInMillis, intent)
    }

    fun scheduleDailySummaryNotification(hour: Int = 21, minute: Int = 0) {
        val intent = createIntent("daily_summary", 2002)
        val calendar = getCalendarTime(hour, minute)
        scheduleAlarm(calendar.timeInMillis, intent)
    }

    fun rescheduleAll() {
        runBlocking {
            val userData = userDataStore.userData.first()
            scheduleDailyMotivationalNotification()
            scheduleWorkoutReminder(userData.workoutReminderHour, userData.workoutReminderMinute)
            scheduleDailySummaryNotification()
        }
    }

    private fun createIntent(type: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_type", type)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getCalendarTime(hour: Int, minute: Int): Calendar {
        val now = Calendar.getInstance()
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DATE, 1)
            }
        }
    }

    private fun scheduleAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        when {
            // Android 12+ requiere verificar si podemos programar alarmas exactas
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
            else -> {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        }
    }
}
