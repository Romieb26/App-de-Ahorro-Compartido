// FirebaseFirestoreService.kt
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFirestoreService @Inject constructor(
    private val database: FirebaseDatabase
) {

    private val usersRef = database.getReference("users")
    private val sharedAccountRef = database.getReference("shared_account").child("main")
    private val transactionsRef = database.getReference("shared_account").child("main").child("transactions")

    // Observar usuario
    fun observeUser(userId: String): Flow<Map<String, Any>?> = callbackFlow {
        val userRef = usersRef.child(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<Map<String, Any>>()
                trySend(value)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userRef.addValueEventListener(listener)

        awaitClose { userRef.removeEventListener(listener) }
    }

    // Observar cuenta compartida
    fun observeSharedAccount(): Flow<Map<String, Any>?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<Map<String, Any>>()
                trySend(value)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        sharedAccountRef.addValueEventListener(listener)

        awaitClose { sharedAccountRef.removeEventListener(listener) }
    }

    // Observar transacciones
    fun observeTransactions(): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { child ->
                    val value = child.getValue<Map<String, Any>>()?.toMutableMap()
                    value?.put("id", child.key ?: "")
                    value
                }
                trySend(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        transactionsRef.addValueEventListener(listener)

        awaitClose { transactionsRef.removeEventListener(listener) }
    }

    // Obtener usuario una vez
    suspend fun getUser(userId: String): Map<String, Any>? {
        val snapshot = usersRef.child(userId).get().await()
        return snapshot.getValue<Map<String, Any>>()
    }

    // Obtener cuenta compartida una vez
    suspend fun getSharedAccount(): Map<String, Any>? {
        val snapshot = sharedAccountRef.get().await()
        return snapshot.getValue<Map<String, Any>>()
    }

    // Actualizar balances
    suspend fun updateBalances(
        userId: String,
        personalBalance: Double,
        sharedBalance: Double
    ) {
        val updates = mapOf<String, Any>(
            "/users/$userId/personalBalance" to personalBalance,
            "/shared_account/main/balance" to sharedBalance
        )

        database.getReference().updateChildren(updates).await()
    }

    // Agregar transacción
    suspend fun addTransaction(transaction: Map<String, Any>): String {
        val newTransactionRef = transactionsRef.push()
        val transactionWithTimestamp = transaction.toMutableMap().apply {
            put("timestamp", ServerValue.TIMESTAMP)
        }
        newTransactionRef.setValue(transactionWithTimestamp).await()
        return newTransactionRef.key ?: ""
    }

    // Crear usuario si no existe
    suspend fun createUserIfNotExists(userId: String, userData: Map<String, Any>) {
        val userRef = usersRef.child(userId)
        val exists = userRef.get().await().exists()

        if (!exists) {
            userRef.setValue(userData).await()
        }
    }

    // Ejecutar transacción atómica para transferencias
    suspend fun executeTransfer(
        userId: String,
        amount: Double,
        operation: (currentPersonal: Double, currentShared: Double) -> Pair<Double, Double>
    ) {
        val userRef = usersRef.child(userId)

        userRef.get().await().let { userSnapshot ->
            val sharedSnapshot = sharedAccountRef.get().await()

            val personal = userSnapshot.child("personalBalance").getValue(Double::class.java) ?: 0.0
            val shared = sharedSnapshot.child("balance").getValue(Double::class.java) ?: 0.0

            val (newPersonal, newShared) = operation(personal, shared)

            val updates = mapOf<String, Any>(
                "/users/$userId/personalBalance" to newPersonal,
                "/shared_account/main/balance" to newShared
            )

            database.getReference().updateChildren(updates).await()
        }
    }
}