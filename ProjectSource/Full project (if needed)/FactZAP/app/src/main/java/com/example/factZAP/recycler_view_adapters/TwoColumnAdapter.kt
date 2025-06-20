package com.example.factZAP.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.factZAP.data_classes.Category
import com.example.factZAP.data_classes.CategoryStatistics
import com.example.factZAP.databinding.CategoryItemsBinding

/**
 * RecyclerView adapter for displaying category items in a two-column grid.
 * Shows category image, name, and total number of available questions.
 *
 * @property items List of categories to display
 * @property categoryStatistics Map of category statistics including question counts
 */
class TwoColumnAdapter(
    private val items: List<Category>,
    private val categoryStatistics: Map<String, CategoryStatistics>
) : RecyclerView.Adapter<TwoColumnAdapter.ViewHolder>() {

    private var pressed: OnPressed? = null

    /**
     * ViewHolder for category items.
     *
     * @property binding View binding for the category item layout
     */
    inner class ViewHolder(val binding: CategoryItemsBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Interface for handling category click events.
     */
    interface OnPressed {
        /**
         * Called when a category is clicked.
         *
         * @param id The ID of the clicked category
         */
        fun onClick(id: Int)
    }

    /**
     * Sets the click listener for category items.
     *
     * @param onPressed Implementation of OnPressed interface
     */
    fun setOnPressed(onPressed: OnPressed) {
        this.pressed = onPressed
    }

    /**
     * Returns the total number of categories.
     *
     * @return Size of the categories list
     */
    override fun getItemCount(): Int = items.size

    /**
     * Creates new view holders for the RecyclerView.
     *
     * @param parent The parent view group
     * @param viewType The view type of the new view
     * @return A new ViewHolder
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = CategoryItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Binds category data to an existing view holder.
     *
     * It sets up the category image, name, number of available questions, and a click
     * listener for category selection
     *
     * @param viewHolder The view holder to update
     * @param position Position of the category in the list
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        val numberOfQuestions = categoryStatistics[item.id]?.totalNumOfQuestions

        with(viewHolder.binding) {
            categoryImageView.setImageResource(item.image)
            categoryNameTextView.text = item.name
            numOfQuestionsTextView.text = root.context.getString(
                com.example.factZAP.R.string.num_of_questions,
                numberOfQuestions
            )

            root.setOnClickListener {
                pressed?.onClick(item.id.toInt())
            }
        }
    }
}