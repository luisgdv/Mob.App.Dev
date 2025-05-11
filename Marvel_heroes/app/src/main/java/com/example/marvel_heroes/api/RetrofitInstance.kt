package com.example.marvel_heroes.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton for Retrofit API clients.
 * Provides access to both Marvel and Superhero API services.
 */
object RetrofitInstance {
    /**
     * Shared OkHttpClient with logging and timeouts
     */
    private val httpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Retrofit instance for Marvel API
     */
    private val marvelRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(MarvelService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
    
    /**
     * Retrofit instance for Superhero API
     */
    private val superheroRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SuperheroService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
    
    /**
     * Marvel API service
     */
    val marvelService: MarvelService by lazy {
        marvelRetrofit.create(MarvelService::class.java)
    }
    
    /**
     * Superhero API service
     */
    val superheroService: SuperheroService by lazy {
        superheroRetrofit.create(SuperheroService::class.java)
    }
}