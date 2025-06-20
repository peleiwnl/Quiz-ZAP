package com.example.factZAP.data_classes

import com.google.gson.annotations.SerializedName

/**
 * Represents overall statistics for questions in the application.
 *
 * @property totalNumOfQuestions The total number of questions in the application.
 * @property totalNumOfPendingQuestions The total number of questions currently pending review.
 * @property totalNumOfVerifiedQuestions The total number of questions that have been verified.
 * @property totalNumOfRejectedQuestions The total number of questions that have been rejected.
 */
data class OverallStatistics(
    @SerializedName("total_num_of_questions") val totalNumOfQuestions: Int,
    @SerializedName("total_num_of_pending_questions") val totalNumOfPendingQuestions: Int,
    @SerializedName("total_num_of_verified_questions") val totalNumOfVerifiedQuestions: Int,
    @SerializedName("total_num_of_rejected_questions") val totalNumOfRejectedQuestions: Int
)