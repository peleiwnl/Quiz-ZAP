package com.example.factZAP.data_classes

/**
 * Represents a category in the application.
 *
 * @property id The unique identifier for the category.
 * @property image The resource ID of the image representing the category.
 * @property name The display name of the category.
 */
data class Category(
    val id: String,
    val image: Int,
    val name: String
)
