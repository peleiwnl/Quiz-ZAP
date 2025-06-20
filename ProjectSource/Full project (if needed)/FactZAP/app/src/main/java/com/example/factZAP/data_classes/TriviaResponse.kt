package com.example.factZAP.data_classes

import com.google.gson.annotations.SerializedName

/**
 * Represents the response received from a trivia API call.
 *
 * @property responseCode The status code of the API response.
 * A value of 0 indicates success
 * @property results The list of trivia questions returned in the response.
 */
data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    val results: List<TriviaQuestion>
)


