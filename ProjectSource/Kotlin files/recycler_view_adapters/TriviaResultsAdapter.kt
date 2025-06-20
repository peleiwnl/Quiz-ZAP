package com.example.factZAP.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.factZAP.data_classes.Result
import com.example.factZAP.databinding.QuestionStatsBinding
import java.util.Locale

/**
 * RecyclerView adapter for displaying trivia question results
 * Shows statistics for each question including number, time taken, type, difficulty, and score.
 *
 * @property list List of question results to display
 */
class TriviaResultsAdapter(private val list: ArrayList<Result>) :
    RecyclerView.Adapter<TriviaResultsAdapter.ViewHolder>() {

    /**
     * ViewHolder for question result items.
     *
     * @property binding View binding for the question stats layout
     */
    class ViewHolder(val binding: QuestionStatsBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * creates new view holders for the RecyclerView.
     *
     * @param parent The parent view group
     * @param viewType The view type of the new view
     * @return A new ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuestionStatsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Binds question result data to an existing view holder.
     *
     * this initializes the question number, time taken to answer, question type,
     * difficulty level, and score achieved
     *
     * @param holder The view holder to update
     * @param position Position of the question in the list
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        with(holder.binding) {
            questionNumber.text = String.format(
                Locale.getDefault(),
                "%d.", position + 1
            )

            questionTime.text = String.format(
                Locale.getDefault(),
                "%d", item.time
            )

            questionType.text = when (item.type) {
                "boolean" -> "true/false"
                else -> item.type
            }

            questionDifficulty.text = item.difficulty
            questionScore.text = String.format(Locale.getDefault(), "%d", item.score)
        }
    }

    /**
     * this returns the total number of question results.
     *
     * @return Size of the results list
     */
    override fun getItemCount(): Int {
        return list.size
    }
}