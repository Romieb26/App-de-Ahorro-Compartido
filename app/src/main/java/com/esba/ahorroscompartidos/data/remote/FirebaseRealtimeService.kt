//FirebaseRealtimeService.kt
package com.esba.ahorroscompartidos.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRealtimeService @Inject constructor(
    private val database: FirebaseDatabase
) {

    private val usersRef = database.getReference("users")
    private val sharedAccountsRef = database.getReference("shared_accounts").child("main")
    private val transactionsRef = database.getReference("shared_accounts").child("main").child("transactions")

    // Observar usuario - MEJORADO: Logs y manejo de datos nulos
    fun observeUser(userId: String): Flow<Map<String, Any>?> = callbackFlow {
        Timber.d("🔍 Iniciando observación de usuario: $userId")
        val userRef = usersRef.child(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val value = snapshot.getValue<Map<String, Any>>()
                    Timber.d("✅ Datos de usuario recibidos: $value")
                    trySend(value)
                } else {
                    Timber.w("⚠️ Usuario $userId no existe en la base de datos")
                    trySend(null) // Enviamos null si el usuario no existe
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("❌ Error observando usuario: ${error.message}")
                // Enviamos null en lugar de cerrar el flow para que pueda reconectarse
                trySend(null)
            }
        }

        userRef.addValueEventListener(listener)

        awaitClose {
            Timber.d("🔚 Cerrando observación de usuario: $userId")
            userRef.removeEventListener(listener)
        }
    }

    // Observar cuenta compartida - MEJORADO
    fun observeSharedAccount(): Flow<Map<String, Any>?> = callbackFlow {
        Timber.d("🔍 Iniciando observación de cuenta compartida")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val value = snapshot.getValue<Map<String, Any>>()
                    Timber.d("✅ Datos de cuenta compartida recibidos: $value")
                    trySend(value)
                } else {
                    Timber.w("⚠️ Cuenta compartida 'main' no existe")
                    trySend(emptyMap())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("❌ Error observando cuenta compartida: ${error.message}")
                trySend(emptyMap())
            }
        }

        sharedAccountsRef.addValueEventListener(listener)

        awaitClose {
            Timber.d("🔚 Cerrando observación de cuenta compartida")
            sharedAccountsRef.removeEventListener(listener)
        }
    }

    // Observar transacciones - MEJORADO
    fun observeTransactions(): Flow<List<Map<String, Any>>> = callbackFlow {
        Timber.d("🔍 Iniciando observación de transacciones")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { child ->
                    val value = child.getValue<Map<String, Any>>()?.toMutableMap()
                    value?.put("id", child.key ?: "")
                    value
                }
                Timber.d("✅ ${transactions.size} transacciones recibidas")
                trySend(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("❌ Error observando transacciones: ${error.message}")
                trySend(emptyList())
            }
        }

        transactionsRef.addValueEventListener(listener)

        awaitClose {
            Timber.d("🔚 Cerrando observación de transacciones")
            transactionsRef.removeEventListener(listener)
        }
    }

    // Obtener usuario una vez
    suspend fun getUser(userId: String): Map<String, Any>? {
        return try {
            Timber.d("🔍 Obteniendo usuario (una vez): $userId")
            val snapshot = usersRef.child(userId).get().await()
            if (snapshot.exists()) {
                val value = snapshot.getValue<Map<String, Any>>()
                Timber.d("✅ Usuario obtenido: $value")
                value
            } else {
                Timber.w("⚠️ Usuario $userId no encontrado")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Error obteniendo usuario")
            null
        }
    }

    // Obtener cuenta compartida una vez
    suspend fun getSharedAccount(): Map<String, Any>? {
        return try {
            Timber.d("🔍 Obteniendo cuenta compartida (una vez)")
            val snapshot = sharedAccountsRef.get().await()
            if (snapshot.exists()) {
                val value = snapshot.getValue<Map<String, Any>>()
                Timber.d("✅ Cuenta compartida obtenida: $value")
                value
            } else {
                Timber.w("⚠️ Cuenta compartida no encontrada")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Error obteniendo cuenta compartida")
            null
        }
    }

    // Actualizar balances
    suspend fun updateBalances(
        userId: String,
        personalBalance: Double,
        sharedBalance: Double
    ) {
        try {
            Timber.d("💾 Actualizando balances - User: $userId, Personal: $personalBalance, Shared: $sharedBalance")
            val updates = mapOf<String, Any>(
                "/users/$userId/personalBalance" to personalBalance,
                "/shared_accounts/main/balance" to sharedBalance
            )
            database.getReference().updateChildren(updates).await()
            Timber.d("✅ Balances actualizados correctamente")
        } catch (e: Exception) {
            Timber.e(e, "❌ Error actualizando balances")
            throw e
        }
    }

    // Agregar transacción
    suspend fun addTransaction(transaction: Map<String, Any>): String {
        return try {
            Timber.d("💾 Agregando transacción: $transaction")
            val newTransactionRef = transactionsRef.push()
            val transactionWithTimestamp = transaction.toMutableMap().apply {
                put("timestamp", ServerValue.TIMESTAMP)
            }
            newTransactionRef.setValue(transactionWithTimestamp).await()
            val key = newTransactionRef.key ?: ""
            Timber.d("✅ Transacción agregada con ID: $key")
            key
        } catch (e: Exception) {
            Timber.e(e, "❌ Error agregando transacción")
            throw e
        }
    }

    // Crear usuario si no existe
    suspend fun createUserIfNotExists(userId: String, userData: Map<String, Any>) {
        try {
            val userRef = usersRef.child(userId)
            val exists = userRef.get().await().exists()
            if (!exists) {
                Timber.d("👤 Creando nuevo usuario: $userId con datos: $userData")
                userRef.setValue(userData).await()
                Timber.d("✅ Usuario creado")
            } else {
                Timber.d("👤 Usuario $userId ya existe")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Error creando usuario")
            throw e
        }
    }

    // Ejecutar transacción atómica para transferencias
    suspend fun executeTransfer(
        userId: String,
        amount: Double,
        operation: (currentPersonal: Double, currentShared: Double) -> Pair<Double, Double>
    ) {
        try {
            Timber.d("💱 Ejecutando transferencia para usuario $userId por monto $amount")
            val userSnapshot = usersRef.child(userId).get().await()
            val sharedSnapshot = sharedAccountsRef.get().await()

            val personal = userSnapshot.child("personalBalance").getValue(Double::class.java) ?: 0.0
            val shared = sharedSnapshot.child("balance").getValue(Double::class.java) ?: 0.0

            Timber.d("💰 Balances actuales - Personal: $personal, Shared: $shared")

            val (newPersonal, newShared) = operation(personal, shared)

            Timber.d("💰 Nuevos balances - Personal: $newPersonal, Shared: $newShared")

            val updates = mapOf<String, Any>(
                "/users/$userId/personalBalance" to newPersonal,
                "/shared_accounts/main/balance" to newShared
            )

            database.getReference().updateChildren(updates).await()
            Timber.d("✅ Transferencia ejecutada correctamente")
        } catch (e: Exception) {
            Timber.e(e, "❌ Error en transferencia")
            throw Exception("Error en la transferencia: ${e.message}")
        }
    }
}