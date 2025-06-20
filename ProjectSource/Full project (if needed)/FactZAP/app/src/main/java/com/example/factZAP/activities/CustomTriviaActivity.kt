
package com.example.factZAP.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.factZAP.R
import com.example.factZAP.databinding.ActivityCustomQuizBinding
import com.example.factZAP.fetch_trivia.FetchTrivia
import com.example.factZAP.utilities.Constants
import com.example.factZAP.utilities.SimpleOnItemSelectedListener
import com.example.factZAP.utilities.SimpleOnSeekBarChangeListener

/**
 * Activity that allows users to customize and start a customized quiz.
 */
class CustomTriviaActivity : AppCompatActivity() {

    /** View binding instance for accessing the activity's views */
    private var binding: ActivityCustomQuizBinding? = null

    /** Number of questions selected for the quiz, the default is 10 */
    private var amount = 10

    /** Selected quiz category ID (null means any) */
    private var category: Int? = null

    /** Selected difficulty level */
    private var difficulty: String? = null

    /** Selected question type */
    private var type: String? = null

    /**
     * Initializes the activity, sets up the UI components and their listeners.
     *
     * it creates:
     * - the toolbar with back navigation
     * - the question amount SeekBar
     * - the category, difficulty, and type spinners
     * - Start quiz button
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomQuizBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSeekbar()
        setSupportActionBar(binding?.toolbar)

        //toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarTitle?.text = getString(R.string.custom_quiz_title)

        val categories = Constants.getCategoryArray()
        binding?.categorySpinner?.adapter = setSpinner(categories)
        binding?.difficultySpinner?.adapter = setSpinner(Constants.difficulties)
        binding?.typeSpinner?.adapter = setSpinner(Constants.types)

        setCategorySpinner()
        setDifficultySpinner()
        setTypeSpinner()

        binding?.startCustomQuiz?.setOnClickListener {
            FetchTrivia(this).getQuiz(amount, category, difficulty, type)
        }
    }

    /**
     * Handles the back button
     *
     * @param item The menu item that was clicked
     * @return true if the event was handled, false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * This initializes the category spinner
     * it updates the [category] property based on the selected position.
     */
    private fun setCategorySpinner() {
        binding?.categorySpinner?.onItemSelectedListener =
            object : SimpleOnItemSelectedListener() {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    category = if (position == 0) null else position + 8 //categories start at 8
                }
            }
    }

    /**
     * This initializes the difficulty spinner
     * it updates the [difficulty] property based on the selected position.
     */
    private fun setDifficultySpinner() {
        binding?.difficultySpinner?.onItemSelectedListener =
            object : SimpleOnItemSelectedListener() {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    difficulty = when (position) {
                        0 -> null //any difficulty
                        1 -> "easy"
                        2 -> "medium"
                        else -> "hard"
                    }
                }
            }
    }

    /**
     * This initializes the type spinner
     * it updates the [type] property based on the selected position.
     */
    private fun setTypeSpinner() {
        binding?.typeSpinner?.onItemSelectedListener =
            object : SimpleOnItemSelectedListener() {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    type = when (position) {
                        0 -> null //any
                        1 -> "multiple"
                        else -> "boolean"
                    }
                }
            }
    }

    /**
     * initializes the Seekbar for selecting the number of questions.
     * it updates the [amount] property and displays the selected value.
     */
    private fun setSeekbar() {
        binding?.seekBarAmount?.setOnSeekBarChangeListener(object :
            SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                amount = progress
                val amountTextView = "Amount: $progress"
                binding?.amountTextView?.text = amountTextView
            }
        })
    }

    /**
     * creates and configures a spinner adapter for the provided list of items.
     *
     * @param list The list of items to display in the spinner
     * @return Configured SpinnerAdapter instance
     */
    private fun setSpinner(list: List<String>): SpinnerAdapter {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
}