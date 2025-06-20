package com.example.factZAP.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.factZAP.data_classes.Result
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.ActivityFinishBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.recycler_view_adapters.TriviaResultsAdapter
import com.example.factZAP.utilities.AchievementsManager
import java.util.Locale

/**
 * this activity displays quiz results and handles score calculation.
 * it is responsible for displaying quiz results in a recycler view,
 * calculating the total score (with streak bonuses), updating a user's firebase profile
 * with the new scores, checking achievement completion and managing daily streaks
 */

class FinishActivity : AppCompatActivity() {

    /** handling user data and scores with firebase */
    private val firebaseSetup = FirebaseSetup()

    /** accessing the activity's views with view binding */
    private var binding: ActivityFinishBinding? = null

    /** using the achievement manager to achievement-related code */
    private lateinit var achievementsManager: AchievementsManager

    /**
     * initializes the activity, processes quiz results, and displays them.
     *
     * This function retrieves results from the intent, calculates the final score,
     * update's the user's score with the final score, checks for achievements,
     * initializes the recycler view and handles quiz streaks
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        achievementsManager = AchievementsManager(this)

        val resultList =
            intent.getSerializableExtraProvider<ArrayList<Result>>("resultList")
            ?: arrayListOf()

        val endScore = getEndScore(resultList)
        FirebaseSetup().updateUserScore(endScore)

        if (resultList.size == 1) { //if its a daily question
            calculateScoreWithStreak(resultList)
            achievementsManager.checkDailyQuizCompletion()
        } else {
            FirebaseSetup().updateUserScore(endScore)
            displayResults(resultList, endScore)
        }

        val correctAnswers = resultList.count { it.score > 0}
        achievementsManager.checkQuizCompletion(resultList.size, correctAnswers)

        binding?.resultsRecyclerview?.layoutManager = LinearLayoutManager(this)
        val resultsAdapter = TriviaResultsAdapter(resultList)

        binding?.resultsRecyclerview?.adapter = resultsAdapter

        binding?.totalScore?.text = String.format(
            Locale.getDefault(),
            "Total Points: %d", getEndScore(resultList)
        )

        binding?.homeButton?.setOnClickListener {
            finish()
        }
    }


    /**
     * Calculates the total score from a list of quiz results.
     *
     * @param list List of quiz results to calculate score from
     * @return Total score as an integer
     */
    private fun getEndScore(list: ArrayList<Result>): Int {
        var endScore = 0
        for (i in list)
            endScore += i.score
        return endScore
    }


    /**
     * safely retrieve serializable extras from Intent.
     *
     * @param identifierParameter Key used to store the extra
     * @return The extra cast to type T, or null if not found
     */
    private inline fun <reified T : java.io.Serializable> Intent.getSerializableExtraProvider(
        identifierParameter: String
    ): T? {
        return this.getSerializableExtra(identifierParameter, T::class.java)
    }


    /**
     * Calculates final score including streak bonus for daily questions.
     *
     * ir retrieves the user's current streak, adds a streak bonus if correct,
     * updates the total score and the streak counter, and then displays the results
     * with the streak bonus
     *
     * @param resultList List containing the single question result
     */
    private fun calculateScoreWithStreak(resultList: ArrayList<Result>) {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val baseScore = getEndScore(resultList)
                    val isCorrect = resultList[0].score > 0

                    val streakBonus = if (isCorrect) {
                        user.dailyStreak
                    } else {
                        0
                    }

                    val totalScore = baseScore + streakBonus
                    FirebaseSetup().updateUserScore(totalScore)
                    updateDailyStreak(isCorrect)
                    displayResults(resultList, totalScore, streakBonus)
                }
            }
        })
    }


    /**
     * displays quiz results in the RecyclerView.
     *
     * @param resultList List of quiz results to display
     * @param totalScore Final score including any bonuses
     * @param streakBonus Optional streak bonus points to display
     */
    private fun displayResults(resultList: ArrayList<Result>,
                               totalScore: Int, streakBonus: Int = 0) {
        binding?.resultsRecyclerview?.layoutManager = LinearLayoutManager(this)
        val resultsAdapter = TriviaResultsAdapter(resultList)
        binding?.resultsRecyclerview?.adapter = resultsAdapter
        if (streakBonus > 0) { //if they have a streak
            binding?.totalScore?.text = String.format(
                Locale.getDefault(),
                "Total Points: %d (Including streak bonus: +%d)",
                totalScore,
                streakBonus
            )
        } else {
            binding?.totalScore?.text = String.format(
                Locale.getDefault(),
                "Total Points: %d",
                totalScore
            )
        }
    }


    /**
     * Updates the user's daily streak based on the result of the daily question.
     *
     * if the streak is correct, the streak is incremented by 1. If it is incorrect,
     * the streak is incorrect, the streak is reset. users can only take the daily question
     * once per day.
     *
     * @param isCorrect Whether the quiz answer was correct
     */
    private fun updateDailyStreak(isCorrect: Boolean) {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val currentDate = java.time.LocalDate.now().toString()
                    val newStreak = if (isCorrect) user.dailyStreak + 1 else 0

                    val updatedUser = user.copy(
                        dailyStreak = newStreak,
                        lastAttemptDate = currentDate
                    )

                    firebaseSetup.updateUserPreferences(updatedUser)

                }
            }
        })
    }
}
