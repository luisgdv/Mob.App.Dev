package com.example.marvel_heroes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.marvel_heroes.Hero

/**
 * Data Access Object (DAO) for Hero entities
 * Provides methods to interact with the heroes table in the database
 */
@Dao
interface HeroDao {
    /**
     * Retrieves all heroes from the database
     * @return List of all heroes
     */
    @Query("SELECT * FROM heroes")
    suspend fun getAllHeroes(): List<Hero>
    
    /**
     * Retrieves a specific hero by ID
     * @param id The hero's unique identifier
     * @return The hero with the specified ID, or null if not found
     */
    @Query("SELECT * FROM heroes WHERE id = :id")
    suspend fun getHeroById(id: Int): Hero?
    
    /**
     * Retrieves all favorite heroes from the database
     * @return List of heroes marked as favorites
     */
    @Query("SELECT * FROM heroes WHERE isFavorite = 1")
    suspend fun getAllFavorites(): List<Hero>
    
    /**
     * Inserts or updates a hero in the database
     * If a hero with the same ID already exists, it will be replaced
     * @param hero The hero to insert or update
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHero(hero: Hero)
    
    /**
     * Inserts a hero into the database
     * If a hero with the same ID already exists, it will be replaced
     * @param hero The hero to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHero(hero: Hero)
    
    /**
     * Updates an existing hero in the database
     * @param hero The hero to update
     */
    @Update
    suspend fun updateHero(hero: Hero)
}