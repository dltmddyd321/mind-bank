package com.example.mindbank.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mindbank.activity.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordActivity : ComponentActivity() {

    private lateinit var splash: androidx.core.splashscreen.SplashScreen
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        startSplash()
        setContent {
            MindBankTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordScreen()
                }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun startSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashView ->
                val icon = splashView.iconView ?: return@setOnExitAnimationListener

                ObjectAnimator.ofPropertyValuesHolder(icon).run {
                    interpolator = AnticipateInterpolator()
                    repeatCount = 2 // 반복 횟수
                    duration = 500L
                    doOnEnd {
                        splashView.remove()
                    }
                    start()
                }
            }
        }
    }
}

@Composable
fun PasswordScreen() {
    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "비밀번호 입력", color = Color.White, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordDots(length = password.length)
        Spacer(modifier = Modifier.height(32.dp))
        NumberPad { number ->
            password += number
        }
    }
}

@Composable
fun PasswordDots(length: Int) {
    Row {
        repeat(4) {
            Icon(
                imageVector = if (it <= length - 1) Icons.Filled.Info else Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color.Blue,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun NumberPad(onNumberClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 숫자 버튼들
        val numbers = arrayOf(
            arrayOf("1", "2", "3"),
            arrayOf("4", "5", "6"),
            arrayOf("7", "8", "9")
        )

        // 숫자 버튼을 표시하는 그리드
        numbers.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { number ->
                    NumberButton(number) {
                        onNumberClick(number)
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 빈 공간
            Spacer(modifier = Modifier.weight(1f))
            // 숫자 0 버튼
            NumberButton("0") {
                onNumberClick("0")
            }
            // 빈 공간
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun NumberButton(num: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(84.dp)
            .padding(8.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .border(1.dp, Color.Gray, CircleShape)
    ) {
        Text(
            text = num,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    MindBankTheme {
        Greeting("Android")
    }
}