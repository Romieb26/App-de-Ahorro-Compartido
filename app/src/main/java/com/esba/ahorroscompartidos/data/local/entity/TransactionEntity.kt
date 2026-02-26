package com.esba.ahorroscompartidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val fromUserId: String,
    val type: String,
    val amount: Double,
    val timestamp: Long,
    val status: String
)
//mensaje jajaja//