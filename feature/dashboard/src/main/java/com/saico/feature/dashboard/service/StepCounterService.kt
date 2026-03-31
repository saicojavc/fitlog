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

    companion object {
        private const val TECH_BLUE = 0xFF3FB9F6.toInt()
    }

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
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.service_active_title).uppercase())
            .setContentText(getString(R.string.service_active_msg))
            .setSubText("FITLOG • ACTIVE SERVICE")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setColor(TECH_BLUE)
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.service_active_msg)))
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
                    saveWorkoutToDatabase(offset, totalStepsSinceReboot, lastResetDate)
                    // Al iniciar un nuevo día, verificamos si se perdió la racha
                    checkStreakLost(lastResetDate)
                    stepCounterDataStore.saveStepCounterData(totalStepsSinceReboot)
                    0
                } else {
                    val dailySteps = (totalStepsSinceReboot - offset).coerceAtLeast(0)
                    if (dailySteps > 0) {
                        saveWorkoutToDatabase(offset, totalStepsSinceReboot, todayStart)
                        updateStreakLogic(dailySteps)
                    }
                    dailySteps
                }
            }.collect { dailySteps ->
                stepCounterDataStore.updateCurrentSteps(dailySteps)
                checkProgressNotifications(dailySteps)
            }
        }
    }

    private suspend fun checkStreakLost(lastActiveDate: Long) {
        val profile = userProfileUseCase.getUserProfileUseCase().first() ?: return
        if (profile.currentStreak == 0) return

        val today = Calendar.getInstance()
        val lastDate = Calendar.getInstance().apply { timeInMillis = lastActiveDate }

        // Si han pasado más de 2 días (el periodo de gracia)
        val diffDays = ((today.timeInMillis - lastDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        if (diffDays >= 2) {
            // COLAPSO TOTAL: Se pierde la racha
            userProfileUseCase.updateUserProfileUseCase(profile.copy(
                currentStreak = 0,
                isFrozen = false,
                graceDaysUsed = 0
            ))
            notificationHelper.showStreakNotification(
                getString(R.string.streak_lost_title),
                getString(R.string.streak_lost_msg)
            )
        } else if (diffDays == 1) {
            // HIBERNACIÓN: Primer día fallido
            userProfileUseCase.updateUserProfileUseCase(profile.copy(
                isFrozen = true,
                graceDaysUsed = 1
            ))
            notificationHelper.showStreakNotification(
                getString(R.string.streak_frozen_title),
                getString(R.string.streak_frozen_msg)
            )
        }
    }

    private suspend fun updateStreakLogic(dailySteps: Int) {
        val profile = userProfileUseCase.getUserProfileUseCase().first() ?: return
        val goal = profile.dailyStepsGoal
        if (goal <= 0 || dailySteps < goal) return

        val today = Calendar.getInstance()
        val lastStreakDate = Calendar.getInstance().apply { timeInMillis = profile.lastStreakDate }

        if (profile.lastStreakDate == 0L || !isSameDay(today, lastStreakDate)) {
            val newStreak = if (isYesterday(today, lastStreakDate) || profile.isFrozen) {
                profile.currentStreak + 1
            } else {
                1
            }

            val updatedProfile = profile.copy(
                currentStreak = newStreak,
                lastStreakDate = System.currentTimeMillis(),
                isFrozen = false, // Se descongela al cumplir la meta
                graceDaysUsed = 0
            )

            userProfileUseCase.updateUserProfileUseCase(updatedProfile)

            // Notificación de Éxito Estilo Duolingo/Cyberpunk
            notificationHelper.showStreakNotification(
                getString(R.string.streak_goal_reached_title),
                getString(R.string.streak_goal_reached_msg, newStreak)
            )
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(today: Calendar, other: Calendar): Boolean {
        val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(yesterday, other)
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

        if (dailySteps >= goal && lastGoalDate < todayStart) {
            // Ya se maneja en updateStreakLogic con el nuevo sistema
            userSettingsDataStore.setGoalReachedShown(System.currentTimeMillis())
            return
        }

        // Aviso de inactividad (Ej: si son las 2pm y lleva menos del 20% del goal)
        val now = Calendar.getInstance()
        if (now.get(Calendar.HOUR_OF_DAY) == 14 && dailySteps < (goal * 0.2)) {
             notificationHelper.showStreakNotification(
                getString(R.string.streak_inactivity_title),
                getString(R.string.streak_inactivity_msg)
            )
        }
    }

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

        workoutUseCase.insertWorkoutUseCase(workout)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
