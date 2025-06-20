package com.example.factZAP.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.factZAP.R
import com.example.factZAP.data_classes.SavedFact
import com.example.factZAP.databinding.ItemSavedFactBinding

/**
 * RecyclerView adapter for displaying saved facts
 * Shows fact number, content, and save date for each saved fact.
 *
 * @property facts List of saved facts to display
 */
class SavedFactAdapter(private val facts: List<SavedFact>) :
    RecyclerView.Adapter<SavedFactAdapter.SavedFactViewHolder>() {

    /**
     * ViewHolder for saved facts
     * Handles binding of fact data to the item layout.
     *
     * @property binding View binding for the saved fact layout
     */
    class SavedFactViewHolder(private val binding: ItemSavedFactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds fact data to the view holder.
         *
         * it sets up the fact number, fact text content and date for when the fact was saved
         *
         * @param fact The saved fact to display
         */
        fun bind(fact: SavedFact) {
            binding.factNumber.text =
                binding.root.context.getString(R.string.fact_zap_number, fact.factNumber)
            binding.factText.text = fact.factText
            binding.savedDate.text = fact.savedDate
        }
    }

    /**
     * this creates new view holders for the RecyclerView.
     *
     * @param parent The parent view group
     * @param viewType The view type of the new view
     * @return A new SavedFactViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedFactViewHolder {
        val binding = ItemSavedFactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedFactViewHolder(binding)
    }

    /**
     * binds saved fact data to an existing view holder.
     *
     * @param holder The view holder to update
     * @param position Position of the fact in the list
     */
    override fun onBindViewHolder(holder: SavedFactViewHolder, position: Int) {
        holder.bind(facts[position])
    }

    /**
     * returns the total number of saved facts.
     *
     * @return Size of the facts list
     */
    override fun getItemCount() = facts.size
}
