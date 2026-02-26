package com.esba.ahorroscompartidos.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val personalBalance: Double
)