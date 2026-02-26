package com.esba.ahorroscompartidos.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun updateBalances(
        userId: String,
        personalBalance: Double,
        sharedBalance: Double
    ) {
        firestore.runBatch { batch ->

            val userRef = firestore.collection("users").document(userId)
            val sharedRef = firestore.collection("shared").document("main")

            batch.update(userRef, "personalBalance", personalBalance)
            batch.update(sharedRef, "balance", sharedBalance)

        }.await()
    }

    suspend fun addTransaction(transaction: Map<String, Any>) {
        firestore.collection("transactions")
            .document(transaction["id"] as String)
            .set(transaction)
            .await()
    }
}