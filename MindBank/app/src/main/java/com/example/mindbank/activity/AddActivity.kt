package com.example.mindbank.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.mindbank.activity.ui.theme.MindBankTheme

class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BackHandlerWithQuestionDialog()
                }
            }
        }
    }
}

@Composable
fun AutoBackUpCheckDialog() {
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
        }, title = {
            Text(text = "백업 데이터 불러오기")
        }, confirmButton = {
            TextButton(
                onClick = {
                    //TODO: 백업 데이터 불러오기
                }
            ) { Text("확인") }
        }, text = {
            Text(
                text = "저장이 완료되지 않은 데이터가 있습니다.\n마저 작성하시겠습니까?",
                textAlign = TextAlign.Center
            )
        }, dismissButton = {
            TextButton(
                onClick = { showDialog = false }
            ) { Text("취소") }
        })
    }
}

@Composable
fun BackHandlerWithQuestionDialog() {
    var showDialog by remember {
        mutableStateOf(false)
    }
    val activity = LocalContext.current as? Activity

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
        }, confirmButton = {
            TextButton(
                onClick = { activity?.finish() }
            ) { Text("확인") }
        }, title = {
            Text(text = "메모 추가 취소")
        }, text = {
            Text(text = "메모 추가를 취소하시겠습니까?")
        }, dismissButton = {
            TextButton(
                onClick = { showDialog = false }
            ) { Text("취소") }
        })
    }

    BackHandler { showDialog = true }
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
fun GreetingPreview2() {
    MindBankTheme {
        Greeting("Android")
    }
}