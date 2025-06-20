
package com.example.factZAP.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.factZAP.R
import com.example.factZAP.databinding.ActivityMainBinding
import com.example.factZAP.fragments.HomeFragment
import com.example.factZAP.fragments.LeaderboardFragment
import com.example.factZAP.fragments.ProfileFragment

/**
 * the main activity handles navigation between the main fragments, home,
 * leaderboard, and profile, whilst configuring the toolbar with the custom title
 * and logo
 */
class MainActivity : AppCompatActivity() {

    /** accessing the activity's views with binding */
    private var binding: ActivityMainBinding? = null

    /**
     * Initializes the activity, sets up navigation and default fragment.
     *
     * it sets up the view binding, toolbar configuration, the bottom nav menu and
     * the initial home fragment
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val homeFragment = HomeFragment()
        val socialFragment = LeaderboardFragment()
        val profileFragment = ProfileFragment()

        setSupportActionBar(binding?.toolbar)

        setCurrentFragment(homeFragment, "Home")

        binding?.bottomNavigationMenu?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setCurrentFragment(homeFragment, "Home")
                }
                R.id.leaderboard -> {
                    setCurrentFragment(socialFragment, "Social")
                }
                R.id.profile -> {
                    setCurrentFragment(profileFragment, "Profile")
                }
            }
            true
        }
    }

    /**
     * changes the current fragment and updates the toolbar.
     *
     * @param fragment The fragment to display
     * @param title Optional title to show in the toolbar. If null, no title will be shown
     */
    private fun setCurrentFragment(fragment: Fragment, title: String? = null) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

        binding?.toolbarTitle?.text = title ?: ""
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.app_logo_png)
        }
    }

    /**
     * cleans up the binding when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}