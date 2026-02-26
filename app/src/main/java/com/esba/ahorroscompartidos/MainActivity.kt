package com.esba.ahorroscompartidos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.androidEntryPoint
import com.esba.ahorroscompartidos.ui.theme.AhorrosCompartidosTheme
import com.esba.ahorroscompartidos.navigation.AppNavGraph
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AhorrosCompartidosTheme {
                AppNavGraph()
            }
        }
    }
}
