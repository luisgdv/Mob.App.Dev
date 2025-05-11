package com.example.marvel_heroes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.marvel_heroes.Hero

/**
 * Room database for storing hero information
 * Provides access to the hero DAO
 */
@Database(entities = [Hero::class], version = 1, exportSchema = false)
abstract class HeroDatabase : RoomDatabase() {
    /**
     * Returns the DAO for accessing hero data
     * @return The hero DAO
     */
    abstract fun heroDao(): HeroDao
}