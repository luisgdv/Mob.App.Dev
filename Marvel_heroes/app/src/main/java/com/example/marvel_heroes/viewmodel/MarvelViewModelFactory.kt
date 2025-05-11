package com.example.marvel_heroes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marvel_heroes.data.HeroDatabase

/**
 * Factory for creating MarvelViewModel instances
 * Allows passing dependencies to the ViewModel
 */
class MarvelViewModelFactory(private val database: HeroDatabase) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the specified ViewModel class
     * @param modelClass The class of the ViewModel to create
     * @return A new instance of the ViewModel
     * @throws IllegalArgumentException if the ViewModel class is unknown
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarvelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarvelViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}