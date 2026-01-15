package com.saico.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.saico.core.ui.R
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
            "workout_reminder" -> {
                notificationHelper.showNotification(
                    context.getString(R.string.workout_reminder_title),
                    context.getString(R.string.workout_reminder_msg),
                    NotificationHelper.DAILY_CHANNEL_ID,
                    1003
                )
            }
            "daily_summary" -> {
                val steps = intent.getIntExtra("current_steps", 0)
                val title = context.getString(R.string.daily_summary_title)
                val message = if (steps < 10000) {
                    context.getString(R.string.daily_summary_msg_incomplete, steps)
                } else {
                    context.getString(R.string.daily_summary_msg_complete, steps)
                }
                notificationHelper.showNotification(
                    title,
                    message,
                    NotificationHelper.SUMMARY_CHANNEL_ID,
                    1002
                )
            }
        }
    }
}
