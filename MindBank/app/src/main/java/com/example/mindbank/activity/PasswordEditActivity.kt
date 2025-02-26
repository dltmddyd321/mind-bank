package com.example.mindbank.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindbank.activity.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordEditActivity : ComponentActivity() {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                MindBankTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var password by remember { mutableStateOf("") }
                        var isLoading by remember { mutableStateOf(true) }

                        LaunchedEffect(Unit) {
                            password = dataStoreViewModel.getPassWord()
                            isLoading = false
                        }

                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            PinCodeScreen(password)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PinCodeScreen(initPassword: String) {
        val pin = remember { mutableStateListOf("", "", "", "", "", "") }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        var editingPassword = initPassword

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter PIN", fontSize = 18.sp, color = Color.White)

            Spacer(modifier = Modifier.height(20.dp))

            PinInputField(pin) { index, value ->
                pin[index] = value  // 변경된 값을 직접 업데이트
                if (value.isNotEmpty() && index < 5) {
                    focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                editingPassword = pin.joinToString("")
                dataStoreViewModel.setPassword(editingPassword)
                keyboardController?.hide()
            }) {
                Text("Submit")
            }
        }
    }

    @Composable
    fun PinInputField(pin: List<String>, onValueChange: (Int, String) -> Unit) {
        val textFieldRefs = remember { List(6) { FocusRequester() } }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp)
        ) {
            repeat(6) { index ->
                TextField(
                    value = pin.getOrElse(index) { "" },
                    onValueChange = { value ->
                        when {
                            value.length == 1 && value.all { it.isDigit() } -> {
                                onValueChange(index, value)
                                if (index < 5) {
                                    textFieldRefs[index + 1].requestFocus() // 🎯 다음 칸으로 이동
                                }
                            }
                            value.isEmpty() -> {
                                onValueChange(index, "")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, if (pin.getOrElse(index) { "" }.isNotEmpty()) Color.White else Color.Gray)
                        .background(Color.Black)
                        .focusRequester(textFieldRefs[index]) // 🎯 각 칸의 포커스 제어
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Backspace) {
                                if (pin[index].isEmpty() && index > 0) {
                                    textFieldRefs[index - 1].requestFocus() // 🎯 이전 칸으로 이동
                                    onValueChange(index - 1, "") // 🎯 이전 칸 값 삭제
                                }
                                true
                            } else {
                                false
                            }
                        }
                )
            }
        }
    }
}


