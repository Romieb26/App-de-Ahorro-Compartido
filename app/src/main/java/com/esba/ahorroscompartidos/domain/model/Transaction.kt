//Transaction.kt
package com.esba.ahorroscompartidos.domain.model

data class Transaction(
    val id: String,
    val fromUserId: String,
    val type: TransactionType,
    val amount: Double,
    val timestamp: Long,
    val status: TransactionStatus
)