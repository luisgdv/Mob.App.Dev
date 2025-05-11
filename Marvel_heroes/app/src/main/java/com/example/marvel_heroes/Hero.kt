package com.example.marvel_heroes

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Entity class representing a Marvel hero
 * Used for database storage and UI display
 */
@Entity(tableName = "heroes")
@Parcelize
data class Hero(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val comicsCount: Int,
    var isFavorite: Boolean = false  // Mutable to allow changing favorite status
) : Parcelable {
    // Parcelable implementation handled by @Parcelize annotation
}