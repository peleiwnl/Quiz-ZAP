package com.example.factZAP.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.factZAP.activities.CustomTriviaActivity
import com.example.factZAP.activities.RandomFactActivity
import com.example.factZAP.data_classes.CategoryStatistics
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.FragmentHomeBinding
import com.example.factZAP.fetch_trivia.FetchTrivia
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.recycler_view_adapters.TwoColumnAdapter
import com.example.factZAP.utilities.Constants
import com.example.factZAP.utilities.Utils
import java.util.Locale

/**
 * this fragment displays the main home screen of the application.
 */
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val firebaseSetup = FirebaseSetup()

    /**
     * Updates the UI to reflect the user's current streak when the fragment resumes.
     */
    override fun onResume() {
        super.onResume()
        updateStreakDisplay()
    }

    /**
     * updates the streak counter display with the user's current daily streak.
     * fetches the latest streak count from Firebase and displays it.
     */
    private fun updateStreakDisplay() {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    binding?.streakNumber?.text = String.format(
                        Locale.getDefault(),
                        "%d",
                        user.dailyStreak
                    )
                }
            }
        })
    }

    /**
     * Handles the daily question functionality.
     * checks if user has already attempted today's question and either
     * shows a message if already attempted, or
     * fetches and displays a new question if not attempted
     */
    private fun handleDailyQuestion() {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val currentDate = java.time.LocalDate.now().toString()

                    if (user.lastAttemptDate == currentDate) {

                        Utils.showToast(requireContext(),
                            "You've already attempted today's question! Come back tomorrow!")
                    } else {

                        val fetchTrivia = FetchTrivia(requireContext())
                        fetchTrivia.getQuiz(1, null, null, null)
                    }
                }
            }
        })
    }

    /**
     * loads a quiz based on user's saved preferences and selected category.
     *
     * @param categoryId Optional category ID to filter questions.
     * If null, questions from all categories are included.
     */
    private fun loadQuizWithUserPreferences(categoryId: Int? = null) {

        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback  {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val fetchTrivia = FetchTrivia(requireContext())
                    fetchTrivia.getQuiz(
                        amount = user.preferredQuestionCount,
                        category = categoryId,
                        difficulty = if (user.preferredDifficulty == "any")
                            null else user.preferredDifficulty,
                        type = null
                    )
                }
            }
        })
    }

    /**
     * Creates and returns the fragment's view hierarchy.
     * sets up the category grid, the click listeners and initial streak display
     *
     * @param inflater layout inflater to inflate views
     * @param container parent view container
     * @param savedInstanceState Saved state bundle
     * @return The fragment's root view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding?.root
        val fetchTrivia = FetchTrivia(requireContext())
        val recyclerViewCategoryList = binding?.categories

        updateStreakDisplay()
        recyclerViewCategoryList?.layoutManager = GridLayoutManager(requireContext(), 2)

        fetchTrivia.getQuestionStatsList(object : FetchTrivia.QuestionStatCallback {
            override fun onQuestionStatFetched(map: Map<String, CategoryStatistics>) {
                val adapter = TwoColumnAdapter(Constants.getCategories(), map)
                recyclerViewCategoryList?.adapter = adapter
                adapter.setOnPressed(object : TwoColumnAdapter.OnPressed {

                    override fun onClick(id: Int) {
                        loadQuizWithUserPreferences(id)
                    }

                })
            }
        })

        binding?.dailyQuestionButton?.setOnClickListener {
            handleDailyQuestion()
        }

        binding?.customQuizButton?.setOnClickListener {
            startActivity(Intent(requireContext(), CustomTriviaActivity::class.java))
        }

        binding?.randomFactButton?.setOnClickListener {
            startActivity(Intent(requireContext(), RandomFactActivity::class.java))
        }

        return root
    }

    /**
     * Cleans the binding when the view is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}