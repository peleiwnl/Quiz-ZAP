package com.example.factZAP.data_classes

import com.google.gson.annotations.SerializedName

/**
 * Represents statistical data about a category's questions.
 *
 * @property totalNumOfQuestions The total number of questions in the category.
 * @property totalNumOfPendingQuestions The total number of questions that are pending review.
 * @property totalNumOfVerifiedQuestions The total number of questions that have been verified.
 * @property totalNumOfRejectedQuestions The total number of questions that have been rejected.
 */
data class CategoryStatistics(
    @SerializedName("total_num_of_questions") val totalNumOfQuestions: Int,
    @SerializedName("total_num_of_pending_questions") val totalNumOfPendingQuestions: Int,
    @SerializedName("total_num_of_verified_questions") val totalNumOfVerifiedQuestions: Int,
    @SerializedName("total_num_of_rejected_questions") val totalNumOfRejectedQuestions: Int
)