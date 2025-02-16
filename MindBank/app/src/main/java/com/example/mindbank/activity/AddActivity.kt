package com.example.mindbank.activity

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mindbank.data.SaveData
import com.example.mindbank.navigation.Screen
import com.example.mindbank.viewmodel.DataStoreViewModel
import com.example.mindbank.viewmodel.DataViewModel
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.util.hexToColor
import com.example.mindbank.util.toHex
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


@AndroidEntryPoint
class AddActivity : ComponentActivity() {

    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val mode = intent?.getStringExtra("mode") ?: Screen.Notes.title
                    val id = intent?.getIntExtra("id", -1) ?: -1
                    var editingData by remember { mutableStateOf<SaveData?>(null) }

                    var title by remember { mutableStateOf("") }
                    var memo by remember { mutableStateOf("") }
                    var circleColor by remember { mutableStateOf(Color.Red) }

                    LaunchedEffect(id) {
                        editingData = dataViewModel.searchById(id)
                        title = editingData?.title ?: ""
                        memo = editingData?.detail ?: ""
                        val lastColor = editingData?.color
                        if (!lastColor.isNullOrEmpty()) circleColor = hexToColor(lastColor)
                    }

                    AutoBackUpCheckDialog(dataStoreViewModel) { backupTitle, backupMemo, backupColor ->
                        title = backupTitle
                        memo = backupMemo
                        circleColor = hexToColor(backupColor)
                    }
                    BackHandlerWithQuestionDialog(false)
                    InputScreen(
                        title,
                        memo,
                        circleColor,
                        isTodoInputMode = mode == Screen.Todo.title,
                        onTitleChange = {
                            title = it
                        },
                        onTextChange = {
                            memo = it
                        },
                        onColorChange = {
                            circleColor = it
                        })
                }
            }
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    @Composable
    fun AutoBackUpCheckDialog(
        viewModel: DataStoreViewModel,
        onBackupLoad: (String, String, String) -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var initTitle = ""
        var initMemo = ""
        var initColor = ""

        LaunchedEffect(key1 = Unit) {
            val unSaved = viewModel.getUnSavedData()
            initTitle = unSaved.title
            initMemo = unSaved.memo
            initColor = unSaved.color
            if (initTitle.isNotEmpty() || initMemo.isNotEmpty()) showDialog = true
        }

        if (showDialog) {
            AlertDialog(onDismissRequest = {
                showDialog = false
            }, title = {
                Text(text = "백업 데이터 불러오기")
            }, confirmButton = {
                TextButton(onClick = {
                    onBackupLoad.invoke(initTitle, initMemo, initColor)
                    showDialog = false
                }) { Text("확인") }
            }, text = {
                Text(
                    text = "저장이 완료되지 않은 데이터가 있습니다.\n마저 작성하시겠습니까?", textAlign = TextAlign.Center
                )
            }, dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("취소") }
            })
        }
    }

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
                Text(text = "메모 추가 취소")
            }, text = {
                Text(text = "메모 추가를 취소하시겠습니까?")
            }, dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("취소") }
            })
        }

        BackHandler { showDialog = true }
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
                            dataStoreViewModel.setUnSavedColor(colorEnvelope.hexCode)
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
                        dataViewModel.insertData(
                            SaveData(
                                title = title,
                                detail = memo,
                                dtCreated = System.currentTimeMillis(),
                                dtUpdated = System.currentTimeMillis(),
                                color = circleColor.toHex()
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
                TitleInputField(title, onTitleChange = { value ->
                    onTitleChange.invoke(value)
                })
                if (!isTodoInputMode) {
                    InputField(memo, onTextChange = { value ->
                        onTextChange.invoke(value)
                    })
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TitleInputField(title: String, onTitleChange: (String) -> Unit) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            maxLines = 1,  // 제목 필드가 한 줄만 입력 가능하도록 제한
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .onFocusChanged {
                    if (!it.isFocused) dataStoreViewModel.setUnSavedTitle(title)
                },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
        )
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
            .onFocusChanged {
                if (!it.isFocused) dataStoreViewModel.setUnSavedMemo(text)
            }

        LaunchedEffect(key1 = text) {
            snapshotFlow { text }
                .debounce(1000)
                .collect {
                    dataStoreViewModel.setUnSavedMemo(it)
                }
        }

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