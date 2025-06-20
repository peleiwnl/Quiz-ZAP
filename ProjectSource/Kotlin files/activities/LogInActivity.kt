
package com.example.factZAP.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.ActivityLogInBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.notifications.NotificationScheduler
import com.example.factZAP.utilities.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Handles user-sign in with Firebase.
 *
 * This activity manages sign-in with email and password,
 * validates input credentials, sets up the notifications for authenticated users,
 * handles automatic sign-in, and provides navigation to the sign-up screen
 */
class LogInActivity : AppCompatActivity() {

    /** accessing the activity's views with binding */
    private var binding: ActivityLogInBinding? = null

    /** handling user authentication with firebase */
    private lateinit var auth:FirebaseAuth

    /**
     * initializes the activity
     *
     * - checks for existing authenticated user
     * - sets up sign-in and registration button listeners
     * - redirects to main activity if user is already authenticated
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding?.signUpTextview?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding?.signInButton?.setOnClickListener {
            signInUser()
        }

    }

    /**
     * handles the user sign-in process with email and password.
     *
     * - validates input credentials
     * - shows progress indicator during authentication
     * - sets up notifications on successful sign-in
     * - navigates to main activity on success
     * - displays error message on failure
     */
    private fun signInUser() {
        val email = binding?.editTextSignInEmail?.text.toString()
        val password = binding?.editTextSignInPassword?.text.toString()
        if (formValidation(email, password)) {
            val pb = Utils.showProgressBar(this)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setupNotifications()
                        pb.cancel()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Utils.showSnackBar(binding?.root!!,
                            "Can't login currently. Try again later")
                        pb.cancel()
                    }
                }
        }
    }


    /**
     * Sets up notifications for authenticated users.
     *
     * - creates notification channel
     * - retrieves user preferences from Firebase
     * - schedules notifications based on user's preferred time
     *
     * this makes it so that notifications are setup after a user logs in to save them from
     * having to constantly save their notification preferences
     */
    private fun setupNotifications() {
        val firebaseSetup = FirebaseSetup()
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let {
                    if (it.notificationTime != 0) {
                        NotificationScheduler().apply {
                            createNotificationChannel(this@LogInActivity)
                            scheduleNotification(this@LogInActivity, it.notificationTime)
                        }
                    }
                }
            }
        })
    }

    /**
     * Validates the sign in form inputs.
     *
     * @param email The email address to validate
     * @param password The password to validate
     * @return true if both email and password are valid, false otherwise
     */
    private fun formValidation(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()->{
                binding?.emailTextInputLayout?.error = "Please provide a valid email"
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.passwordTextInputLayout?.error = "Please provide a valid password"
                false
            }
            else -> { true }
        }
    }
}