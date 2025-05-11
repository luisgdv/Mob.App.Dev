package com.example.marvel_heroes.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Network module for Marvel API communication.
 * Provides Retrofit instance, API service, and authentication utilities.
 */
object NetworkModule {
    // API configuration constants
    private const val BASE_URL = "https://gateway.marvel.com/v1/public/"
    private const val PUBLIC_KEY = "124adf6a8131fc4b2930f93d647c15d3"
    private const val PRIVATE_KEY = "1234"

    /**
     * OkHttpClient with logging and timeouts configured
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Retrofit instance configured for Marvel API
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Marvel API service instance
     */
    val marvelService: MarvelService = retrofit.create(MarvelService::class.java)

    /**
     * Generates MD5 hash required for Marvel API authentication
     * @param timestamp Current timestamp
     * @return MD5 hash of timestamp + private key + public key
     */
    fun generateHash(timestamp: String): String {
        val input = timestamp + PRIVATE_KEY + PUBLIC_KEY
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    /**
     * Returns the public API key
     * @return Marvel API public key
     */
    fun getApiKey(): String = PUBLIC_KEY
}