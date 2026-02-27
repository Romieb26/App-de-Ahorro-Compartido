//AuthRepository.kt
package com.esba.ahorroscompartidos.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    fun logout()
}