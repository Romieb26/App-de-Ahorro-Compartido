package com.esba.ahorroscompartidos.presentation.cuentaahorro.data

import com.esba.ahorroscompartidos.presentation.historial.data.Transaccion

data class CentaHorrorState(
    val personalBalance: Double = 0.0,
    val sharedBalance: Double = 0.0,
    val historical: List<Transaccion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)