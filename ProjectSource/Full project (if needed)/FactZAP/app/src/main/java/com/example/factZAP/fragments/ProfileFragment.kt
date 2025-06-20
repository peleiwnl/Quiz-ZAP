package com.example.factZAP.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.factZAP.R
import com.example.factZAP.activities.LogInActivity
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.FragmentProfileBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.recycler_view_adapters.AchievementAdapter
import com.example.factZAP.recycler_view_adapters.SavedFactAdapter
import com.example.factZAP.utilities.Constants
import com.example.factZAP.utilities.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale

/**
 * this fragment displays the user's profile information, achievements and saved facts
 */
class ProfileFragment : Fragment() {

    private lateinit var achievementsAdapter: AchievementAdapter
    private lateinit var auth: FirebaseAuth
    private var binding: FragmentProfileBinding? = null

    /**
     * Creates and returns the fragment's view hierarchy.
     * it sets up the menu for preferences access, achievement display,
     * user authentication check, profile information, scores, and sign out functionality
     *
     * @param inflater Layout inflater to inflate views
     * @param container Parent view container
     * @param savedInstanceState Saved state bundle
     * @return The fragment's root view or null if user is not authenticated
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = binding?.root

        setupMenu()
        setupAchievements()

        auth = Firebase.auth
        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(requireContext(), LogInActivity::class.java))
            return root
        }

        loadUserProfile()

        updateScoreView("allTimeScore", binding?.tvUserRanking!!)
        updateScoreView("weeklyScore", binding?.tvWeeklyRank!!)
        updateScoreView("monthlyScore", binding?.tvMonthlyRank!!)

        binding?.cvSignOut?.setOnClickListener {
            if (auth.currentUser != null) {
                auth.signOut()
                val intent = Intent(requireContext(), LogInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        return root
    }

    /**
     * Gathers and displays the user's profile information, such as
     * score statistics, user name and join date, profile picture, and saved facts
     */
    private fun loadUserProfile() {
        val progressBar =  Utils.showProgressBar(requireContext())
        FirebaseSetup().getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                progressBar.cancel()
                userInfo?.let {

                    binding?.tvUserPoints?.text =
                        String.format(Locale.getDefault(), "%d", it.allTimeScore)
                    binding?.tvLastGameScore?.text =
                        String.format(Locale.getDefault(), "%d", it.lastGameScore)
                    binding?.tvWeeklyScore?.text =
                        String.format(Locale.getDefault(), "%d", it.weeklyScore)
                    binding?.tvMonthlyScore?.text =
                        String.format(Locale.getDefault(), "%d", it.monthlyScore)
                    binding?.tvUserName?.text = it.name
                    binding?.joinDate?.text = it.joinDate

                    val resourceId =
                        Constants.avatars[it.image.lowercase()] ?: Constants.DEFAULT_AVATAR
                    binding?.userProfilePic?.setImageResource(resourceId)

                    binding?.savedFactsRecyclerView?.layoutManager =
                        LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false)

                    binding?.savedFactsRecyclerView?.adapter =
                        SavedFactAdapter(it.savedFacts)

                    if (it.savedFacts.isEmpty()) {
                        binding?.savedFactsTextView?.visibility = View.GONE
                        binding?.savedFactsRecyclerView?.visibility = View.GONE
                    } else {
                        binding?.savedFactsTextView?.visibility = View.VISIBLE
                        binding?.savedFactsRecyclerView?.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    /**
     * Updates score ranking display for a specific score type.
     *
     * @param type The type of score ("allTimeScore", "weeklyScore", "monthlyScore")
     * @param textView TextView to display the ranking
     */
    private fun updateScoreView(type: String, textView: TextView) {
        FirebaseSetup().getRank(type, object : FirebaseSetup.UserRankCallback {
            override fun onUserRankFetched(rank: Int?) {
                rank?.let {
                    textView.text = String.format(Locale.getDefault(), "%d", it)
                }
            }
        })
    }

    /**
     * Cleans the binding when the view is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Sets up the preferences menu in the action bar.
     * provides access to the preferences bottom sheet.
     */
    private fun setupMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        PreferencesBottomSheet().show(
                            parentFragmentManager,
                            PreferencesBottomSheet.TAG
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Initializes and populates the achievements display.
     * shows unlocked achievements in a horizontal RecyclerView.
     * hides the achievements section if none are unlocked.
     */
    private fun setupAchievements() {
        achievementsAdapter = AchievementAdapter()
        binding?.achievementsRecyclerView?.apply {
            adapter = achievementsAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        FirebaseSetup().getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val unlockedAchievements = user.achievements.filter { it.unlocked }

                    if (unlockedAchievements.isNotEmpty()) {

                        achievementsAdapter.updateAchievements(unlockedAchievements)
                        binding?.achievementsTextView?.visibility = View.VISIBLE
                        binding?.achievementsRecyclerView?.visibility = View.VISIBLE
                    } else {

                        binding?.achievementsTextView?.visibility = View.GONE
                        binding?.achievementsRecyclerView?.visibility = View.GONE
                    }
                }
            }
        })
    }

    /**
     * Reloads user profile when the fragment resumes.
     * Ensures displayed information is current.
     */
    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }



}
