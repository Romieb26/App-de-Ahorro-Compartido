package com.esba.ahorroscompartidos.data.mapper

import com.esba.ahorroscompartidos.data.local.entity.*
import com.esba.ahorroscompartidos.domain.model.*

fun UserAccountEntity.toDomain() =
    UserAccount(id, name, personalBalance)

fun SharedAccountEntity.toDomain() =
    SharedAccount(id, balance)

fun TransactionEntity.toDomain() =
    Transaction(
        id = id,
        fromUserId = fromUserId,
        type = TransactionType.valueOf(type),
        amount = amount,
        timestamp = timestamp,
        status = TransactionStatus.valueOf(status)
    )

fun Transaction.toEntity() =
    TransactionEntity(
        id = id,
        fromUserId = fromUserId,
        type = type.name,
        amount = amount,
        timestamp = timestamp,
        status = status.name
    )