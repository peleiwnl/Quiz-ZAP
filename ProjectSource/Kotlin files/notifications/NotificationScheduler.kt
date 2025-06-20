package com.example.factZAP.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * Manages scheduling and configuration of daily quiz notifications
 */
class NotificationScheduler {


    companion object {
        /** Notification channel identifier */
        const val CHANNEL_ID = "daily_quiz_channel"
        /** Unique identifier for daily quiz notifications */
        const val NOTIFICATION_ID = 1
        /** value indicating when the notification should be sent
         * based on when quiz becomes available */
        const val WHEN_AVAILABLE = -1
    }

    /**
     * Creates a notification channel for daily quiz notifications.
     *
     * @param context Application context for notification channel creation
     */
    fun createNotificationChannel(context: Context) {

        val name = "Daily Quiz Notifications"
        val descriptionText = "Notifications for daily quiz availability"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Schedules a daily notification for a specific hour of the day.
     * If hourOfDay is WHEN_AVAILABLE, it switches to availability-based scheduling.
     * cancels any existing notifications before scheduling new ones.
     *
     * @param context Application context for alarm scheduling
     * @param hourOfDay Hour to schedule notification (0-23) or WHEN_AVAILABLE
     */
    fun scheduleNotification(context: Context, hourOfDay: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        if (hourOfDay == WHEN_AVAILABLE) {

            scheduleWhenAvailable(context)
            return
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /**
     * this schedules hourly checks for quiz availability, which is used for the
     * when available option
     *
     * @param context Application context for alarm scheduling
     */
    fun scheduleWhenAvailable(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("checkAvailability", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_HOUR, //this can be changed to test that notifications work
            pendingIntent
        )

    }
}