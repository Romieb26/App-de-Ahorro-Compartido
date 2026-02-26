package com.esba.ahorroscompartidos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.
import com.esba.ahorroscompartidos.Presentation.dashboard.DashboardScreen
import com.esba.ahorroscompartidos.Presentation.login.LoginScreen
import com.esba.ahorroscompartidos.presentation.transactions.TransactionHistoryScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Dashboard.route) {
            DashboardScreen(
                onOpenHistory = {
                    navController.navigate(Routes.History.route)
                }
            )
        }

        composable(Routes.History.route) {
            TransactionHistoryScreen()
        }
    }
}