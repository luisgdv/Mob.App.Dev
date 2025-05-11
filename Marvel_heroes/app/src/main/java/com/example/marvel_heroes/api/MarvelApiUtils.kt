package com.example.marvel_heroes.api

import java.math.BigInteger
import java.security.MessageDigest
import java.util.Date

/**
 * Utility object for Marvel API authentication
 * Provides methods to generate timestamp and hash required for Marvel API calls
 */
object MarvelApiUtils {
    
    /**
     * Generates a timestamp for API requests
     * @return Current timestamp as string
     */
    fun generateTimestamp(): String {
        return Date().time.toString()
    }
    
    /**
     * Generates an MD5 hash for Marvel API authentication
     * @param timestamp Current timestamp
     * @param privateKey Marvel API private key
     * @param publicKey Marvel API public key
     * @return MD5 hash as hexadecimal string
     */
    fun generateHash(timestamp: String, privateKey: String, publicKey: String): String {
        val input = timestamp + privateKey + publicKey
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}