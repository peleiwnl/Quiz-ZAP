
package com.example.factZAP.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.factZAP.R
import com.example.factZAP.databinding.ActivityRandomFactBinding
import com.example.factZAP.fetch_trivia.FetchFact
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.utilities.Utils

/**
 * Activity for displaying and saving random facts.
 */
class RandomFactActivity : AppCompatActivity(), FetchFact.FactCallback {

    /** accessing the activity's views using view binding */
    private var binding: ActivityRandomFactBinding? = null

    /** Stores the current fact's number */
    private var currentFactNumber: Int = 0

    /** Stores the current fact's text */
    private var currentFactText: String = ""

    /** Tracks whether the current fact has been saved to prevent duplication */
    private var isCurrentFactSaved = false

    /**
     * initializes the activity and sets up the UI components.
     * it sets up the toolbar with back navigation, the save button and the initial fact
     * fetch.
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRandomFactBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.random_fact_title)

        binding?.saveFact?.setOnClickListener {
            if (!isCurrentFactSaved) {
                FirebaseSetup().saveFact(currentFactNumber, currentFactText)
                isCurrentFactSaved = true
            } else {
                binding?.root?.let { root ->
                    Utils.showSnackBar(root, "You have already saved this fact!")
                }
            }

        }


        FetchFact(this).getFact(this)
    }

    /**
     * callback for when a fact is successfully fetched.
     * Updates the UI with the new fact information.
     *
     * @param factNumber The unique identifier of the fact
     * @param fact The text content of the fact
     */
    override fun onFactFetched(factNumber: Int, fact: String) {
        currentFactNumber = factNumber
        currentFactText = fact
        binding?.factNumber?.text = getString(R.string.fact_zap_number, factNumber)
        binding?.randomFact?.text = fact
    }

    /**
     * handles the back button.
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
}