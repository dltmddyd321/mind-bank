package com.example.mindbank.presentation.navigation.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindbank.R
import com.example.mindbank.presentation.navigation.component.BottomNavBar
import com.example.mindbank.presentation.navigation.component.ChecklistList
import com.example.mindbank.presentation.navigation.component.HomeScreen
import com.example.mindbank.presentation.navigation.component.MainGrid
import com.example.mindbank.presentation.navigation.component.MainTopBar
import com.example.mindbank.presentation.navigation.component.Screen
import com.example.mindbank.presentation.navigation.component.SearchBar
import com.example.mindbank.presentation.navigation.component.SettingsScreen
import com.example.mindbank.presentation.navigation.theme.MindBankTheme
import com.example.mindbank.util.AppLanguageState
import com.example.mindbank.util.DataType
import com.example.mindbank.viewmodel.MemoViewModel
import com.example.mindbank.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint


@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val languageState = AppLanguageState()
    private val todoViewModel: TodoViewModel by viewModels()
    private val memoViewModel: MemoViewModel by viewModels()
    private var backPressedTime: Long = 0
    private var isTodoMode = false

    private val todoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                todoViewModel.loadTodoList()
            }
        }
    private val memoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                memoViewModel.loadMemoList()
            }
        }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChangeSystemBarsTheme(!isSystemInDarkTheme())
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) },
                floatingActionButton = {
                    if (currentRoute == Screen.Todo.route || currentRoute == Screen.Notes.route) {
                        val context = LocalContext.current
                        FloatingActionButton(
                            onClick = {
                                if (isTodoMode) {
                                    val intent = Intent(context, AddTodoActivity::class.java)
                                    todoLauncher.launch(intent)
                                } else {
                                    val intent = Intent(context, AddMemoActivity::class.java)
                                    memoLauncher.launch(intent)
                                }
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
                }
            ) { paddingValues ->
                isTodoMode = currentRoute == Screen.Todo.route
                NavHost(navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController, memoViewModel, todoViewModel,
                            paddingValues, onEditTodo = { todo ->
                                val intent =
                                    Intent(this@MainActivity, AddTodoActivity::class.java)
                                        .apply { putExtra("id", todo.id) }
                                todoLauncher.launch(intent)
                            }, onEditMemo = { memo ->
                                val intent =
                                    Intent(this@MainActivity, AddMemoActivity::class.java)
                                        .apply { putExtra("id", memo.id) }
                                memoLauncher.launch(intent)
                            })
                    }
                    composable(Screen.Todo.route) {
                        NotesScreen(
                            memoViewModel,
                            todoViewModel,
                            paddingValues,
                            DataType.Todo
                        )
                    }
                    composable(Screen.Notes.route) {
                        NotesScreen(
                            memoViewModel,
                            todoViewModel,
                            paddingValues,
                            DataType.Memo
                        )
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(paddingValues, languageState) {
                            todoViewModel.clear()
                            memoViewModel.clear()
                        }
                    }
                }
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < 2000) { // 2초 내에 다시 눌렀다면 종료
                        finish()
                    } else {
                        backPressedTime = currentTime
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.alert_back_press),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    @Composable
    fun NotesScreen(
        memoViewModel: MemoViewModel,
        todoViewModel: TodoViewModel,
        paddingValues: PaddingValues,
        dataType: DataType,
    ) {
        MindBankTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (dataType == DataType.Todo) {
                    MainScreen(todoViewModel, paddingValues)
                } else {
                    MainScreen(memoViewModel, paddingValues)
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainScreen(viewModel: ViewModel, paddingValues: PaddingValues) {
        val title =
            if (isTodoMode) stringResource(R.string.todo_title) else stringResource(R.string.memo_title)
        var searchText by remember { mutableStateOf("") }
        Scaffold(
            topBar = {
                Column {
                    MainTopBar(title)
                    SearchBar(
                        hint = getString(R.string.alert_search),
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
                if (viewModel is MemoViewModel) {
                    MainGrid(viewModel, searchText) { memo ->
                        val intent = Intent(this@MainActivity, AddMemoActivity::class.java)
                            .apply { putExtra("id", memo.id) }
                        memoLauncher.launch(intent)
                    }
                } else if (viewModel is TodoViewModel) {
                    ChecklistList(viewModel, searchText) { todo ->
                        val intent = Intent(this@MainActivity, AddTodoActivity::class.java)
                            .apply { putExtra("id", todo.id) }
                        todoLauncher.launch(intent)
                    }
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