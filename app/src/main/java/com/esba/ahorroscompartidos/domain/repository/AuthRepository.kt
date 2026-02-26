package com.esba.ahorroscompartidos.domain.repository

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>

    fun getCurrentUserId(): String?

    fun isUserLoggedIn(): Boolean

    fun logout()
}