package com.example.mindbank.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindbank.activity.ui.theme.MindBankTheme

class PasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}


@Composable
fun NumberPad(onNumberClick: (String) -> Unit) {
    Column {
        // 숫자 버튼들
        val numbers = arrayOf(
            arrayOf("1", "2", "3"),
            arrayOf("4", "5", "6"),
            arrayOf("7", "8", "9")
        )

        // 숫자 버튼을 표시하는 그리드
        numbers.forEach { row ->
            Row {
                row.forEach { number ->
                    NumberButton(number) {
                        onNumberClick(number)
                    }
                }
            }
        }

        Row {
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
            .size(64.dp)
            .padding(8.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .border(1.dp, Color.Gray, CircleShape)
    ) {
        Text(
            text = num,
            color = Color.White,
            fontSize = 24.sp
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