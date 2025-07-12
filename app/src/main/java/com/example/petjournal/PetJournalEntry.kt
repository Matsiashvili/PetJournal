package com.example.petjournal

data class PetJournalEntry(
    val id: String? = null,
    val petName: String = "",
    val species: String = "",
    val breed: String = "",
    val date: Long = 0,
    val notes: String = "",
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)