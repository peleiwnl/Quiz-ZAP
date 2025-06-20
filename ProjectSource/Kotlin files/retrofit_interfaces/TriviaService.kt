package com.example.factZAP.retrofit_interfaces

import com.example.factZAP.data_classes.TriviaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for interacting with the trivia API.
 */
interface TriviaService {

    /**
     * Makes a GET request to the /api.php endpoint to fetch trivia questions
     *
     * @param amount The number of questions to fetch
     * @param category The category of the questions to fetch
     * @param difficulty The difficulty level of the questions (e.g., "easy", "medium", "hard")
     * @param type The type of questions (e.g., "multiple" or "boolean")
     *
     */
    @GET("api.php")
    fun getQuiz(
        @Query("amount") amount: Int,
        @Query("category") category: Int?,
        @Query("difficulty") difficulty: String?,
        @Query("type") type: String?
    ): Call<TriviaResponse>
}
