
package com.example.factZAP.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.ActivityRegisterBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.firebase.ScoreManagement
import com.example.factZAP.notifications.NotificationScheduler
import com.example.factZAP.utilities.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Activity handling new user registration and account setup.
 *
 * This activity manages user registration with name, email, and password,
 * validates input credentials, creates user profile in Firebase,
 * initializes system documents if needed, Sets up default notification preferences
 * and provides navigation to the sign-in screen
 */
class RegisterActivity : AppCompatActivity() {
    /** accessing views with binding */
    private var binding: ActivityRegisterBinding? = null

    /** using firebase auth for registration*/
    private lateinit var auth: FirebaseAuth

    /**
     * initializes the activity
     *
     * sets up firebase authentication, sign-up button listener
     * and the navigation to the login page.
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        binding?.loginScreenTextview?.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }

        binding?.signUpButton?.setOnClickListener { registerUser() }
    }

    /**
     * Handles the user registration process.
     *
     * - validates input fields
     * - creates Firebase authentication account
     * - creates user profile document
     * - initializes system documents if needed
     * - sets up default notifications
     * - navigates to main activity on success
     */
    private fun registerUser() {
        val email = binding?.etUserEmail?.text.toString()
        val name = binding?.etUserName?.text.toString()
        val password = binding?.etUserPassword?.text.toString()
        if (formValidation(name, email, password)) {
            val pb = Utils.showProgressBar(this)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { register ->
                    if (register.isSuccessful) {
                        val id = register.result.user?.uid
                        val currentDate = SimpleDateFormat("d MMMM yyyy",
                            Locale.getDefault()).format(System.currentTimeMillis())
                        val userdata = User( //creating attributes for the user
                            name = name,
                            id = id!!,
                            emailId = email,
                            joinDate = currentDate,
                            image = "avatar_1",
                            notificationTime = NotificationScheduler.WHEN_AVAILABLE
                        )

                        initializeSystemDocument {
                            FirebaseSetup().registerUser(userdata)
                            setupNotifications(userdata.notificationTime)
                            pb.cancel()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        binding?.root?.let { root ->
                            Utils.showSnackBar(
                                root, "Failed to create the user, please try again later")
                        }
                        pb.cancel()
                    }
                }
        }
    }

    /**
     * sets up notification preferences for new users.
     *
     * @param notificationTime The default notification time to set
     */
    private fun setupNotifications(notificationTime: Int) {
        NotificationScheduler().apply {
            createNotificationChannel(this@RegisterActivity)
            scheduleNotification(this@RegisterActivity, notificationTime)
        }
    }

    /**
     * initializes system-wide documents in Fire store if they don't exist.
     * Used for score management and system settings.
     *
     * @param onComplete Callback to execute after initialization attempt
     */
    private fun initializeSystemDocument(onComplete: () -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("system")
            .document("scoreResets")
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    ScoreManagement().initializeSystemDocument()
                }
                onComplete()
            }
            .addOnFailureListener {
                onComplete()
            }
    }

    /**
     * validates the registration form inputs.
     *
     * @param name The user's name to validate
     * @param email The email address to validate
     * @param password The password to validate
     * @return true if all inputs are valid, false otherwise
     */
    private fun formValidation(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                binding?.userNameTextInputLayout?.error = "Please enter a valid name"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() && TextUtils.isEmpty(email)  -> {
                binding?.userEmailTextInputLayout?.error = "Please enter a valid email address"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding?.userPasswordTextInputLayout?.error = "Please enter a valid password"
                false
            }
            else -> { true }
        }
    }


}