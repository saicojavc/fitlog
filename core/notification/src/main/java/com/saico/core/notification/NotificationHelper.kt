package com.saico.core.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val DAILY_CHANNEL_ID = "motivational_daily"
        const val PROGRESS_CHANNEL_ID = "progress_achievements"
        const val SUMMARY_CHANNEL_ID = "daily_summary"
        const val WORKOUT_CHANNEL_ID = "workout_active"
        const val WORKOUT_NOTIFICATION_ID = 3001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyChannel = NotificationChannel(
                DAILY_CHANNEL_ID,
                "Motivación Diaria",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Frases motivadoras por la mañana"
                enableVibration(true)
            }

            val progressChannel = NotificationChannel(
                PROGRESS_CHANNEL_ID,
                "Logros y Progreso",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones al alcanzar metas de pasos"
                enableVibration(true)
            }

            val summaryChannel = NotificationChannel(
                SUMMARY_CHANNEL_ID,
                "Resumen del Día",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val workoutChannel = NotificationChannel(
                WORKOUT_CHANNEL_ID,
                "Entrenamiento Activo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el progreso de tu entrenamiento actual"
                setShowBadge(false)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(dailyChannel)
            manager.createNotificationChannel(progressChannel)
            manager.createNotificationChannel(summaryChannel)
            manager.createNotificationChannel(workoutChannel)
        }
    }

    /**
     * Muestra una notificación si el permiso POST_NOTIFICATIONS ha sido otorgado.
     */
    @SuppressLint("MissingPermission")
    fun showNotification(
        title: String,
        message: String,
        channelId: String,
        notificationId: Int,
        isOngoing: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (isOngoing) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(isOngoing) // Esto hace que no se pueda quitar
            .setAutoCancel(!isOngoing)
            .setOnlyAlertOnce(true) // Evita ruidos/vibraciones constantes
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: Exception) {}
    }

    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
