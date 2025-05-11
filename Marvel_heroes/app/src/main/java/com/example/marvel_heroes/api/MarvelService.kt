package com.example.marvel_heroes.api

import retrofit2.http.GET
import retrofit2.http.Query

data class MarvelResponse(
    val code: Int,
    val status: String,
    val data: DataContainer
)

data class DataContainer(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<MarvelCharacter>
)

data class MarvelCharacter(
    val id: Int,
    val name: String,
    val description: String,
    val thumbnail: Thumbnail,
    val comics: ComicList
)

data class Thumbnail(
    val path: String,
    val extension: String
)

data class ComicList(
    val available: Int,
    val items: List<ComicSummary>
)

data class ComicSummary(
    val resourceURI: String,
    val name: String
)

interface MarvelService {
    companion object {
        const val BASE_URL = "https://gateway.marvel.com/v1/public/"
        const val PUBLIC_KEY = "124adf6a8131fc4b2930f93d647c15d3"
        const val PRIVATE_KEY = "90fc1d61677d55fdde9f160c5fa9005b43c97ca9"
    }

    @GET("characters")
    suspend fun getCharacters(
        @Query("ts") timestamp: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): MarvelResponse
}   