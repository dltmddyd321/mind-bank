package com.example.mindbank.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindbank.component.ChecklistList
import com.example.mindbank.component.SearchBar
import com.example.mindbank.navigation.BottomNavBar
import com.example.mindbank.navigation.MainGrid
import com.example.mindbank.navigation.MainTopBar
import com.example.mindbank.navigation.Screen
import com.example.mindbank.navigation.SettingsScreen
import com.example.mindbank.state.DataType
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.DataViewModel
import com.example.mindbank.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()
    private var isTodoMode = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val refreshTrigger = remember { mutableStateOf(false) }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { _ -> refreshTrigger.value = !refreshTrigger.value }
            ChangeSystemBarsTheme(!isSystemInDarkTheme())
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) }, floatingActionButton = {
                    val context = LocalContext.current
                    FloatingActionButton(
                        onClick = {
                            val intent = if (isTodoMode) Intent(context, AddTodoActivity::class.java)
                            else Intent(context, AddMemoActivity::class.java)
                            launcher.launch(intent)
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add FAB",
                            tint = Color.White,
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(navController, startDestination = Screen.Todo.route) {
                    composable(Screen.Todo.route) { NotesScreen(refreshTrigger.value, dataViewModel, todoViewModel, paddingValues, DataType.Todo) }
                    composable(Screen.Notes.route) { NotesScreen(refreshTrigger.value, dataViewModel, todoViewModel, paddingValues, DataType.Memo) }
                    composable(Screen.Settings.route) { SettingsScreen(paddingValues) { dataViewModel.clear() } }
                }
            }
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @Composable
    fun NotesScreen(
        refreshTrigger: Boolean,
        dataViewModel: DataViewModel,
        todoViewModel: TodoViewModel,
        paddingValues: PaddingValues,
        dataType: DataType
    ) {
        MindBankTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (dataType == DataType.Todo) {
                    MainScreen(todoViewModel, paddingValues, refreshTrigger)
                } else {
                    MainScreen(dataViewModel, paddingValues, refreshTrigger)
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainScreen(viewModel: ViewModel, paddingValues: PaddingValues, refreshTrigger: Boolean) {
        isTodoMode = viewModel is TodoViewModel
        val title = if (isTodoMode) "Todo" else "Memo"
        var searchText by remember { mutableStateOf("") }
        Scaffold(
            topBar = {
                Column {
                    MainTopBar(title)
                    SearchBar(
                        hint = "검색어를 입력하시오.",
                        onTextChange = { searchText = it }
                    )
                }
            },
            modifier = Modifier.padding(paddingValues)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (viewModel is DataViewModel) {
                    MainGrid(viewModel, searchText, refreshTrigger)
                } else if (viewModel is TodoViewModel) {
                    ChecklistList(viewModel, searchText, refreshTrigger)
                }
            }
        }
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