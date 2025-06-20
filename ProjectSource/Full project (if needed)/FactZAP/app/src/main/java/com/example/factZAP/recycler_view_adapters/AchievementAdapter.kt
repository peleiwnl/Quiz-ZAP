package com.example.factZAP.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.factZAP.R
import com.example.factZAP.data_classes.Achievement
import com.example.factZAP.databinding.ItemAchievementBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * RecyclerView adapter for displaying achievements
 */
class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    /**
     * DiffUtil callback for comparing achievements
     */
    private val diffCallback = object : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Mapping of achievement icons to drawable resources.
     */
    private val achievementIconMap = mapOf(
        "ic_trophy" to R.drawable.ic_trophy,
        "ic_calendar" to R.drawable.ic_calendar,
        "ic_medal" to R.drawable.ic_medal
    )

    /**
     * ViewHolder for achievements. binds achievement data to item views.
     *
     * @property binding View binding for the achievement layout
     */
    inner class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                val resourceId = achievementIconMap[achievement.iconName]
                    ?: R.drawable.ic_trophy
                achievementIcon.setImageResource(resourceId)
                achievementTitle.text = achievement.title
                achievementDescription.text = achievement.description

                if (achievement.unlocked) {
                    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(Date(achievement.unlockedDate))
                    achievementDate.text =
                        itemView.context.getString(R.string.achievement_unlock_date, dateStr)
                    achievementDate.visibility = View.VISIBLE
                } else {
                    achievementDate.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Creates new view holders for the recycler view
     *
     * @param parent The parent view group
     * @param viewType The view type of the new view
     * @return A new AchievementViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AchievementViewHolder(binding)
    }

    /**
     * binds achievement data to an existing view holder.
     *
     * @param holder The view holder to update
     * @param position Position of the item in the data set
     */
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    /**
     * Returns the total number of achievements in the current list.
     *
     * @return Size of the current achievement list
     */
    override fun getItemCount() = differ.currentList.size

    private val differ = AsyncListDiffer(this, diffCallback)

    /**
     * Updates the list of achievements displayed in the RecyclerView.
     * Filters out locked achievements and submits the new list to DiffUtil.
     *
     * @param newAchievements List of achievements to display
     */
    fun updateAchievements(newAchievements: List<Achievement>) {
        val filteredAchievements = newAchievements.filter { it.unlocked }
        differ.submitList(filteredAchievements)
    }
}


