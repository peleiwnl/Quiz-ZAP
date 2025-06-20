package com.example.factZAP.firebase

import com.example.factZAP.utilities.Constants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar
import java.util.Date

/**
 * Manages the periodic reset of user scores in the application.
 * Handles weekly and monthly score resets using Firebase Fire store.
 */
class ScoreManagement {
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * checks if scores need to be reset and performs the reset if necessary.
     * compares current time against last reset timestamps for weekly and monthly scores.
     *
     * @param callback Function to be called after check/reset operations complete
     */
    fun checkAndResetScores(callback: () -> Unit) {
        val calendar = Calendar.getInstance()

        mFireStore.collection("system")
            .document("scoreResets")
            .get()
            .addOnSuccessListener { document ->
                val lastWeeklyReset = document?.getTimestamp("lastWeeklyReset")?.toDate()
                val lastMonthlyReset = document?.getTimestamp("lastMonthlyReset")?.toDate()

                val needsWeeklyReset: Boolean
                val needsMonthlyReset: Boolean

                if (lastWeeklyReset != null) {
                    val weeksSinceReset = getWeeksBetween(lastWeeklyReset, calendar.time)
                    needsWeeklyReset = weeksSinceReset >= 1
                } else {
                    needsWeeklyReset = true
                }

                if (lastMonthlyReset != null) {
                    val monthsSinceReset = getMonthsBetween(lastMonthlyReset, calendar.time)
                    needsMonthlyReset = monthsSinceReset >= 1
                } else {
                    needsMonthlyReset = true
                }

                if (needsWeeklyReset || needsMonthlyReset) {
                    performReset(needsWeeklyReset, needsMonthlyReset, calendar.time, callback)
                } else {
                    callback()
                }
            }
            .addOnFailureListener {
                callback()
            }
    }

    /**
     * calculates the number of weeks between two dates.
     *
     * @param date1 The earlier date
     * @param date2 The later date
     * @return Number of weeks between the dates
     */
    private fun getWeeksBetween(date1: Date, date2: Date): Long {
        val diff = date2.time - date1.time
        return diff / (7 * 24 * 60 * 60 * 1000)
    }

    /**
     * Calculates the number of months between two dates
     *
     * @param date1 The earlier date
     * @param date2 The later date
     * @return Number of months between the dates
     */
    private fun getMonthsBetween(date1: Date, date2: Date): Int {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        val yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)
        val monthDiff = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH)

        return yearDiff * 12 + monthDiff
    }

    /**
     * Performs reset operations on user scores
     * it updates system timestamps and resets user scores in a single batch operation.
     *
     * @param weeklyReset Whether weekly scores should be reset
     * @param monthlyReset Whether monthly scores should be reset
     * @param currentTime Current timestamp for updating last reset times
     * @param callback Function to be called after reset operations complete
     */
    private fun performReset(
        weeklyReset: Boolean, monthlyReset: Boolean, currentTime: Date, callback: () -> Unit) {
        val batch = mFireStore.batch()
        val systemRef = mFireStore.collection("system").
        document("scoreResets")
        val updates = mutableMapOf<String, Any>()

        if (weeklyReset) {
            updates["lastWeeklyReset"] = Timestamp(currentTime)
        }
        if (monthlyReset) {
            updates["lastMonthlyReset"] = Timestamp(currentTime)
        }

        batch.set(systemRef, updates, SetOptions.merge())

        mFireStore.collection(Constants.USER)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docRef = mFireStore.collection(Constants.USER).document(document.id)
                    val userUpdates = mutableMapOf<String, Any>()

                    if (weeklyReset) {
                        userUpdates[Constants.WEEKLY_SCORE] = 0
                    }
                    if (monthlyReset) {
                        userUpdates[Constants.MONTHLY_SCORE] = 0
                    }

                    batch.update(docRef, userUpdates)
                }

                batch.commit()
                    .addOnSuccessListener {
                        callback()
                    }
            }
    }


    /**
     * Initializes the system document with current timestamps
     * it creates or updates the scoreResets document
     * with current time for both weekly and monthly resets.
     */
    fun initializeSystemDocument() {
        val currentTime = Timestamp(Date())
        mFireStore.collection("system")
            .document("scoreResets")
            .set(mapOf(
                "lastWeeklyReset" to currentTime,
                "lastMonthlyReset" to currentTime
            ), SetOptions.merge())
    }
}