package com.example.factZAP.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.factZAP.R
import com.example.factZAP.data_classes.LeaderBoardModel
import com.example.factZAP.databinding.FragmentLeaderboardBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.recycler_view_adapters.LeaderBoardAdapter
import com.example.factZAP.utilities.AchievementsManager
import com.example.factZAP.utilities.Constants
import com.example.factZAP.utilities.Utils

/**
 * this fragment displays user rankings and leaderboards in the application.
 * Shows all-time, weekly, and monthly leaderboards with user scores and rankings.
 */
class LeaderboardFragment : Fragment() {

    private var binding: FragmentLeaderboardBinding? = null

    /**
     * Creates and returns the fragment's view hierarchy.
     * it initializes the leaderboard recyclerview, the radio buttons for leaderboard types,
     * the all-time leaderboard view, and hides the action bar for the layout
     *
     * @param inflater Layout inflater to inflate views
     * @param container Parent view container
     * @param savedInstanceState Saved state bundle
     * @return The fragment's root view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        binding?.leaderBoardRecyclerView?.layoutManager = LinearLayoutManager(requireContext())

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        binding?.leaderBoardRecyclerView?.layoutManager = LinearLayoutManager(requireContext())

        val pbDialog = Utils.showProgressBar(requireContext())
        setLeaderBoard(Constants.ALL_TIME_SCORE, pbDialog)

        binding?.RadioGroup?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rbAllTime -> setLeaderBoard(Constants.ALL_TIME_SCORE, pbDialog)
                R.id.rbWeekly -> setLeaderBoard(Constants.WEEKLY_SCORE, pbDialog)
                R.id.rbMonthly -> setLeaderBoard(Constants.MONTHLY_SCORE, pbDialog)
            }
        }

        return binding?.root
    }

    /**
     * Restores the action bar visibility when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    /**
     * updates the leaderboard display with user rankings for the specified score type.
     * checks for achievement unlocks if user is in top 3 positions.
     *
     * @param type The type of scores to display (all-time, weekly, or monthly)
     * @param pbDialog Progress dialog to show during data loading
     */
    private fun setLeaderBoard(type: String, pbDialog: Dialog) {
        FirebaseSetup().getLeaderBoardData(type, object : FirebaseSetup.LeaderBoardDataCallback {
            override fun onLeaderBoardDataFetched(leaderBoardModel: LeaderBoardModel?) {
                pbDialog.cancel()
                if (leaderBoardModel != null) {
                    val allRanks = leaderBoardModel.allRanks

                    val adapter = LeaderBoardAdapter(allRanks, type)
                    binding?.leaderBoardRecyclerView?.adapter = adapter

                    FirebaseSetup().getRank(type, object : FirebaseSetup.UserRankCallback {
                        override fun onUserRankFetched(rank: Int?) {
                            if (rank != null && rank <= 3) {
                                //if a user is top 3, grant them the achievement
                                AchievementsManager(requireContext()).checkLeaderboardPosition(rank)
                            }
                        }
                    })

                }
            }
        })
    }

}

