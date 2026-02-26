package com.esba.ahorroscompartidos.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.esba.ahorroscompartidos.data.local.dao.BankDao
import com.esba.ahorroscompartidos.data.local.entity.*

@Database(
    entities = [
        UserAccountEntity::class,
        SharedAccountEntity::class,
        TransactionEntity::class
    ],
    version = 1
)
abstract class DualSaveDatabase : RoomDatabase() {
    abstract fun bankDao(): BankDao
}