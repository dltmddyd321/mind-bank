@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mindbank.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mindbank.data.Task
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.util.hexToColor
import com.example.mindbank.util.toHex
import com.example.mindbank.viewmodel.TodoViewModel
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
class AddTodoActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()
    private var editId = -1

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

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    var title by remember { mutableStateOf("") }
                    var circleColor by remember { mutableStateOf(Color.Red) }
                    editId = intent?.getIntExtra("id", -1) ?: -1
                    var editingData by remember { mutableStateOf<Task?>(null) }
                    var isEditMode by remember { mutableStateOf(false) }

                    LaunchedEffect(editId) {
                        editingData = todoViewModel.searchById(editId)
                        title = editingData?.title ?: ""
                        val lastColor = editingData?.color
                        if (!lastColor.isNullOrEmpty()) circleColor = hexToColor(lastColor)
                        if (editingData != null) isEditMode = true
                    }

                    BackHandlerWithQuestionDialog(false)
                    InputScreen(
                        title,
                        circleColor,
                        isEditMode,
                        onTextChange = {
                            title = it
                        },
                        onColorChange = {
                            circleColor = it
                        })
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
        circleColor: Color,
        isEditMode: Boolean,
        onTextChange: (String) -> Unit,
        onColorChange: (Color) -> Unit
    ) {
        val context = LocalContext.current
        Scaffold(topBar = {
            val colorController = rememberColorPickerController()
            var showBackDialog by remember { mutableStateOf(false) }
            var showColorPicker by remember { mutableStateOf(false) }
            if (showBackDialog) {
                BackHandlerWithQuestionDialog(true)
            }

            if (showColorPicker) {
                var selectedColor by remember { mutableStateOf(Color.Red) }

                Dialog(onDismissRequest = { showColorPicker = false }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HsvColorPicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(450.dp),
                            controller = colorController,
                            onColorChanged = { colorEnvelope: ColorEnvelope ->
                                selectedColor = colorEnvelope.color
                                onColorChange.invoke(selectedColor)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onColorChange.invoke(selectedColor)
                                showColorPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("확인", fontSize = 18.sp)
                        }
                    }
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
                        if (title.isBlank()) {
                            Toast.makeText(context, "내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val currentTime = System.currentTimeMillis()
                        val task = if (isEditMode) {
                            Task(
                                id = editId,
                                title = title,
                                dtCreated = currentTime,
                                dtUpdated = currentTime,
                                color = circleColor.toHex(),
                                isDone = false,
                                position = currentTime
                            )
                        } else {
                            Task(
                                title = title,
                                dtCreated = currentTime,
                                dtUpdated = currentTime,
                                color = circleColor.toHex(),
                                isDone = false,
                                position = currentTime
                            )
                        }
                        todoViewModel.updateTodo(task)
                        setResult(RESULT_OK)
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
                InputField(title, onTextChange = { value ->
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
            value = text,
            onValueChange = onTextChange,
            modifier = textFieldModifier,
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) Text("What's happening?", color = placeholderColor)
                innerTextField()
            },
            textStyle = textStyle
        )
    }
}