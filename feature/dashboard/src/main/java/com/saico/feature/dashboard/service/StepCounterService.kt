package com.saico.feature.dashboard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
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
import com.saico.core.ui.R
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
    private var isObserving = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        if (!isObserving) {
            observeSteps()
            isObserving = true
        }
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

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.service_active_title))
            .setContentText(getString(R.string.service_active_msg))
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(1, notification)
        }
    }

    private fun observeSteps() {
        serviceScope.launch {
            combine(
                stepCounterSensor.steps,
                stepCounterDataStore.stepOffset,
                stepCounterDataStore.lastResetDate
            ) { totalStepsSinceReboot, offset, lastResetDate ->

                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                if (stepCounterDataStore.isNewDay(lastResetDate)) {
                    // 1. Guardar resumen final de ayer
                    saveWorkoutToDatabase(offset, totalStepsSinceReboot, lastResetDate)
                    // 2. Resetear para hoy
                    stepCounterDataStore.saveStepCounterData(totalStepsSinceReboot)
                    0
                } else {
                    val dailySteps = (totalStepsSinceReboot - offset).coerceAtLeast(0)
                    // 3. Sincronización en VIVO: Actualizamos el progreso de HOY en la DB y Nube
                    if (dailySteps > 0) {
                        saveWorkoutToDatabase(offset, totalStepsSinceReboot, todayStart)
                    }
                    dailySteps
                }
            }.collect { dailySteps ->
                stepCounterDataStore.updateCurrentSteps(dailySteps)
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
                getString(R.string.goal_reached_title),
                getString(R.string.goal_reached_msg, goal),
                NotificationHelper.PROGRESS_CHANNEL_ID,
                2001
            )
            userSettingsDataStore.setGoalReachedShown(System.currentTimeMillis())
            return
        }

        if (dailySteps >= goal / 2 && lastHalfDate < todayStart && lastGoalDate < todayStart) {
            notificationHelper.showNotification(
                getString(R.string.half_goal_title),
                getString(R.string.half_goal_msg, dailySteps),
                NotificationHelper.PROGRESS_CHANNEL_ID,
                2002
            )
            userSettingsDataStore.setHalfGoalShown(System.currentTimeMillis())
        }
    }

    /**
     * Guarda el progreso en la base de datos local (Room).
     * Como InsertWorkoutUseCase tiene lógica de Firebase, esto también sincroniza con la nube en vivo.
     */
    private suspend fun saveWorkoutToDatabase(offset: Int, currentSensorValue: Int, date: Long) {
        val stepsTaken = (currentSensorValue - offset).coerceAtLeast(0)
        if (stepsTaken < 0) return

        val userProfile = userProfileUseCase.getUserProfileUseCase().first()

        val calories = FitnessCalculator.calculateCaloriesBurned(stepsTaken, userProfile?.weightKg ?: 70.0)
        val distance = FitnessCalculator.calculateDistanceKm(stepsTaken, userProfile?.heightCm?.toInt() ?: 170, userProfile?.gender ?: "male")
        val activeTime = FitnessCalculator.calculateActiveTimeMinutes(stepsTaken)

        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""

        val workout = Workout(
            steps = stepsTaken,
            calories = calories,
            distance = distance.toDouble(),
            time = Time(activeTime * 60 * 1000L),
            date = date,
            dayOfWeek = dayOfWeek
        )

        // Esto dispara Room + Firebase gracias al UseCase sincronizado
        workoutUseCase.insertWorkoutUseCase(workout)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
