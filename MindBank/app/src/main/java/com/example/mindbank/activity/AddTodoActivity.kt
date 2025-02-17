package com.example.mindbank.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mindbank.data.SaveData
import com.example.mindbank.data.Task
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.util.toHex
import com.example.mindbank.viewmodel.TodoViewModel
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@AndroidEntryPoint
class AddTodoActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()

    @Composable
    fun BackHandlerWithQuestionDialog(initValue: Boolean) {
        var showDialog by remember {
            mutableStateOf(initValue)
        }
        val activity = LocalContext.current as? Activity ?: return

        if (showDialog) {
            AlertDialog(onDismissRequest = {
                showDialog = false
            }, confirmButton = {
                TextButton(onClick = { activity.finish() }) { Text("확인") }
            }, title = {
                Text(text = "할일 추가 취소")
            }, text = {
                Text(text = "할일 추가를 취소하시겠습니까?")
            }, dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("취소") }
            })
        }

        BackHandler { showDialog = true }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BackHandlerWithQuestionDialog(false)
                }
            }
        }
    }

    @FlowPreview
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @ExperimentalMaterial3Api
    @Composable
    fun InputScreen(
        title: String,
        memo: String,
        circleColor: Color,
        isTodoInputMode: Boolean,
        onTitleChange: (String) -> Unit,
        onTextChange: (String) -> Unit,
        onColorChange: (Color) -> Unit
    ) {

        Scaffold(topBar = {
            val colorController = rememberColorPickerController()
            var showBackDialog by remember { mutableStateOf(false) }
            var showColorPicker by remember { mutableStateOf(false) }
            if (showBackDialog) {
                BackHandlerWithQuestionDialog(true)
            }

            if (showColorPicker) {
                Dialog(onDismissRequest = { showColorPicker = false }) {
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .padding(10.dp),
                        controller = colorController,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            onColorChange.invoke(colorEnvelope.color)
                        }
                    )
                }
            }

            TopAppBar(title = {
                Text(text = "Memo", color = MaterialTheme.colorScheme.onPrimary)
            }, navigationIcon = {
                IconButton(onClick = { showBackDialog = true }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }, actions = {
                Row {
                    Canvas(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                            .offset(y = 4.dp)
                            .clickable {
                                showColorPicker = true
                            }
                    ) {
                        drawCircle(
                            color = circleColor,
                            radius = size.minDimension / 2
                        )
                    }
                    Button(onClick = {
                        val currentTime = System.currentTimeMillis()
                        todoViewModel.updateTodo(
                            Task(
                                title = title,
                                dtCreated = currentTime,
                                dtUpdated = currentTime,
                                color = circleColor.toHex(),
                                isDone = false,
                                position = currentTime
                            )
                        )
                        finish()
                    }) {
                        Text("Save")
                    }
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onSecondary
            )
            )
        }) {
            Column(modifier = Modifier.padding(it)) {
                InputField(memo, onTextChange = { value ->
                    onTextChange.invoke(value)
                })
            }
        }
    }

    @FlowPreview
    @Composable
    fun InputField(
        text: String,
        onTextChange: (String) -> Unit
    ) {
        val textFieldModifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))

        val textColor = MaterialTheme.colorScheme.onBackground
        val placeholderColor = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray

        val textStyle = TextStyle(
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            color = textColor
        )

        BasicTextField(
            value = text, onValueChange = onTextChange, modifier = textFieldModifier,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) Text("What's happening?", color = placeholderColor)
                innerTextField()
            }, textStyle = textStyle
        )
    }
}