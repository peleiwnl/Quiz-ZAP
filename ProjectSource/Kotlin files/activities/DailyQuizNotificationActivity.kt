package com.example.factZAP.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.factZAP.R
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.ActivityDailyQuizNotificationBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.notifications.NotificationScheduler
import com.example.factZAP.utilities.Constants

/**
 * Activity for managing daily quiz notification preferences.
 *
 * this activity allows users to: select preferred notification time for daily quizzes,
 * save notification preferences to Firebase, schedule notifications, and request
 * notification permissions
 */

class DailyQuizNotificationActivity : AppCompatActivity() {
    /** view binding for accessing the activity's views */
    private var binding: ActivityDailyQuizNotificationBinding? = null

    /** handling user preferences through firebase */
    private val firebaseSetup = FirebaseSetup()

    /** managing daily notifications using notification scheduler */
    private val notificationScheduler = NotificationScheduler()

    /**
     * initializes the activity, sets up UI components, and loads saved preferences.
     *
     * this function requests notification permissions, initializes the toolbar,
     * creates the notification channel, configures time selection using the radio buttons,
     * and applies the saved notification preferences
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     * after previously being shut down
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            101
        )
        binding = ActivityDailyQuizNotificationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //back button
        binding?.toolbarTitle?.text = getString(R.string.daily_quiz_notification_time_Toolbar)

        notificationScheduler.createNotificationChannel(this)

        setupNotificationTimeSelection()
        loadSavedPreferences()
    }

    /**
     * handles the back button
     *
     * @param item The menu item that was clicked
     * @return true if the event was handled, false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * sets up the notification time selection functionality
     */
    private fun setupNotificationTimeSelection() {
        binding?.saveNotificationTime?.setOnClickListener {
            val selectedId = binding?.notificationTimeGroup?.
            checkedRadioButtonId ?: return@setOnClickListener
            val selectedHour = Constants.NOTIFICATION_TIME_RADIO_BUTTONS
                .entries
                .find { it.value == selectedId }
                ?.key ?: return@setOnClickListener


            notificationScheduler.scheduleNotification(this, selectedHour)

            // save preference to user profile
            firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
                override fun onUserDetailsFetched(userInfo: User?) {
                    userInfo?.let {
                        it.notificationTime = selectedHour
                        firebaseSetup.updateUserPreferences(it)
                    }
                }
            })

            finish()
        }
    }

    /**
     * loads previously saved notification preferences from Firebase.
     * If a preference exists, sets the corresponding radio button and schedules the notification.
     */
    private fun loadSavedPreferences() {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let {
                    val radioButtonId =
                        Constants.NOTIFICATION_TIME_RADIO_BUTTONS[it.notificationTime]
                    radioButtonId?.let { id ->
                        binding?.notificationTimeGroup?.check(id)
                        notificationScheduler.scheduleNotification(
                            this@DailyQuizNotificationActivity, it.notificationTime)
                    }
                }
            }
        })
    }
}