package com.esba.ahorroscompartidos.navigation

import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.esba.ahorroscompartidos.Presentation.dashboard.DashboardScreen
import com.esba.ahorroscompartidos.Presentation.login.LoginScreen
import com.esba.ahorroscompartidos.Presentation.prueba.SplashScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route   // 🔥 ahora empieza en Splash
    ) {

        // 🔵 SPLASH
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 🔵 LOGIN
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 🔵 DASHBOARD
        composable(Routes.Dashboard.route) {
            DashboardScreen() // usa el tuyo o uno temporal
        }


    }
}