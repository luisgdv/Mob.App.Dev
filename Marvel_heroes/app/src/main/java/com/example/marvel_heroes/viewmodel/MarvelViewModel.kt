package com.example.marvel_heroes.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marvel_heroes.Hero
import com.example.marvel_heroes.api.MarvelCharacter
import com.example.marvel_heroes.api.RetrofitInstance
import com.example.marvel_heroes.data.HeroDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel for managing Marvel hero data
 * Handles loading, filtering, and searching heroes
 */
class MarvelViewModel(private val database: HeroDatabase) : ViewModel() {
    // LiveData for heroes list
    private val _heroes = MutableLiveData<List<Hero>>()
    val heroes: LiveData<List<Hero>> = _heroes
    
    // Service for API calls
    private val superheroService = RetrofitInstance.superheroService
    
    /**
     * Loads Marvel heroes from the API
     * Filters to only include Marvel Comics heroes
     * Preserves favorite status from the database
     */
    fun loadHeroes() {
        viewModelScope.launch {
            try {
                Log.d("MarvelViewModel", "Making API request to superhero API")
                
                // Get all superheroes from the API
                val response = withContext(Dispatchers.IO) {
                    superheroService.getAllSuperheroes()
                }
                
                Log.d("MarvelViewModel", "API response: ${response.size} heroes")
                
                // Filter to only Marvel heroes
                val marvelHeroes = response.filter { 
                    it.biography.publisher == "Marvel Comics" 
                }
                
                Log.d("MarvelViewModel", "Filtered to ${marvelHeroes.size} Marvel heroes")
                
                // Convert SuperheroResponse to Hero objects
                val heroesFromApi = marvelHeroes.map { superhero ->
                    Hero(
                        id = superhero.id,
                        name = superhero.name,
                        description = "Intelligence: ${superhero.powerstats.intelligence}, Strength: ${superhero.powerstats.strength}",
                        imageUrl = superhero.images.lg,
                        comicsCount = superhero.biography.aliases.size,
                        isFavorite = false
                    )
                }
                
                // Preserve favorite status from database
                val updatedHeroes = withContext(Dispatchers.IO) {
                    heroesFromApi.map { hero ->
                        val existingHero = database.heroDao().getHeroById(hero.id)
                        if (existingHero != null && existingHero.isFavorite) {
                            hero.copy(isFavorite = true)
                        } else {
                            hero
                        }
                    }
                }
                
                Log.d("MarvelViewModel", "Mapped ${updatedHeroes.size} heroes")
                _heroes.value = updatedHeroes
                
            } catch (e: HttpException) {
                Log.e("MarvelViewModel", "HTTP error: ${e.code()}", e)
                Log.e("MarvelViewModel", "Error body: ${e.response()?.errorBody()?.string()}")
            } catch (e: IOException) {
                Log.e("MarvelViewModel", "Network error", e)
            } catch (e: Exception) {
                Log.e("MarvelViewModel", "Error loading heroes", e)
            }
        }
    }
    
    /**
     * Toggles the favorite status of a hero
     * Updates both the local model and the database
     * @param hero The hero to toggle favorite status
     */
    fun toggleFavorite(hero: Hero) {
        viewModelScope.launch {
            hero.isFavorite = !hero.isFavorite
            database.heroDao().upsertHero(hero)
            
            // Refresh the list to show updated favorite status
            _heroes.value = _heroes.value?.map {
                if (it.id == hero.id) hero else it
            }
        }
    }
    
    /**
     * Maps a MarvelCharacter to a Hero object
     * @param character The MarvelCharacter to convert
     * @return A Hero object with data from the MarvelCharacter
     */
    private fun mapMarvelCharacterToHero(character: MarvelCharacter): Hero {
        return Hero(
            id = character.id,
            name = character.name,
            description = character.description,
            imageUrl = "${character.thumbnail.path}.${character.thumbnail.extension}",
            comicsCount = 0,
            isFavorite = false
        )
    }
    
    /**
     * Searches heroes by name
     * @param query The search query
     */
    fun searchHeroes(query: String) {
        if (query.isBlank()) {
            // If query is empty, load all heroes
            loadHeroes()
            return
        }
        
        viewModelScope.launch {
            try {
                // First try to search from local database
                val allHeroes = database.heroDao().getAllHeroes()
                val filteredHeroes = allHeroes.filter { 
                    it.name.contains(query, ignoreCase = true) 
                }
                
                _heroes.value = filteredHeroes
                
                // If we have few or no results, try to search from API
                if (filteredHeroes.size < 5) {
                    // This would require API support for search
                    // For now, we'll just use the local filtering
                } else {
                    Log.d("MarvelViewModel", "Found ${filteredHeroes.size} results for query: $query")
                }
            } catch (e: Exception) {
                Log.e("MarvelViewModel", "Error searching heroes", e)
            }
        }
    }
    
    /**
     * Filters heroes by name in ascending order (A-Z)
     */
    fun filterHeroesByNameAscending() {
        viewModelScope.launch {
            _heroes.value = _heroes.value?.sortedBy { it.name }
            Log.d("MarvelViewModel", "Filtered heroes by name A-Z")
        }
    }
    
    /**
     * Filters heroes by name in descending order (Z-A)
     */
    fun filterHeroesByNameDescending() {
        viewModelScope.launch {
            _heroes.value = _heroes.value?.sortedByDescending { it.name }
            Log.d("MarvelViewModel", "Filtered heroes by name Z-A")
        }
    }
    
    /**
     * Filters heroes by intelligence in ascending order (0-100)
     */
    fun filterHeroesByIntelligenceAscending() {
        viewModelScope.launch {
            _heroes.value = _heroes.value?.sortedBy { 
                // Extract intelligence value from description
                val match = "Intelligence: (\\d+)".toRegex().find(it.description)
                match?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
            Log.d("MarvelViewModel", "Filtered heroes by intelligence 0-100")
        }
    }
    
    /**
     * Filters heroes by intelligence in descending order (100-0)
     */
    fun filterHeroesByIntelligenceDescending() {
        viewModelScope.launch {
            _heroes.value = _heroes.value?.sortedByDescending { 
                // Extract intelligence value from description
                val match = "Intelligence: (\\d+)".toRegex().find(it.description)
                match?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
            Log.d("MarvelViewModel", "Filtered heroes by intelligence 100-0")
        }
    }
    
    /**
     * Filters heroes by strength in ascending order (0-100)
     */
    fun filterHeroesByStrengthAscending() {
        viewModelScope.launch {
            _heroes.value = _heroes.value?.sortedBy { 
                // Extract strength value from description
                val match = "Strength: (\\d+)".toRegex().find(it.description)
                match?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
            Log.d("MarvelViewModel", "Filtered heroes by strength 0-100")
        }
    }
    
    /**
     * Filters heroes by strength in descending order (100-0)
     */
    fun filterHeroesByStrengthDescending() {
        viewModelScope.launch {
            _heroes.value = _heroes.value?.sortedByDescending { 
                // Extract strength value from description
                val match = "Strength: (\\d+)".toRegex().find(it.description)
                match?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
            Log.d("MarvelViewModel", "Filtered heroes by strength 100-0")
        }
    }
}