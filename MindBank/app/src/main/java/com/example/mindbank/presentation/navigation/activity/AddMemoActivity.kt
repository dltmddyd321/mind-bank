package com.example.mindbank.presentation.navigation.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mindbank.R
import com.example.mindbank.db.data.Memo
import com.example.mindbank.presentation.navigation.theme.MindBankTheme
import com.example.mindbank.util.hexToColor
import com.example.mindbank.util.toHex
import com.example.mindbank.viewmodel.DataStoreViewModel
import com.example.mindbank.viewmodel.MemoViewModel
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AddMemoActivity : ComponentActivity() {

    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val memoViewModel: MemoViewModel by viewModels()
    private var lastUpdatedTime = System.currentTimeMillis()
    private var editMemo: Memo? = null
    private var editId = -1
    private var isEditMode = false

    @OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    editId = intent?.getIntExtra("id", -1) ?: -1
                    var title by remember { mutableStateOf("") }
                    var link by remember { mutableStateOf("") }
                    var memo by remember { mutableStateOf("") }
                    var circleColor by remember { mutableStateOf(Color.Red) }

                    LaunchedEffect(editId) {
                        editMemo = memoViewModel.searchById(editId)
                        title = editMemo?.title ?: ""
                        link = editMemo?.link ?: ""
                        memo = editMemo?.detail ?: ""
                        lastUpdatedTime = editMemo?.dtUpdated ?: System.currentTimeMillis()
                        val lastColor = editMemo?.color
                        if (!lastColor.isNullOrEmpty()) {
                            isEditMode = true
                            circleColor = hexToColor(lastColor)
                        }
                    }

                    AutoBackUpCheckDialog(dataStoreViewModel) { backupTitle, backupLink, backupMemo, backupColor ->
                        title = backupTitle
                        link = backupLink
                        memo = backupMemo
                        circleColor = hexToColor(backupColor)
                        isEditMode = true
                    }
                    BackHandlerWithQuestionDialog(false)
                    InputScreen(
                        title,
                        memo,
                        circleColor,
                        onTitleChange = {
                            title = it
                        },
                        onTextChange = {
                            memo = it
                        },
                        onColorChange = {
                            circleColor = it
                        }
                    )
                }
            }
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    @Composable
    fun AutoBackUpCheckDialog(
        viewModel: DataStoreViewModel,
        onBackupLoad: (String, String, String, String) -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var initTitle = ""
        var initLink = ""
        var initMemo = ""
        var initColor = ""

        LaunchedEffect(key1 = Unit) {
            val unSaved = viewModel.getUnSavedData()
            initTitle = unSaved.title
            initLink = unSaved.link
            initMemo = unSaved.memo
            initColor = unSaved.color
            if (initTitle.isNotEmpty() || initMemo.isNotEmpty() || initLink.isNotEmpty()) showDialog = true
        }

        if (showDialog) {
            AlertDialog(onDismissRequest = {
                showDialog = false
            }, title = {
                Text(text = "백업 데이터 불러오기")
            }, confirmButton = {
                TextButton(onClick = {
                    onBackupLoad.invoke(initTitle, initLink, initMemo, initColor)
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
                Text(text = if (isEditMode) "메모 수정 취소" else "메모 추가 취소")
            }, text = {
                Text(text = if (isEditMode) "메모 수정을 취소하시겠습니까?" else "메모 추가를 취소하시겠습니까?")
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

            TopAppBar(
                title = {
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
                            if (editId != -1) {
                                editMemo?.let {
                                    it.title = title
                                    it.detail = memo
                                    it.dtUpdated = System.currentTimeMillis()
                                    it.color = circleColor.toHex()
                                    memoViewModel.insertData(it)
                                }
                            } else {
                                memoViewModel.insertData(
                                    Memo(
                                        title = title,
                                        detail = memo,
                                        dtCreated = System.currentTimeMillis(),
                                        dtUpdated = System.currentTimeMillis(),
                                        color = circleColor.toHex()
                                    )
                                )
                            }
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
                var url by remember { mutableStateOf("") }

                Spacer(modifier = Modifier.height(16.dp))

                TitleInputField(title, onTitleChange = { value ->
                    onTitleChange.invoke(value)
                })

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    value = url,
                    cursorBrush = SolidColor(Color.White),
                    onValueChange = { value ->
                        url = value
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .onFocusChanged { value ->
                            if (!value.isFocused) dataStoreViewModel.setUnSavedLink(url)
                        },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        if (url.isEmpty()) {
                            Text(
                                getString(R.string.input_url),
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                InputField(memo, onTextChange = { value ->
                    onTextChange.invoke(value)
                })

                Spacer(modifier = Modifier.height(8.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Color.Gray.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isEditMode) {
                    val formattedDate = remember(lastUpdatedTime) {
                        val sdf = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분", Locale.getDefault())
                        sdf.format(Date(lastUpdatedTime))
                    }

                    Text(
                        text = "마지막 업데이트: $formattedDate",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }

    @Composable
    fun TitleInputField(title: String, onTitleChange: (String) -> Unit) {
        BasicTextField(
            value = title,
            cursorBrush = SolidColor(Color.White),
            onValueChange = onTitleChange,
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .onFocusChanged {
                    if (!it.isFocused) dataStoreViewModel.setUnSavedTitle(title)
                },
            decorationBox = { innerTextField ->
                if (title.isEmpty()) {
                    Text(
                        "Title",
                        color = Color.Gray,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                innerTextField()
            }
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
            .wrapContentHeight()
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

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            cursorBrush = SolidColor(Color.White),
            modifier = textFieldModifier,
            textStyle = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = textColor
            ),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(getString(R.string.input_memo), color = placeholderColor, fontSize = 18.sp)
                }
                innerTextField()
            }
        )
    }
}