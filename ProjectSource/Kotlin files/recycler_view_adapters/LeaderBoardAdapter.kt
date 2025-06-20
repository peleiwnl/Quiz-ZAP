package com.example.factZAP.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.factZAP.R
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.LeaderBoardItemBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.utilities.Utils
import java.util.Locale

/**
 * RecyclerView adapter for displaying leaderboard entries
 * Shows user rankings with bronze, silver and gold for top 3 positions.
 *
 * @property itemList List of user models to display in the leaderboard
 * @property type Type of score to display ("allTimeScore", "weeklyScore", "monthlyScore")
 */
class LeaderBoardAdapter(private val itemList: List<User?>, private val type: String) :
    RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    /**
     * ViewHolder for leaderboard items.
     *
     * @property binding View binding for the leaderboard item layout
     */
    class ViewHolder(val binding: LeaderBoardItemBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Creates new view holders for the RecyclerView.
     *
     * @param parent The parent view group
     * @param viewType The view type of the new view
     * @return A new ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LeaderBoardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Returns the total number of items in the leaderboard.
     *
     * @return Size of the user list
     */
    override fun getItemCount(): Int {
        return itemList.size
    }

    /**
     * Binds leaderboard data to an existing view holder.
     *
     * it sets up the rank number, user name and score, profile picture, and a background
     * for 1st, 2nd, 3rd, and 4th onwards.
     *
     * @param holder The view holder to update
     * @param position Position of the item in the leaderboard
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userInfo = itemList[position]

        userInfo?.let { user ->
            with(holder.binding) {
                tvLeaderboardRank.text = String.format(Locale.getDefault(),
                    "%d", position + 1)
                tvLeaderboardName.text = user.name
                tvLeaderboardScore.text = String.format(Locale.getDefault(),
                    "%d", Utils.intendedScore(user, type))

                FirebaseSetup().setProfileImage(user.image, ivLeaderBoardProfilePic)

                when (position) {
                    0 -> { //top of the leaderboard
                        CardViewLeaderboard.setCardBackgroundColor(
                            ContextCompat.getColor(holder.itemView.context, R.color.gold))
                        tvLeaderboardScore.setTextColor(ContextCompat.getColor(
                            holder.itemView.context, R.color.white))
                    }
                    1 -> { //2nd
                        CardViewLeaderboard.setCardBackgroundColor(
                            ContextCompat.getColor(holder.itemView.context, R.color.silver))
                        tvLeaderboardScore.setTextColor(ContextCompat.getColor(
                            holder.itemView.context, R.color.white))
                    }
                    2 -> { //3rd
                        CardViewLeaderboard.setCardBackgroundColor(
                            ContextCompat.getColor(holder.itemView.context, R.color.bronze))
                        tvLeaderboardScore.setTextColor(ContextCompat.getColor(
                            holder.itemView.context, R.color.white))
                    }
                    else -> {
                        CardViewLeaderboard.setCardBackgroundColor(
                            ContextCompat.getColor(holder.itemView.context,
                                R.color.background_secondary)
                        )
                    }
                }
            }
        }
    }
}