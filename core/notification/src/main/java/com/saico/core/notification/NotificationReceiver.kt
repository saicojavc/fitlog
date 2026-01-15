package com.saico.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("notification_type") ?: return

        when (type) {
            "daily_motivational" -> {
                val messages = listOf(
                    "¡Buenos días! Hoy es perfecto para tus 10k pasos 💪",
                    "Un paso más cerca de tu mejor versión 🚶‍♂️",
                    "La constancia es la clave del éxito. ¡A darle! 🔥",
                    "Tu cuerpo te lo agradecerá. ¡Empieza hoy! ✨",
                    "No te detengas hasta que te sientas orgulloso 🏆"
                )
                notificationHelper.showNotification(
                    "¡Es hora de moverse!",
                    messages.random(),
                    NotificationHelper.DAILY_CHANNEL_ID,
                    1001
                )
            }
            "workout_reminder" -> {
                notificationHelper.showNotification(
                    "Recordatorio de entrenamiento",
                    "Es hora de iniciar tu rutina diaria. ¡Tú puedes! 💪",
                    NotificationHelper.DAILY_CHANNEL_ID,
                    1003
                )
            }
            "daily_summary" -> {
                val steps = intent.getIntExtra("current_steps", 0)
                if (steps < 10000) {
                    notificationHelper.showNotification(
                        "Resumen del día",
                        "Hoy hiciste $steps pasos. ¡Buen esfuerzo! Mañana por los 10k 😊",
                        NotificationHelper.SUMMARY_CHANNEL_ID,
                        1002
                    )
                } else {
                    notificationHelper.showNotification(
                        "Resumen del día",
                        "Hoy hiciste $steps pasos. ¡Te has superado! ¡Felicidades! 😊",
                        NotificationHelper.SUMMARY_CHANNEL_ID,
                        1002
                    )
                }
            }
        }
    }
}
