package com.example.mindbank.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import com.example.mindbank.viewmodel.DataViewModel
import com.example.mindbank.navigation.BottomNavBar
import com.example.mindbank.navigation.NavigationGraph
import com.example.mindbank.viewmodel.AdviceViewModel
import com.example.mindbank.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()
    private val adviceViewModel: AdviceViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChangeSystemBarsTheme(!isSystemInDarkTheme())
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) }
            ) { paddingValues ->
                NavigationGraph(navController, dataViewModel, adviceViewModel, paddingValues)
            }
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @Composable
    private fun ChangeSystemBarsTheme(lightTheme: Boolean) {
        LaunchedEffect(lightTheme) {
            if (lightTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        Color.White.toArgb(), Color.White.toArgb()
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        Color.White.toArgb(), Color.White.toArgb()
                    ),
                )
            } else {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        Color.Black.toArgb()
                    ),
                    navigationBarStyle = SystemBarStyle.dark(
                        Color.Black.toArgb()
                    ),
                )
            }
        }
    }
}

