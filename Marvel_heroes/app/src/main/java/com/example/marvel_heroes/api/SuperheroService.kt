package com.example.marvel_heroes.api

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service interface for the Superhero API
 * Provides methods to fetch superhero data
 */
interface SuperheroService {
    companion object {
        /**
         * Base URL for the Superhero API
         */
        const val BASE_URL = "https://cdn.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/"
    }
    
    /**
     * Fetches all superheroes from the API
     * @return List of superhero data
     */
    @GET("all.json")
    suspend fun getAllSuperheroes(): List<SuperheroResponse>
    
    /**
     * Fetches detailed biography for a specific hero
     * @param id The hero's unique identifier
     * @return Biography data for the requested hero
     */
    @GET("biography/{id}.json")
    suspend fun getHeroBiography(@Path("id") id: Int): Biography
}

/**
 * Data class representing a complete superhero response from the API
 */
data class SuperheroResponse(
    val id: Int,
    val name: String,
    val powerstats: PowerStats,
    val biography: Biography,
    val appearance: Appearance,
    val work: Work,
    val connections: Connections,
    val images: Images
)

/**
 * Data class representing a hero's power statistics
 */
data class PowerStats(
    val intelligence: Int,
    val strength: Int,
    val speed: Int,
    val durability: Int,
    val power: Int,
    val combat: Int
)

/**
 * Data class representing a hero's biographical information
 */
data class Biography(
    val fullName: String,
    val alterEgos: String,
    val aliases: List<String>,
    val placeOfBirth: String,
    val firstAppearance: String,
    val publisher: String,
    val alignment: String
)

/**
 * Data class representing a hero's physical appearance
 */
data class Appearance(
    val gender: String,
    val race: String,
    val height: List<String>,
    val weight: List<String>,
    val eyeColor: String,
    val hairColor: String
)

/**
 * Data class representing a hero's work information
 */
data class Work(
    val occupation: String,
    val base: String
)

/**
 * Data class representing a hero's connections to other characters
 */
data class Connections(
    val groupAffiliation: String,
    val relatives: String
)

/**
 * Data class representing a hero's images in various sizes
 */
data class Images(
    val xs: String, // Extra small image
    val sm: String, // Small image
    val md: String, // Medium image
    val lg: String  // Large image
)