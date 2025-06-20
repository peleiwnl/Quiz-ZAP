package com.example.factZAP.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.factZAP.R
import com.example.factZAP.activities.DailyQuizNotificationActivity
import com.example.factZAP.activities.SelectAvatarActivity
import com.example.factZAP.data_classes.User
import com.example.factZAP.databinding.FragmentPreferencesBinding
import com.example.factZAP.firebase.FirebaseSetup
import com.example.factZAP.utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * this BottomSheetDialogFragment allows users to manage their quiz preferences.
 */
class PreferencesBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPreferencesBinding
    private val firebaseSetup = FirebaseSetup()

    /**
     * creates and returns the fragment's view hierarchy.
     * initializes user preferences and sets up UI listeners.
     *
     * @param inflater Layout inflater to inflate views
     * @param container Parent view container
     * @param savedInstanceState Saved state bundle
     * @return The fragment's root view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false)

        loadUserPreferences()
        setupPreferenceListeners()

        return binding.root
    }

    /**
     * Loads and displays the user's current preferences from Firebase, such as the preferred
     * difficulty level, question count, and current avatar
     */
    private fun loadUserPreferences() {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { user ->
                    val difficultyRadioButton = when (user.preferredDifficulty) {
                        "easy" -> binding.radioEasy
                        "medium" -> binding.radioMedium
                        "hard" -> binding.radioHard
                        else -> binding.radioAny
                    }
                    difficultyRadioButton.isChecked = true

                    val questionRadioButton = when (user.preferredQuestionCount) {
                        5 -> binding.radio5
                        15 -> binding.radio15
                        else -> binding.radio10
                    }
                    questionRadioButton.isChecked = true

                    val resourceId =
                        Constants.avatars[user.image.lowercase()] ?: Constants.DEFAULT_AVATAR
                    binding.userProfilePicture.setImageResource(resourceId)
                }
            }
        })
    }

    /**
     * initializes up listeners for preference changes and navigation buttons.
     * it handles difficulty selection, question count selection, avatar change navigation
     * and daily quiz notification settings.
     */
    private fun setupPreferenceListeners() {
        binding.difficultyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val difficulty = when (checkedId) {
                R.id.radio_easy -> "easy"
                R.id.radio_medium -> "medium"
                R.id.radio_hard -> "hard"
                else -> "any"
            }
            updatePreferences(difficulty = difficulty)
        }

        binding.questionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val count = when (checkedId) {
                R.id.radio_5 -> 5
                R.id.radio_15 -> 15
                else -> 10
            }
            updatePreferences(questionCount = count)
        }

        binding.changePictureButton.setOnClickListener {
            startActivity(Intent(requireContext(), SelectAvatarActivity::class.java))
        }

        binding.dailyQuizButton.setOnClickListener {
            startActivity(Intent(requireContext(), DailyQuizNotificationActivity::class.java))
        }
    }

    /**
     * Updates user preferences in Firebase.
     *
     * @param difficulty Optional new difficulty setting
     * @param questionCount Optional new question count setting
     */
    private fun updatePreferences(difficulty: String? = null, questionCount: Int? = null) {
        firebaseSetup.getUserDetails(object : FirebaseSetup.UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        preferredDifficulty = difficulty ?: currentUser.preferredDifficulty,
                        preferredQuestionCount = questionCount ?: currentUser.preferredQuestionCount
                    )
                    firebaseSetup.updateUserPreferences(updatedUser)
                }
            }
        })
    }

    /**
     * creates and configures the bottom sheet dialog.
     *
     * @param savedInstanceState Saved state bundle
     * @return Configured BottomSheetDialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout?

            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                setupHeight(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    /**
     * Configures the bottom sheet to use wrap_content height.
     *
     * @param bottomSheet The bottom sheet view to configure
     */
    private fun setupHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }


    companion object {
        const val TAG = "PreferencesBottomSheet"
    }

    /**
     * Reloads user preferences when the fragment resumes.
     * Ensures displayed preferences are current after potential changes.
     */
    override fun onResume() {
        super.onResume()
        loadUserPreferences()
    }
}
