package com.example.factZAP.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.text.HtmlCompat
import com.example.factZAP.R
import com.example.factZAP.data_classes.Category

/**
 * Utility object containing constants and helper functions
 */
object Constants {

    /**
     * Firebase collection name for user data.
     */
    const val USER = "USER"

    /**
     * Firebase field names for different score types.
     */
    const val ALL_TIME_SCORE = "allTimeScore"
    const val WEEKLY_SCORE = "weeklyScore"
    const val MONTHLY_SCORE = "monthlyScore"
    const val LAST_GAME_SCORE = "lastGameScore"

    /**
     * List of available quiz difficulty levels.
     */
    val difficulties = listOf("Any", "Easy", "Medium", "Hard")

    /**
     * List of available question types.
     */
    val types = listOf("Any", "Multiple Choice", "True/false")


    /**
     * Maps notification times (in 24-hour format) to radio button resource IDs.
     */
    val NOTIFICATION_TIME_RADIO_BUTTONS = mapOf(
        8 to R.id.time_8am,
        10 to R.id.time_10am,
        12 to R.id.time_12pm,
        14 to R.id.time_2pm,
        16 to R.id.time_4pm,
        18 to R.id.time_6pm,
        20 to R.id.time_8pm,
        -1 to R.id.when_available
    )

    /**
     * generates a list of all available trivia categories.
     * Each category contains an ID, image resource, and display name.
     *
     * @return ArrayList of Category objects
     */
    fun getCategories(): ArrayList<Category> {
        val categoryList = listOf(
            Pair("9", Pair(R.drawable.general_knowledge, "General Knowledge")),
            Pair("10", Pair(R.drawable.books, "Books")),
            Pair("11", Pair(R.drawable.movies, "Film")),
            Pair("12", Pair(R.drawable.music, "Music")),
            Pair("13", Pair(R.drawable.musicals, "Musicals & Theatres")),
            Pair("14", Pair(R.drawable.television, "Television")),
            Pair("15", Pair(R.drawable.video_games, "Video Games")),
            Pair("16", Pair(R.drawable.board_game, "Board Games")),
            Pair("17", Pair(R.drawable.science, "Science & Nature")),
            Pair("18", Pair(R.drawable.computer, "Computers")),
            Pair("19", Pair(R.drawable.maths, "Mathematics")),
            Pair("20", Pair(R.drawable.mythology, "Mythology")),
            Pair("21", Pair(R.drawable.sports, "Sports")),
            Pair("22", Pair(R.drawable.geography, "Geography")),
            Pair("23", Pair(R.drawable.history, "History")),
            Pair("24", Pair(R.drawable.politics, "Politics")),
            Pair("25", Pair(R.drawable.art, "Art")),
            Pair("26", Pair(R.drawable.celebrities, "Celebrities")),
            Pair("27", Pair(R.drawable.animals, "Animals")),
            Pair("28", Pair(R.drawable.vehicles, "Vehicles")),
            Pair("29", Pair(R.drawable.comic, "Comics")),
            Pair("30", Pair(R.drawable.gadgets, "Gadgets")),
            Pair("31", Pair(R.drawable.anime, "Anime & Manga")),
            Pair("32", Pair(R.drawable.cartoon, "Cartoons & Animations"))
        )

        return ArrayList(categoryList.map { Category(it.first, it.second.first, it.second.second) })
    }

    /**
     * this randomizes answer options for a quiz question.
     * it decodes HTML entities and shuffles correct and incorrect answers.
     *
     * @param rightAnswer The correct answer string
     * @param wrongAnswers List of incorrect answer strings
     * @return Pair of original correct answer and shuffled list of all answers
     */
    fun randomizeOptions(
        rightAnswer: String,
        wrongAnswers: List<String>
    ): Pair<String, List<String>> {
        val answersList = mutableListOf<String>()

        answersList.add(decodeString(rightAnswer))

        for (i in wrongAnswers) {
            answersList.add(decodeString(i))
        }

        answersList.shuffle()
        return Pair(rightAnswer, answersList)
    }

    /**
     * gets a list of category names
     *
     * @return List of category names
     */
    fun getCategoryArray(): List<String> {
        val categories = getCategories()
        val result = mutableListOf<String>()
        result.add("Any")
        for (i in categories)
            result.add(i.name)
        return result
    }

    /**
     * checks if network connectivity is available.
     *
     * @param context Application context
     * @return true if network is available, false otherwise
     */
    fun hasInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    /**
     * decodes HTML-encoded strings.
     *
     * @param htmlEncoded HTML-encoded string
     * @return Decoded string
     */
    fun decodeString(htmlEncoded: String): String {
        return HtmlCompat.fromHtml(htmlEncoded, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    /**
     * Maps avatar identifiers to their drawable resource IDs.
     */
    val avatars = mapOf(
        "default" to R.drawable.default_picture,
        "avatar_1" to R.drawable.avatar_1,
        "avatar_2" to R.drawable.avatar_2,
        "avatar_3" to R.drawable.avatar_3,
        "avatar_4" to R.drawable.avatar_4,
        "avatar_5" to R.drawable.avatar_5,
        "avatar_6" to R.drawable.avatar_6,
        "avatar_7" to R.drawable.avatar_7
    )

    /**
     * Default avatar drawable resource ID.
     */
    val DEFAULT_AVATAR = R.drawable.default_picture

    /**
     * List of available avatar identifiers.
     */
    val avatarNames = avatars.keys.toList()

}