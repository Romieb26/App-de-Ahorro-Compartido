//AuthRepositoryImpl.kt
package com.esba.ahorroscompartidos.data.repository

import com.esba.ahorroscompartidos.data.remote.FirebaseAuthDataSource
import com.esba.ahorroscompartidos.data.remote.FirebaseRealtimeService
import com.esba.ahorroscompartidos.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAuthDataSource,
    private val realtimeService: FirebaseRealtimeService  // Agregado
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val result = dataSource.login(email, password)
            result.onSuccess { userId ->
                // Crear usuario en Realtime DB si no existe
                realtimeService.createUserIfNotExists(
                    userId,
                    mapOf(
                        "personalBalance" to 0.0,
                        "sharedAccountId" to "main"
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val result = dataSource.register(email, password)
            result.onSuccess { userId ->
                // Crear usuario en Realtime DB
                realtimeService.createUserIfNotExists(
                    userId,
                    mapOf(
                        "personalBalance" to 0.0,
                        "sharedAccountId" to "main"
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean =
        dataSource.getCurrentUserId() != null

    override fun getCurrentUserId(): String? =
        dataSource.getCurrentUserId()

    override fun logout() =
        dataSource.logout()
}