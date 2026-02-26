package com.esba.ahorroscompartidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shared_account")
data class SharedAccountEntity(
    @PrimaryKey val id: String,
    val balance: Double
)