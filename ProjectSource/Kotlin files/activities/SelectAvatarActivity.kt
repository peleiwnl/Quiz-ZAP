
package com.example.factZAP.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.factZAP.R
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.ActivitySelectAvatarBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.recycler_view_adapters.AvatarAdapter

/**
 *  Selecting and updating a user's avatar.
 */
class SelectAvatarActivity : AppCompatActivity() {

    /** accessing the activity's views using binding */
    private var binding: ActivitySelectAvatarBinding? = null

    /** handling user data and preferences with firebase */
    private val firebaseSetup = FirebaseSetup()

    /**
     * Initializes the activity and sets up the UI components.
     * it sets up the view binding, toolbar with back navigation and retrieves
     * the current user info to display the appropriate avatar selection.
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAvatarBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarTitle?.text = getString(R.string.select_avatar_title)

        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    setupRecyclerView(user.image)
                }
            }
        })
    }

    /**
     * Sets up the RecyclerView with a grid of avatar options.
     *
     * @param currentAvatar The currently selected avatar
     */
    private fun setupRecyclerView(currentAvatar: String) {
        val adapter = AvatarAdapter(currentAvatar) { selectedAvatar ->
            firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
                override fun onUserDetailsFetched(userInfo: User?) {
                    userInfo?.let { user ->
                        val updatedUser = user.copy(image = selectedAvatar)
                        firebaseSetup.updateUserPreferences(updatedUser)
                    }
                }
            })
        }

        binding?.avatarsRecyclerView?.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(this@SelectAvatarActivity, 3)
            //3 columns
        }
    }

    /**
     * handles the back button
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