//UserAccount.kt
package com.esba.ahorroscompartidos.domain.model

data class UserAccount(
    val id: String,
    val personalBalance: Double,
    val sharedAccountId: String = "main"  // Valor por defecto agregado
)