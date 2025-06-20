package com.example.factZAP.data_classes

/**
 * Represents a saved fact
 *
 * @property factNumber The fact's associated unique number
 * @property factText the fact itself
 * @property savedDate when the fact was saved to the user's profile
 */
data class SavedFact(
    val factNumber: Int = 0,
    val factText: String = "",
    val savedDate: String = ""
)