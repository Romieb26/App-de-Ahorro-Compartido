package com.esba.ahorroscompartidos.navigation

sealed class Routes(val route: String) {

    object Login : Routes("login")

    object CuentaAhorro : Routes("cuentaAhorro")
    object Historial : Routes("historial")
}