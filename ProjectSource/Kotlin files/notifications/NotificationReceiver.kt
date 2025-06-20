package com.example.factZAP.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.factZAP.R
import com.example.factZAP.data_classes.User
import com.example.factZAP.firebase.FirebaseSetup
import java.time.LocalDate

/**
 * BroadcastReceiver that handles daily question notifications
 */
class NotificationReceiver : BroadcastReceiver() {
    private val firebaseSetup = FirebaseSetup()


    /**
     * Called when a notification broadcast is received.
     *
     * returns early if user is not logged in.
     *
     * @param context The current context
     * @param intent The received intent containing notification parameters
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (!firebaseSetup.isUserLoggedIn()) {
            return
        }

        try {
            val checkAvailability =
                intent.getBooleanExtra("checkAvailability", false)

            if (checkAvailability) {
                NotificationScheduler().scheduleWhenAvailable(context)

                checkQuizAvailability(context.applicationContext)
            } else {
                showNotification(context.applicationContext)
            }
        } catch (e: Exception) {
            Log.d("NotificationReceiver", "Error in onReceive")
        }
    }

    /**
     * checks if a daily quiz is available for the current user.
     * the notification only shows if the user exists and hasn't attempted the daily question
     *
     * @param context Application context for Firebase and notification access
     */
    private fun checkQuizAvailability(context: Context) {
        val firebaseSetup = FirebaseSetup()
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                try {
                    if (userInfo == null) {
                        return
                    }
                    val currentDate = LocalDate.now().toString()

                    if (userInfo.lastAttemptDate != currentDate) {
                        showNotification(context)
                    } else {
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * Creates and displays a notification for the daily quiz.
     * the notification has a title, text, icon, and a clickable action to open the app
     * from the notification. It also has auto-cancel behaviour.
     *
     * @param context Application context for notification creation
     * @throws Exception if notification creation fails
     */
    private fun showNotification(context: Context) {
        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
                .setContentTitle("Daily Question Available!")
                .setContentText("Your daily question is now available. Test your knowledge!")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(NotificationScheduler.NOTIFICATION_ID, notification)

        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Error showing notification", e)
        }
    }


}