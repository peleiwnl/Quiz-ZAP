package com.example.factZAP.data_classes

/**
 * Represents a user in the application
 *
 * @property id the user's unique identifier
 * @property emailId the user's email address
 * @property name the user's name
 * @property image the user's profile picture
 * @property allTimeScore the user's total score
 * @property weeklyScore the user's weekly score
 * @property monthlyScore the user's monthly score
 * @property lastGameScore the user's last game score
 * @property preferredDifficulty the user's preferred difficulty, default = any
 * @property preferredQuestionCount the user's preferred question count, default = 10
 * @property dailyStreak the user's daily streak, default = 0
 * @property lastAttemptDate the user's last attempt date of the daily question
 * @property joinDate the user's join date
 * @property savedFacts a list of the user's saved facts
 * @property achievements all of the user's unlocked achievements
 * @property notificationTime the user's preferred time to receive the daily question notification
 * @property lastDailyCorrect if the user got the last daily question correct or not
 * @property lastDailyAttempt the time from their last attempt
 * @property streak the user's current streak
 */
data class User(
    val id: String = "",
    val emailId: String = "",
    val name: String = "",
    val image: String = "",
    val allTimeScore: Int = 0,
    val weeklyScore: Int = 0,
    val monthlyScore: Int = 0,
    val lastGameScore: Int = 0,
    val preferredDifficulty: String = "any",
    val preferredQuestionCount: Int = 10,
    val dailyStreak: Int = 0,
    val lastAttemptDate: String = "",
    val joinDate: String = "",
    val savedFacts: List<SavedFact> = listOf(),
    val achievements: List<Achievement> = listOf(),
    var notificationTime: Int = -1,
    val lastDailyCorrect: Boolean = false,
    val lastDailyAttempt: Long = 0,
    val streak: Int = 0
)