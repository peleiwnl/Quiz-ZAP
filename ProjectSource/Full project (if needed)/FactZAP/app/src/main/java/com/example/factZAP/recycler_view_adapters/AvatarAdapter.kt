package com.example.factZAP.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.factZAP.R
import com.example.factZAP.databinding.ItemAvatarBinding
import com.example.factZAP.utilities.Constants

/**
 * this RecyclerView adapter displays and selects avatar images
 * Manages avatar selection state and provides visual feedback for the selected avatar.
 *
 * @property selectedAvatar The currently selected avatar
 * @property onAvatarSelected Callback function invoked when a new avatar is selected
 */
class AvatarAdapter(
    private var selectedAvatar: String,
    private val onAvatarSelected: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    /**
     * ViewHolder for avatars
     *
     * @property binding View binding for the avatar
     */
    inner class AvatarViewHolder(val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Creates new view holders for the RecyclerView.
     *
     * @param parent The parent view group
     * @param viewType The view type of the new view
     * @return A new AvatarViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AvatarViewHolder(binding)
    }

    /**
     * Binds avatar data to an existing view holder.
     * it sets up the avatar image, click listener for avatar selection,
     * and selection state visual feedback.
     *
     * @param holder The view holder to update
     * @param position Position of the item in the avatar list
     */
    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatar = Constants.avatarNames[position]

        Constants.avatars[avatar]?.let { resourceId ->
            holder.binding.avatarImage.setImageResource(resourceId)
        }

        holder.binding.avatarContainer.setBackgroundResource(
            if (avatar == selectedAvatar) R.drawable.selected_avatar_background
            else android.R.color.transparent
        )

        holder.itemView.setOnClickListener {
            val previousSelected = selectedAvatar
            selectedAvatar = avatar
            onAvatarSelected(avatar)

            notifyItemChanged(Constants.avatarNames.indexOf(previousSelected))
            notifyItemChanged(position)
        }
    }

    /**
     * Returns the total number of available avatars.
     *
     * @return Size of the avatar list from Constants
     */
    override fun getItemCount() = Constants.avatars.size
}