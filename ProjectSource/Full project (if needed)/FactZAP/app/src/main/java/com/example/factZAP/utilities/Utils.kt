package com.example.factZAP.utilities

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.factZAP.R
import com.example.factZAP.data_classes.User
import com.google.android.material.snackbar.Snackbar

/**
 * Utility object providing common UI and data handling functions
 */
object Utils {

    /**
     * Displays a short SnackBar message.
     *
     * @param msg Message to display
     * @param view The view
     */
    fun showSnackBar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Displays a short toast message.
     *
     * @param context Application context
     * @param msg Message to display
     */
    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }



    /**
     * Retrieves the appropriate score value from a UserModel based on score type
     *
     * @param user User model containing score data
     * @param type Type of score to retrieve (defined in Constants)
     * @return The requested score value, or 0 if type is invalid
     */
    fun intendedScore(user: User, type: String):Int{
        return when(type){
            Constants.ALL_TIME_SCORE-> user.allTimeScore
            Constants.WEEKLY_SCORE->user.weeklyScore
            Constants.MONTHLY_SCORE->user.monthlyScore
            else -> 0
        }
    }


    /**
     * Creates and displays a loading progress bar
     *
     * @param context Application context
     * @return The created Dialog instance
     */
    fun showProgressBar(context: Context): Dialog {
        val pb = Dialog(context)
        pb.setContentView(R.layout.loading)
        pb.show()
        return pb
    }


}