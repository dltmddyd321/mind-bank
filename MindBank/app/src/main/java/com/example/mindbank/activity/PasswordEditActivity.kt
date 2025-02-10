package com.example.mindbank.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindbank.activity.ui.theme.MindBankTheme

class PasswordEditActivity : ComponentActivity() {
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
                        PinCodeScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun PinCodeScreen() {
    var pin by remember { mutableStateOf(List(6) { "" }) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
            pin = pin.toMutableList().apply {
                this[index] = value
            }
            if (value.isNotEmpty() && index < 5) {
                focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            keyboardController?.hide()
        }) {
            Text("Submit")
        }
    }
}

@Composable
fun PinInputField(pin: List<String>, onValueChange: (Int, String) -> Unit) {
    val focusManager = LocalFocusManager.current
    val textInputService = LocalTextInputService.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        repeat(6) { index ->
            TextField(
                value = pin[index],
                onValueChange = { value ->
                    if (value.length <= 1 && value.all { it.isDigit() }) {
                        onValueChange(index, value)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier
                    .size(50.dp)
                    .border(2.dp, if (pin[index].isNotEmpty()) Color.White else Color.Gray)
                    .background(Color.Black),
                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, color = Color.White)
            )
        }
    }
}