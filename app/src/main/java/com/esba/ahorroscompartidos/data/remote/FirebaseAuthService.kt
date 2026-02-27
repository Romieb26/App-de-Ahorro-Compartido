//FirebaseAuthService.kt
package com.esba.ahorroscompartidos.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun currentUserId(): String =
        auth.currentUser?.uid ?: throw Exception("User not logged in")
}