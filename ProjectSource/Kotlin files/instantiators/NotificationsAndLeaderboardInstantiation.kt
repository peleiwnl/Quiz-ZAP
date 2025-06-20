package com.example.factZAP.instantiators

import android.app.Application
import com.example.factZAP.data_classes.User
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.firebase.ScoreManagement
import com.example.factZAP.notifications.NotificationScheduler

/**
 * handles initialization of leaderboard system and notification settings
 * for the application.
 */
class NotificationsAndLeaderboardInstantiation  : Application() {

    /**
     * Called when the application is first created.
     * Initializes the leaderboard system, and notification settings restoration
     */
    override fun onCreate() {
        super.onCreate()
        initializeLeaderboardSystem()
        restoreNotificationSettings()
    }

    /**
     * Initializes the leaderboard by creating/updating the system document
     * in Firebase that tracks score reset timestamps.
     */
    private fun initializeLeaderboardSystem() {
        ScoreManagement().initializeSystemDocument()
    }

    /**
     * Restores notification settings for logged-in users.
     * Retrieves the user's saved notification time preference and reschedules
     * notifications if a time was previously set unless the user isn't logged in.
     */
    private fun restoreNotificationSettings() {
        val firebaseSetup = FirebaseSetup()
        if (!firebaseSetup.isUserLoggedIn()) return

        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let {
                    if (it.notificationTime != 0) {
                        NotificationScheduler().
                        scheduleNotification(applicationContext, it.notificationTime)
                    }
                }
            }
        })
    }
}