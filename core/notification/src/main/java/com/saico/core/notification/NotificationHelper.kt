package com.saico.core.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
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
        const val ALARM_CHANNEL_ID = "workout_reminder_alarm"
        const val STREAK_CHANNEL_ID = "streak_status"

        const val WORKOUT_NOTIFICATION_ID = 3001
        const val ALARM_NOTIFICATION_ID = 1003
        const val STREAK_NOTIFICATION_ID = 4001

        const val ACTION_DISMISS_ALARM = "com.saico.fitlog.ACTION_DISMISS_ALARM"

        const val TECH_BLUE = 0xFF3FB9F6.toInt()
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyChannel = NotificationChannel(DAILY_CHANNEL_ID, "FITLOG • MOTIVACIÓN", NotificationManager.IMPORTANCE_HIGH)
            val progressChannel = NotificationChannel(PROGRESS_CHANNEL_ID, "FITLOG • LOGROS", NotificationManager.IMPORTANCE_HIGH)
            val summaryChannel = NotificationChannel(SUMMARY_CHANNEL_ID, "FITLOG • RESUMEN", NotificationManager.IMPORTANCE_HIGH)

            val workoutChannel = NotificationChannel(WORKOUT_CHANNEL_ID, "FITLOG • ENTRENAMIENTO", NotificationManager.IMPORTANCE_HIGH).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val alarmChannel = NotificationChannel(ALARM_CHANNEL_ID, "FITLOG • ALARMAS", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Canal para recordatorios tipo alarma"
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // Nuevo canal para Rachas
            val streakChannel = NotificationChannel(STREAK_CHANNEL_ID, "FITLOG • RACHAS", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones críticas sobre tu racha diaria"
                enableVibration(true)
                setShowBadge(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(dailyChannel)
            manager.createNotificationChannel(progressChannel)
            manager.createNotificationChannel(summaryChannel)
            manager.createNotificationChannel(workoutChannel)
            manager.createNotificationChannel(alarmChannel)
            manager.createNotificationChannel(streakChannel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showStreakNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, STREAK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation) // Cambiar por icono de racha si existe
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setColor(TECH_BLUE)
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        NotificationManagerCompat.from(context).notify(STREAK_NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    fun showAlarmNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val dismissIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DISMISS_ALARM
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_NOTIFICATION_ID,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title.uppercase())
            .setContentText(message)
            .setSubText(context.getString(com.saico.core.ui.R.string.alarm_subtext))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 500, 500, 500, 500, 500))
            .setFullScreenIntent(pendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(TECH_BLUE)
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(com.saico.core.ui.R.string.dismiss),
                dismissPendingIntent
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        NotificationManagerCompat.from(context).notify(ALARM_NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    fun showWorkoutNotification(title: String, content: String, startTimeMillis: Long? = null, isPaused: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, WORKOUT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle(title.uppercase())
            .setContentText(content)
            .setSubText("FITLOG • ENTRENAMIENTO")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setColor(TECH_BLUE)
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))

        if (startTimeMillis != null && !isPaused) {
            builder.setUsesChronometer(true)
            builder.setWhen(startTimeMillis)
        }

        NotificationManagerCompat.from(context).notify(WORKOUT_NOTIFICATION_ID, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showNotification(title: String, message: String, channelId: String, notificationId: Int, isOngoing: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle(title.uppercase())
            .setContentText(message)
            .setSubText("FITLOG • ANALYTICS")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(isOngoing)
            .setAutoCancel(!isOngoing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(TECH_BLUE)
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) = NotificationManagerCompat.from(context).cancel(notificationId)
}
