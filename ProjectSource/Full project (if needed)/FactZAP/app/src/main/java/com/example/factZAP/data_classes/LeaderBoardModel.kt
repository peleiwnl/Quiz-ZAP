package com.example.factZAP.data_classes

/**
 * Represents the leaderboard containing a list of ranked users.
 *
 * @property allRanks A list of users ranked on the leaderboard.
 * Each entry is represented by a [User],
 * or null if no user data is present for that rank.
 */
data class LeaderBoardModel(
    val allRanks:List<User?>
)
