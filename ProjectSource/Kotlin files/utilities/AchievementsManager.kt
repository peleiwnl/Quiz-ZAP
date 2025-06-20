package com.example.factZAP.utilities

import android.content.Context
import com.example.factZAP.achievements.Achievements
import com.example.factZAP.data_classes.Achievement
import com.example.factZAP.data_classes.User
import com.example.factZAP.firebase.FirebaseSetup

/**
 * Manages achievement logic and unlocking
 *
 * @property context Application context for showing achievement notifications
 */
class AchievementsManager(private val context: Context) {

    private val firebaseSetup = FirebaseSetup()

    /**
     * Checks if user has earned the Perfect Score achievement.
     * Achievement is unlocked if user answers all questions correctly in a quiz
     *
     * @param totalQuestions Total number of questions in the quiz
     * @param correctAnswers Number of questions answered correctly
     */
    fun checkQuizCompletion(totalQuestions: Int, correctAnswers: Int) {
        if (totalQuestions > 1 && totalQuestions == correctAnswers) {
            unlockAchievement(Achievements.PERFECT_SCORE)
        }
    }

    /**
     * Checks and unlocks the daily champion achievement when a daily quiz is completed.
     */
    fun checkDailyQuizCompletion() {
        unlockAchievement(Achievements.DAILY_CHAMPION)
    }

    /**
     * Checks if user has earned the Top Three achievement.
     * Achievement is unlocked when user reaches top 3 position in any leaderboard.
     *
     * @param position User's position in the leaderboard
     */
    fun checkLeaderboardPosition(position: Int) {
        if (position <= 3) {
            unlockAchievement(Achievements.TOP_THREE)
        }
    }

    /**
     * Unlocks an achievement and updates user data.
     *
     * it does the following:
     * - checks if achievement is already unlocked
     * - updates achievement status with unlock time
     * - updates user preferences in Firebase
     * - shows achievement notification
     *
     * @param achievement The achievement to unlock
     */
    private fun unlockAchievement(achievement: Achievement) {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val currentAchievements = user.achievements.toMutableList()
                    val achievementIndex =
                        currentAchievements.indexOfFirst { it.id == achievement.id }

                    if (achievementIndex == -1 || !currentAchievements[achievementIndex].unlocked) {
                        val unlockedAchievement = achievement.copy(
                            unlocked = true,
                            unlockedDate = System.currentTimeMillis()
                        )

                        if (achievementIndex == -1) {
                            currentAchievements.add(unlockedAchievement)
                        } else {
                            currentAchievements[achievementIndex] = unlockedAchievement
                        }

                        val updatedUser = user.copy(achievements = currentAchievements)
                        firebaseSetup.updateUserPreferences(updatedUser)

                        showAchievementUnlocked(unlockedAchievement)
                    }
                }
            }
        })
    }

    /**
     * This displays a toast notification when an achievement is unlocked.
     *
     * @param achievement The unlocked achievement
     */
    private fun showAchievementUnlocked(achievement: Achievement) {
        val message = "Achievement Unlocked: ${achievement.title}"
        Utils.showToast(context, message)
    }
}