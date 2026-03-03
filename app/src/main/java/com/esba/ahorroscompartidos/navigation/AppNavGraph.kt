//AppNavGraph
package com.esba.ahorroscompartidos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esba.ahorroscompartidos.presentation.cuentaahorro.screen.CuentaAhorroScreen
import com.esba.ahorroscompartidos.presentation.historial.screen.HistorialScreen
import com.esba.ahorroscompartidos.presentation.login.screen.LoginScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        // 🔵 LOGIN
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.CuentaAhorro.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 🔵 CUENTA AHORRO
        composable(Routes.CuentaAhorro.route) {
            CuentaAhorroScreen(
                onBackToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.CuentaAhorro.route) { inclusive = true }
                    }
                },
                onNavigateToHistorial = {
                    navController.navigate(Routes.Historial.route)
                }
            )
        }

        composable(Routes.Historial.route) {
            HistorialScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}