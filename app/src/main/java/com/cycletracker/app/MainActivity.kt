package com.cycletracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cycletracker.app.ui.navigation.CycleTrackerApp
import com.cycletracker.app.ui.theme.CycleTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CycleTrackerTheme {
                CycleTrackerApp()
            }
        }
    }
}
