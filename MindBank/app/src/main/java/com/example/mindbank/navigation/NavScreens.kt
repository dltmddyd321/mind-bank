package com.example.mindbank.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mindbank.component.AdviceScreen
import com.example.mindbank.viewmodel.DataViewModel
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.AdviceViewModel
import com.example.mindbank.viewmodel.TodoViewModel

@Composable
fun HomeScreen(
    adviceViewModel: AdviceViewModel,
    todoViewModel: TodoViewModel,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        AdviceScreen(adviceViewModel, todoViewModel)
    }
}


@Composable
fun SettingsScreen(paddingValues: PaddingValues, dataViewModel: DataViewModel, onConfirmDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        MindBankTheme {
            Scaffold(
                topBar = {
                    MainTopBar("Settings")
                },
                content = {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SettingsScreen(onConfirmDelete = {
                            onConfirmDelete.invoke()
                        })
                    }
                }
            )
        }
    }
}
