package com.example.mindbank.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
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
                            PasswordFlowScreen(
                                savedPassword = password,
                                onSavePassword = { newPassword ->
                                    dataStoreViewModel.setPassword(newPassword)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PasswordFlowScreen(savedPassword: String, onSavePassword: (String) -> Unit) {
        var currentStep by remember { mutableIntStateOf(0) } // 0: 입력 단계, 1: 재확인 단계
        var enteredPassword by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        val context = LocalContext.current

        PinCodeScreen(context,
            title = if (currentStep == 0) "Enter PIN" else "Confirm PIN",
            onComplete = { pin ->
                if (currentStep == 0) {
                    enteredPassword = pin
                    currentStep = 1 // 재확인 단계로 이동
                    errorMessage = ""
                } else {
                    if (enteredPassword == pin) {
                        onSavePassword(pin) // 저장
                    } else {
                        errorMessage = "비밀번호가 일치하지 않습니다."
                    }
                }
            },
            onBack = {
                if (currentStep == 1) {
                    currentStep = 0 // 재확인 단계에서 뒤로 가기 가능
                    errorMessage = "" // 🔴 뒤로 갈 때 에러 메시지 초기화
                }
            },
            errorMessage = errorMessage
        )
    }

    @Composable
    fun PinCodeScreen(
        context: Context,
        title: String,
        onComplete: (String) -> Unit,
        onBack: () -> Unit,
        errorMessage: String = ""
    ) {
        val pin = remember { mutableStateListOf("", "", "", "", "", "") }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            PinInputField(pin) { index, value ->
                pin[index] = value
                if (value.isNotEmpty() && index < 5) {
                    focusManager.moveFocus(FocusDirection.Next)
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row {
                if (title == "Confirm PIN") {
                    Button(onClick = onBack, modifier = Modifier.padding(end = 8.dp)) {
                        Text("Back")
                    }
                }
                Button(onClick = {
                    val enteredPin = pin.joinToString("")
                    if (enteredPin.length == 6) {
                        onComplete(enteredPin)
                        keyboardController?.hide()
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter all 6 digits",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("Submit")
                }
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


