package com.saico.feature.dashboard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.common.util.StepCounterSensor
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.domain.usecase.workout.WorkoutUseCase
import com.saico.core.model.Workout
import com.saico.core.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service() {

    @Inject
    lateinit var stepCounterSensor: StepCounterSensor

    @Inject
    lateinit var stepCounterDataStore: StepCounterDataStore

    @Inject
    lateinit var userSettingsDataStore: UserSettingsDataStore

    @Inject
    lateinit var userProfileUseCase: UserProfileUseCase

    @Inject
    lateinit var workoutUseCase: WorkoutUseCase

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        observeSteps()
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "step_counter_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fitlog Step Counter",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

//        val notification: Notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Fitlog está activo")
//            .setContentText("Contando tus pasos en segundo plano...")
//            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
//            .setContentIntent(pendingIntent)
//            .build()
//
//        startForeground(1, notification)
    }

    private fun observeSteps() {
        serviceScope.launch {
            combine(
                stepCounterSensor.steps,
                stepCounterDataStore.stepOffset,
                stepCounterDataStore.lastResetDate
            ) { totalStepsSinceReboot, offset, lastResetDate ->
                if (stepCounterDataStore.isNewDay(lastResetDate)) {
                    savePreviousDayWorkout(offset, totalStepsSinceReboot, lastResetDate)
                    stepCounterDataStore.saveStepCounterData(totalStepsSinceReboot)
                    0
                } else {
                    (totalStepsSinceReboot - offset).coerceAtLeast(0)
                }
            }.collect { dailySteps ->
                checkProgressNotifications(dailySteps)
            }
        }
    }

    private suspend fun checkProgressNotifications(dailySteps: Int) {
        val profile = userProfileUseCase.getUserProfileUseCase().first() ?: return
        val goal = profile.dailyStepsGoal
        if (goal <= 0) return

        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val lastGoalDate = userSettingsDataStore.goalReachedShownDate.first()
        val lastHalfDate = userSettingsDataStore.halfGoalShownDate.first()

        if (dailySteps >= goal && lastGoalDate < todayStart) {
            notificationHelper.showNotification(
                "¡Meta cumplida! 🎉",
                "¡Increíble! Has llegado a tus $goal pasos.",
                NotificationHelper.PROGRESS_CHANNEL_ID,
                2001
            )
            userSettingsDataStore.setGoalReachedShown(System.currentTimeMillis())
            return
        }

        if (dailySteps >= goal / 2 && lastHalfDate < todayStart && lastGoalDate < todayStart) {
            notificationHelper.showNotification(
                "¡Mitad del camino! 🔥",
                "Ya llevas $dailySteps pasos. ¡Sigue así!",
                NotificationHelper.PROGRESS_CHANNEL_ID,
                2002
            )
            userSettingsDataStore.setHalfGoalShown(System.currentTimeMillis())
        }
    }

    private suspend fun savePreviousDayWorkout(previousOffset: Int, currentSensorValue: Int, previousDayDate: Long) {
        val yesterdaySteps = currentSensorValue - previousOffset
        if (yesterdaySteps <= 0) return

        val userProfile = userProfileUseCase.getUserProfileUseCase().first()

        val calories = FitnessCalculator.calculateCaloriesBurned(yesterdaySteps, userProfile?.weightKg ?: 0.0)
        val distance = FitnessCalculator.calculateDistanceKm(yesterdaySteps, userProfile?.heightCm?.toInt() ?: 0, userProfile?.gender ?: "")
        val activeTime = FitnessCalculator.calculateActiveTimeMinutes(yesterdaySteps)

        val calendar = Calendar.getInstance().apply { timeInMillis = previousDayDate }
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""

        val workout = Workout(
            steps = yesterdaySteps,
            calories = calories,
            distance = distance.toDouble(),
            time = Time(activeTime * 60 * 1000L),
            date = previousDayDate,
            dayOfWeek = dayOfWeek
        )

        workoutUseCase.insertWorkoutUseCase(workout)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
