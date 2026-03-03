//Transaccion.kt
package com.esba.ahorroscompartidos.presentation.historial.data


import java.util.Date

data class Transaccion(
    val id: String = "",
    val tipo: String = "",
    val usuarioId: String = "",
    val monto: Double = 0.0,
    val fecha: Date? = null
)