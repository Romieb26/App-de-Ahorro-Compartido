package com.esba.ahorroscompartidos.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Dashboard : Routes("dashboard")
    object History : Routes("history")
}