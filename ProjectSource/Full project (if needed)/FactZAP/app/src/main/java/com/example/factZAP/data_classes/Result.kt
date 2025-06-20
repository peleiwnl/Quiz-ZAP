package com.example.factZAP.data_classes

import java.io.Serializable

/**
 * Represents the result of a completed quiz
 *
 * @property time The total time taken to complete the question
 * @property type The type of question
 * @property difficulty the difficulty of the question
 * @property score the score from the question
 */
data class Result(
    val time: Int,
    val type: String,
    val difficulty: String,
    val score: Int
) : Serializable
