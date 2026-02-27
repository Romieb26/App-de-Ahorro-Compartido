//BankRepositoryImpl.kt
package com.esba.ahorroscompartidos.data.repository

import com.esba.ahorroscompartidos.data.remote.FirebaseAuthDataSource
import com.esba.ahorroscompartidos.data.remote.FirebaseRealtimeService
import com.esba.ahorroscompartidos.domain.model.SharedAccount
import com.esba.ahorroscompartidos.domain.model.Transaction
import com.esba.ahorroscompartidos.domain.model.TransactionStatus
import com.esba.ahorroscompartidos.domain.model.TransactionType
import com.esba.ahorroscompartidos.domain.model.UserAccount
import com.esba.ahorroscompartidos.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val realtimeService: FirebaseRealtimeService
) : BankRepository {

    override fun observeUserAccount(): Flow<UserAccount?> {
        val userId = authDataSource.getCurrentUserId()
            ?: return flowOf(null).also {
                Timber.w("⚠️ observeUserAccount: No hay usuario autenticado")
            }

        Timber.d("📡 observeUserAccount: Iniciando para userId: $userId")

        return realtimeService.observeUser(userId)
            .catch { e ->
                Timber.e(e, "❌ Error en observeUserAccount")
                emit(null)
            }
            .map { data ->
                if (data == null) {
                    Timber.w("⚠️ observeUserAccount: Datos recibidos son null")
                    return@map null
                }

                Timber.d("📊 observeUserAccount: Datos crudos recibidos: $data")

                try {
                    // Extraer valores de forma segura
                    val personalBalance = when (val balance = data["personalBalance"]) {
                        is Double -> balance
                        is Long -> balance.toDouble()
                        is Int -> balance.toDouble()
                        else -> {
                            Timber.w("⚠️ personalBalance no es número, es: ${balance?.javaClass}")
                            0.0
                        }
                    }

                    val sharedAccountId = data["sharedAccountId"] as? String ?: "main"

                    UserAccount(
                        id = userId,
                        personalBalance = personalBalance,
                        sharedAccountId = sharedAccountId
                    ).also {
                        Timber.d("✅ UserAccount mapeado: $it")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "❌ Error mapeando UserAccount")
                    null
                }
            }
    }

    override fun observeSharedAccount(): Flow<SharedAccount?> {
        Timber.d("📡 observeSharedAccount: Iniciando")

        return realtimeService.observeSharedAccount()
            .catch { e ->
                Timber.e(e, "❌ Error en observeSharedAccount")
                emit(null)
            }
            .map { data ->
                if (data == null || data.isEmpty()) {
                    Timber.w("⚠️ observeSharedAccount: Datos vacíos o nulos")
                    return@map null
                }

                Timber.d("📊 observeSharedAccount: Datos crudos recibidos: $data")

                try {
                    val balance = when (val bal = data["balance"]) {
                        is Double -> bal
                        is Long -> bal.toDouble()
                        is Int -> bal.toDouble()
                        else -> {
                            Timber.w("⚠️ balance no es número, es: ${bal?.javaClass}")
                            0.0
                        }
                    }

                    SharedAccount(
                        id = "main",
                        balance = balance
                    ).also {
                        Timber.d("✅ SharedAccount mapeado: $it")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "❌ Error mapeando SharedAccount")
                    null
                }
            }
    }

    override fun observeTransactions(): Flow<List<Transaction>> {
        Timber.d("📡 observeTransactions: Iniciando")

        return realtimeService.observeTransactions()
            .catch { e ->
                Timber.e(e, "❌ Error en observeTransactions")
                emit(emptyList())
            }
            .map { list ->
                Timber.d("📊 observeTransactions: ${list.size} transacciones crudas recibidas")

                list.mapNotNull { data ->
                    try {
                        val id = data["id"] as? String ?: return@mapNotNull null
                        val fromUserId = data["fromUser"] as? String ?: ""
                        val typeStr = data["type"] as? String ?: return@mapNotNull null
                        val amount = when (val amt = data["amount"]) {
                            is Double -> amt
                            is Long -> amt.toDouble()
                            is Int -> amt.toDouble()
                            else -> {
                                Timber.w("⚠️ amount no es número en transacción $id")
                                0.0
                            }
                        }
                        val timestamp = data["timestamp"] as? Long ?: System.currentTimeMillis()
                        val statusStr = data["status"] as? String ?: "CONFIRMED"

                        // Convertir String a Enum de forma segura
                        val type = try {
                            TransactionType.valueOf(typeStr)
                        } catch (e: IllegalArgumentException) {
                            Timber.w("⚠️ Tipo de transacción desconocido: $typeStr")
                            return@mapNotNull null
                        }

                        val status = try {
                            TransactionStatus.valueOf(statusStr)
                        } catch (e: IllegalArgumentException) {
                            Timber.w("⚠️ Estado de transacción desconocido: $statusStr")
                            TransactionStatus.CONFIRMED // Valor por defecto
                        }

                        Transaction(
                            id = id,
                            fromUserId = fromUserId,
                            type = type,
                            amount = amount,
                            timestamp = timestamp,
                            status = status
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "❌ Error parseando transacción")
                        null
                    }
                }.also {
                    Timber.d("✅ ${it.size} transacciones mapeadas correctamente")
                }
            }
    }

    override suspend fun transferToShared(amount: Double) {
        val userId = getCurrentUserId() ?: throw Exception("Usuario no autenticado")
        Timber.d("💱 transferToShared: $amount por usuario $userId")

        try {
            // Verificar saldo y ejecutar transferencia
            realtimeService.executeTransfer(userId, amount) { personal, shared ->
                if (personal < amount) {
                    throw Exception("Saldo insuficiente para transferir")
                }
                Pair(personal - amount, shared + amount)
            }

            // Registrar transacción
            val transaction = mapOf(
                "type" to TransactionType.TRANSFER_TO_SHARED.name,
                "fromUser" to userId,
                "amount" to amount,
                "status" to TransactionStatus.CONFIRMED.name
            )

            realtimeService.addTransaction(transaction)
            Timber.d("✅ transferToShared completado")
        } catch (e: Exception) {
            Timber.e(e, "❌ Error en transferToShared")
            throw e
        }
    }

    override suspend fun withdrawFromShared(amount: Double) {
        val userId = getCurrentUserId() ?: throw Exception("Usuario no autenticado")
        Timber.d("💱 withdrawFromShared: $amount por usuario $userId")

        try {
            realtimeService.executeTransfer(userId, amount) { personal, shared ->
                if (shared < amount) {
                    throw Exception("Fondos insuficientes en cuenta compartida")
                }
                Pair(personal + amount, shared - amount)
            }

            val transaction = mapOf(
                "type" to TransactionType.WITHDRAW_SHARED.name,
                "fromUser" to userId,
                "amount" to amount,
                "status" to TransactionStatus.CONFIRMED.name
            )

            realtimeService.addTransaction(transaction)
            Timber.d("✅ withdrawFromShared completado")
        } catch (e: Exception) {
            Timber.e(e, "❌ Error en withdrawFromShared")
            throw e
        }
    }

    override suspend fun depositPersonal(amount: Double) {
        val userId = getCurrentUserId() ?: throw Exception("Usuario no autenticado")
        Timber.d("💱 depositPersonal: $amount por usuario $userId")

        try {
            val user = realtimeService.getUser(userId)
            val personal = when (val bal = user?.get("personalBalance")) {
                is Double -> bal
                is Long -> bal.toDouble()
                is Int -> bal.toDouble()
                else -> 0.0
            }

            val sharedAccount = realtimeService.getSharedAccount()
            val shared = when (val bal = sharedAccount?.get("balance")) {
                is Double -> bal
                is Long -> bal.toDouble()
                is Int -> bal.toDouble()
                else -> 0.0
            }

            realtimeService.updateBalances(userId, personal + amount, shared)

            val transaction = mapOf(
                "type" to TransactionType.DEPOSIT_PERSONAL.name,
                "fromUser" to userId,
                "amount" to amount,
                "status" to TransactionStatus.CONFIRMED.name
            )

            realtimeService.addTransaction(transaction)
            Timber.d("✅ depositPersonal completado")
        } catch (e: Exception) {
            Timber.e(e, "❌ Error en depositPersonal")
            throw e
        }
    }

    override suspend fun withdrawPersonal(amount: Double) {
        val userId = getCurrentUserId() ?: throw Exception("Usuario no autenticado")
        Timber.d("💱 withdrawPersonal: $amount por usuario $userId")

        try {
            val user = realtimeService.getUser(userId)
            val personal = when (val bal = user?.get("personalBalance")) {
                is Double -> bal
                is Long -> bal.toDouble()
                is Int -> bal.toDouble()
                else -> 0.0
            }

            if (personal < amount) {
                throw Exception("Saldo personal insuficiente")
            }

            val sharedAccount = realtimeService.getSharedAccount()
            val shared = when (val bal = sharedAccount?.get("balance")) {
                is Double -> bal
                is Long -> bal.toDouble()
                is Int -> bal.toDouble()
                else -> 0.0
            }

            realtimeService.updateBalances(userId, personal - amount, shared)

            val transaction = mapOf(
                "type" to TransactionType.WITHDRAW_PERSONAL.name,
                "fromUser" to userId,
                "amount" to amount,
                "status" to TransactionStatus.CONFIRMED.name
            )

            realtimeService.addTransaction(transaction)
            Timber.d("✅ withdrawPersonal completado")
        } catch (e: Exception) {
            Timber.e(e, "❌ Error en withdrawPersonal")
            throw e
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return authDataSource.getCurrentUserId()
    }
}