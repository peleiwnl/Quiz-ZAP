package com.example.factZAP.data_classes

/**
 * Represents statistics for questions, such as the overall stats and the categories
 *
 * @property overall The overall statistics for all questions.
 * @property categories A map of category-specific statistics,
 * where the key is the category ID or name,
 * and the value is the [CategoryStatistics] for that category.
 */
data class QuestionStatistics(
    val overall: OverallStatistics,
    val categories: Map<String, CategoryStatistics>
)



