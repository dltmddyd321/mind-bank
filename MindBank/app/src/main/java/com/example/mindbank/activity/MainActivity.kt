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
import com.example.mindbank.navigation.BottomNavBar
import com.example.mindbank.navigation.NavigationGraph
import com.example.mindbank.viewmodel.AdviceViewModel
import com.example.mindbank.viewmodel.DataViewModel
import com.example.mindbank.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.Arrays
import java.util.StringTokenizer
import kotlin.math.min


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

@Throws(Exception::class)
fun main() {
    val br = BufferedReader(InputStreamReader(System.`in`))
    val bw = BufferedWriter(OutputStreamWriter(System.out))
    val st = StringTokenizer(br.readLine())

    val N = st.nextToken().toInt()
    val M = st.nextToken().toInt()
    val arr = IntArray(N)
    for (i in 0 until N) {
        arr[i] = br.readLine().toInt()
    }
    Arrays.sort(arr)

    var i = 0
    var j = 0
    var ans = Int.MAX_VALUE
    // 투 포인터 알고리즘
    while (i < N) {
        if (arr[i] - arr[j] < M) {
            i++
            continue
        }

        if (arr[i] - arr[j] == M) {
            ans = M
            break
        }

        ans = min(ans.toDouble(), (arr[i] - arr[j]).toDouble()).toInt()
        j++
    }

    bw.write(ans.toString() + "\n")
    bw.flush()
    bw.close()
    br.close()
}
