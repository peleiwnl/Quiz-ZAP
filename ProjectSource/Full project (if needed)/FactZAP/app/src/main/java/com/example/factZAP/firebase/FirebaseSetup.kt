package com.example.factZAP.firebase

import android.widget.ImageView
import com.example.factZAP.R
import com.example.factZAP.data_classes.LeaderBoardModel
import com.example.factZAP.data_classes.SavedFact
import com.example.factZAP.data_classes.User
import com.example.factZAP.utilities.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

/**
 * Handles Firebase operations for the application including user management,
 * profile operations, scoring, and leaderboard functionality.
 */
class FirebaseSetup {

    private val fireStore = FirebaseFirestore.getInstance()



    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise.
     */
    fun isUserLoggedIn(): Boolean {
        val currentUser = Firebase.auth.currentUser
        return currentUser != null
    }

    /**
     * Registers or updates a user in Fire store with the provided user information.
     *
     * @param userDetails The user model containing the information to be stored.
     */
    fun registerUser(userDetails: User) {
        fireStore.collection(Constants.USER)
            .document(getUser()).set(userDetails, SetOptions.merge())
    }


    /**
     * Retrieves the current user's information from Fire store.
     *
     * @param callback Callback to handle the fetched user information.
     */
    fun getUserDetails(callback: UserInfoCallback) {
        val fireStoreUser = FirebaseFirestore.getInstance().
        collection(Constants.USER).document(getUser())

        fireStoreUser.get().
        addOnSuccessListener { documentSnapshot -> val userDetails =
            documentSnapshot.toObject(User::class.java)

            callback.onUserDetailsFetched(userDetails)
        }.addOnFailureListener {

            callback.onUserDetailsFetched(null)
        }
    }


    /**
     * Retrieves the user by their ID
     *
     * @return the user's ID
     */
    private fun getUser(): String {
        val currentUser = Firebase.auth.currentUser
        var currentUserId = ""
        if (currentUser != null)
            currentUserId = currentUser.uid
        return currentUserId
    }



    /**
     * Sets a user's profile image
     *
     * @param imageRef The reference to the image.
     * @param view The ImageView to display the profile image.
     */
    fun setProfileImage(imageRef: String?, view: ImageView) {
        if (!imageRef.isNullOrEmpty() && imageRef.startsWith("avatar_")) {
            val resourceId = Constants.avatars[imageRef.lowercase()] ?: Constants.DEFAULT_AVATAR
            view.setImageResource(resourceId)
        } else {
            view.setImageResource(R.drawable.default_picture)
        }
    }


    /**
     * Updates a user's score across different categories (all-time, weekly, monthly).
     *
     * @param newScore The score to be added to the user's existing scores.
     */
    fun updateUserScore(newScore: Int) {
        val fireStoreUser = fireStore.collection(Constants.USER).document(getUser())
        getUserDetails(object : UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let {
                    val weeklyScore = userInfo.weeklyScore + newScore
                    val monthlyScore = userInfo.monthlyScore + newScore
                    val allTimeScore = userInfo.allTimeScore + newScore
                    fireStoreUser.update(
                        Constants.WEEKLY_SCORE, weeklyScore,
                        Constants.MONTHLY_SCORE, monthlyScore,
                        Constants.ALL_TIME_SCORE, allTimeScore,
                        Constants.LAST_GAME_SCORE, newScore
                    )
                }
            }
        })
    }

    /**
     * Retrieves the user's rank.
     *
     * @param type The type e.g. monthlyScore or weeklyScore.
     * @param callback Callback to handle the fetched rank information.
     */
    fun getRank(type: String, callback:UserRankCallback){
        var userRank: Int?
        fireStore.collection(Constants.USER)
            .orderBy(type, Query.Direction.DESCENDING)
            .get().addOnSuccessListener { successResult ->
                userRank = 1
                val user = getUser()
                for (document in successResult) {
                    if (document.id == user)
                        break
                    userRank = userRank!! + 1
                }
                callback.onUserRankFetched(userRank)
            }.addOnFailureListener {
                callback.onUserRankFetched(null)
            }
    }


    /**
     * Retrieves leaderboard data for the specific type.
     *
     * @param type The type e.g. weeklyScore
     * @param callBack Callback to handle the fetched leaderboard data.
     */
    fun getLeaderBoardData(type: String, callBack: LeaderBoardDataCallback) {
        val scoreManagement = ScoreManagement()
        scoreManagement.checkAndResetScores {
            fireStore.collection(Constants.USER)
                .orderBy(type, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val allRanks = mutableListOf<User?>()
                    for (document in result) {
                        val userDetails = document.toObject(User::class.java)
                        allRanks.add(userDetails)
                    }
                    callBack.onLeaderBoardDataFetched(LeaderBoardModel(allRanks))
                }
                .addOnFailureListener {
                    callBack.onLeaderBoardDataFetched(null)
                }
        }
    }


    /**
     * Updates user preferences in Fire store.
     *
     * @param userInfo Updated user model containing the new preferences.
     */
    fun updateUserPreferences(userInfo: User) {
        fireStore.collection(Constants.USER)
            .document(getUser())
            .set(userInfo, SetOptions.merge())
    }


    /**
     * Saves a fact to the user's saved facts collection.
     *
     * @param factNumber The unique identifier of the fact.
     * @param factText The content of the fact to be saved.
     */
    fun saveFact(factNumber: Int, factText: String) {
        val userDocument = fireStore.collection(Constants.USER).document(getUser())

        getUserDetails(object : UserInfoCallback {
            override fun onUserDetailsFetched(userInfo: User?) {
                userInfo?.let {
                    val currentDate = java.text.SimpleDateFormat("dd MMM yyyy",
                        java.util.Locale.getDefault()).format(java.util.Date())

                    val newFact = SavedFact(factNumber, factText, currentDate)
                    val updatedFacts = (userInfo.savedFacts + newFact)

                    userDocument.update("savedFacts", updatedFacts)
                }
            }
        })
    }

    /**
     * Interface for handling user information fetch callbacks.
     */
    interface UserInfoCallback {
        /**
         * Called when user information is fetched from Fire store.
         *
         * @param userInfo The fetched user information, null if fetch failed.
         */
        fun onUserDetailsFetched(userInfo: User?)
    }

    /**
     * Interface for handling user rank fetch callbacks.
     */
    interface UserRankCallback {
        /**
         * Called when user rank is fetched.
         *
         * @param rank The user's rank, null if fetch failed.
         */
        fun onUserRankFetched(rank:Int?)
    }

    /**
     * Interface for handling leaderboard data fetch callbacks.
     */
    interface LeaderBoardDataCallback{
        /**
         * Called when leaderboard data is fetched.
         *
         * @param leaderBoardModel The leaderboard data, null if fetch failed.
         */
        fun onLeaderBoardDataFetched(leaderBoardModel: LeaderBoardModel?)
    }





}