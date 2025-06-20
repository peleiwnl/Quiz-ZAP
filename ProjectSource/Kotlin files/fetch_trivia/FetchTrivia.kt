package com.example.factZAP.fetch_trivia

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.factZAP.activities.TriviaActivity
import com.example.factZAP.data_classes.CategoryStatistics
import com.example.factZAP.data_classes.QuestionStatistics
import com.example.factZAP.data_classes.TriviaResponse
import com.example.factZAP.retrofit_interfaces.QuestionStatisticsService
import com.example.factZAP.retrofit_interfaces.TriviaService
import com.example.factZAP.utilities.Constants
import com.example.factZAP.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class responsible for fetching trivia questions and statistics from OpenTDB API.
 *
 * @property context Application context used for network checks and activity launches
 */
class FetchTrivia(private val context: Context) {

    /**
     * Fetches a quiz from OpenTDB API based on specified parameters.
     *
     * it checks network availability, shows a progress indicator, makes the API request,
     * launches the trivia activity and handles error cases.
     *
     * @param amount Number of questions to fetch
     * @param category Optional category ID to filter questions
     * @param difficulty Optional difficulty level ("easy", "medium", "hard")
     * @param type Optional question type ("multiple", "boolean")
     */
    fun getQuiz(amount: Int, category: Int?, difficulty: String?, type: String?) {

        if (Constants.hasInternet(context)) {
            val pb = Utils.showProgressBar(context)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: TriviaService = retrofit.create(TriviaService::class.java)

            val getTriviaResponseCall: Call<TriviaResponse> = service.getQuiz(
                amount, category, difficulty, type
            )

            getTriviaResponseCall.enqueue(object : Callback<TriviaResponse> {
                override fun onResponse(
                    call: Call<TriviaResponse>,
                    response: Response<TriviaResponse>
                ) {
                    pb.cancel()

                    if (response.isSuccessful) {
                        val responseInformation: TriviaResponse = response.body()!!
                        val listOfQuestions = ArrayList(responseInformation.results)


                        if (listOfQuestions.isNotEmpty()) {
                            Log.d("FetchTrivia", "Starting TriviaActivity...")
                            val intent = Intent(context, TriviaActivity::class.java)
                            intent.putExtra("questionList", listOfQuestions)
                            context.startActivity(intent)
                        } else {
                            Utils.showToast(
                                context, "Sorry, we don't have $amount questions available" +
                                        " for this topic."
                            )
                        }

                    } else {
                        Utils.showToast(context, "Failed, please try again")
                    }
                }

                override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                    pb.cancel()
                    Utils.showToast(context, "Failure in response")
                }

            })
        } else {
            Utils.showToast(
                context, "Please check your network connection" +
                        "and try again"
            )
        }

    }

    /**
     * Fetches statistics about available questions per category.
     *
     * @param callBack Interface implementation to receive the statistics
     */
    fun getQuestionStatsList(callBack: QuestionStatCallback) {
        if (Constants.hasInternet(context)) {
            val pb = Utils.showProgressBar(context)
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: QuestionStatisticsService =
                retrofit.create(QuestionStatisticsService::class.java)
            val getQuestionStatsCall: Call<QuestionStatistics> = service.getData()

            getQuestionStatsCall.enqueue(object : Callback<QuestionStatistics> {
                override fun onResponse(
                    call: Call<QuestionStatistics>,
                    response: Response<QuestionStatistics>
                ) {
                    pb.cancel()
                    if (response.isSuccessful) {
                        val questionStatistics: QuestionStatistics = response.body()!!
                        val mapOfCategories = questionStatistics.categories
                        callBack.onQuestionStatFetched(mapOfCategories)

                    } else {
                        Utils.showToast(context, "Error Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<QuestionStatistics>, response: Throwable) {
                    pb.cancel()
                    Utils.showToast(context, "there was an API call failure")
                }

            })
        } else {
            Utils.showToast(
                context, "Please check your internet connection" +
                        "and try again"
            )
        }
    }

    /**
     * Callback interface for delivering question statistics.
     *
     * The map contains:
     * - Category IDs as keys
     * - [CategoryStatistics] objects as values with detailed category information
     */
    interface QuestionStatCallback {
        fun onQuestionStatFetched(map: Map<String, CategoryStatistics>)
    }


}