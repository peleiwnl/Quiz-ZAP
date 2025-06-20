package com.example.factZAP.data_classes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Represents a trivia question
 *
 * @property type The question type (MCQ/TF)
 * @property difficulty The difficulty of the question (easy, med, hard)
 * @property category the category of the question
 * @property question the question itself
 * @property correctAnswer the correct answer
 * @property incorrectAnswers a list of the incorrect answers
 */
data class TriviaQuestion(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
) : Serializable