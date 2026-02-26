package com.esba.ahorroscompartidos.data.repository

import com.esba.ahorroscompartidos.data.remote.FirebaseAuthDataSource
import com.esba.ahorroscompartidos.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            dataSource.login(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            dataSource.register(email, password)
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

