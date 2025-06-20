package com.example.factZAP.retrofit_interfaces

import com.example.factZAP.data_classes.QuestionStatistics
import retrofit2.Call
import retrofit2.http.GET

/**
 * Retrofit interface for interacting with the question statistics of the trivia API
 */
interface QuestionStatisticsService {

    /**
     * Makes a GET request to the /api_count_global.php endpoint of the statistics API.
     */
    @GET("api_count_global.php")
    fun getData(): Call<QuestionStatistics>
}
