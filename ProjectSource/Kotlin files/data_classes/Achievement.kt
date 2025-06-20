package com.example.factZAP.data_classes

/**
 * represents an achievement in the application.
 *
 * @property id The unique identifier for the achievement.
 * @property title The name or title of the achievement.
 * @property description A brief description of the achievement.
 * @property iconName The name of the icon associated with the achievement.
 * @property unlocked Indicates whether the achievement has been unlocked.
 * @property unlockedDate The timestamp when the achievement was unlocked.
 */
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconName: String = "",
    val unlocked: Boolean = false,
    val unlockedDate: Long = 0
)
