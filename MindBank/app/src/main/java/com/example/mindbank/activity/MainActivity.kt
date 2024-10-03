package com.example.mindbank.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.example.mindbank.db.DataStoreViewModel
import com.example.mindbank.db.DataViewModel
import com.example.mindbank.navigation.BottomNavBar
import com.example.mindbank.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val dataViewModel: DataViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) }
            ) { paddingValues ->
                NavigationGraph(navController, dataViewModel, dataStoreViewModel, paddingValues)
            }
        }
    }
}

