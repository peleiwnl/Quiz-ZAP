/**
 * This object contains all available achievements in the application.
 */
package com.example.factZAP.achievements
import com.example.factZAP.data_classes.Achievement

object Achievements {

    /**
     * Achievement awarded when a user gets all questions correct in a quiz.
     */
    val PERFECT_SCORE = Achievement(
        id = "perfect_score",
        title = "Perfect Score",
        description = "Get all questions right in a quiz",
        iconName = "ic_trophy",
        unlocked = false
    )

    /**
     * Achievement awarded when a user completes a daily quiz challenge.
     */
    val DAILY_CHAMPION = Achievement(
        id = "daily_champion",
        title = "Daily Champion",
        description = "Complete a daily quiz",
        iconName = "ic_calendar",
        unlocked = false
    )

    /**
     * Achievement awarded when a user places in the top 3 positions of any leaderboard.
     */
    val TOP_THREE = Achievement(
        id = "top_three",
        title = "Leaderboard Elite",
        description = "Place in the top 3 of any leaderboard",
        iconName = "ic_medal",
        unlocked = false
    )

}