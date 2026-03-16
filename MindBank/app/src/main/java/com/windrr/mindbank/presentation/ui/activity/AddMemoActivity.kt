package com.windrr.mindbank.presentation.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.presentation.ui.theme.SpaceBorder
import com.windrr.mindbank.presentation.ui.theme.SpaceCloud
import com.windrr.mindbank.presentation.ui.theme.SpacePurple
import com.windrr.mindbank.presentation.ui.theme.SpaceStar
import com.windrr.mindbank.presentation.ui.theme.SpaceSurface
import com.windrr.mindbank.presentation.ui.theme.SpaceTheme
import com.windrr.mindbank.util.hexToColor
import com.windrr.mindbank.util.toHex
import com.windrr.mindbank.viewmodel.DataStoreViewModel
import com.windrr.mindbank.viewmodel.MemoViewModel
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
            SpaceTheme {
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
                        link,
                        memo,
                        circleColor,
                        onTitleChange = {
                            title = it
                        },
                        onLinkChange = {
                            link = it
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
            if (initTitle.isNotEmpty() || initMemo.isNotEmpty() || initLink.isNotEmpty()) showDialog =
                true
        }

        if (showDialog) {
            AlertDialog(onDismissRequest = {
                showDialog = false
            }, title = {
                Text(text = stringResource(R.string.dialog_backup_title))
            }, confirmButton = {
                TextButton(onClick = {
                    onBackupLoad.invoke(initTitle, initLink, initMemo, initColor)
                    showDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            }, text = {
                Text(
                    text = stringResource(R.string.dialog_backup_message),
                    textAlign = TextAlign.Center
                )
            }, dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) { Text(stringResource(R.string.cancel)) }
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
                TextButton(onClick = { activity.finish() }) { Text(stringResource(R.string.confirm)) }
            }, title = {
                Text(
                    text = if (isEditMode) stringResource(R.string.dialog_cancel_edit_title) else stringResource(
                        R.string.dialog_cancel_add_title
                    )
                )
            }, text = {
                Text(
                    text = if (isEditMode) stringResource(R.string.dialog_cancel_edit_message) else stringResource(
                        R.string.dialog_cancel_add_message
                    )
                )
            }, dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) { Text(stringResource(R.string.cancel)) }
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
        link: String,
        memo: String,
        circleColor: Color,
        onTitleChange: (String) -> Unit,
        onLinkChange: (String) -> Unit,
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
                            Text(stringResource(R.string.confirm), fontSize = 18.sp)
                        }
                    }
                }
            }

            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.memo_title),
                        color = SpaceCloud
                    )
                }, navigationIcon = {
                    IconButton(onClick = { showBackDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SpaceStar
                        )
                    }
                }, actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(36.dp)
                                .padding(6.dp)
                                .offset(y = 2.dp)
                                .clickable {
                                    showColorPicker = true
                                }
                        ) {
                            drawCircle(
                                color = circleColor,
                                radius = size.minDimension / 2
                            )
                        }
                        Button(
                            onClick = {
                            if (title.isBlank() && memo.isBlank()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.input_contents),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            if (editId != -1) {
                                editMemo?.let {
                                    it.title = title
                                    it.link = link
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
                                        color = circleColor.toHex(),
                                        link = link.ifBlank { null }
                                    )
                                )
                            }
                            setResult(RESULT_OK)
                            finish()
                            },
                            modifier = Modifier.height(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            )
                        ) {
                            Text(stringResource(R.string.action_save))
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceSurface,
                    titleContentColor = SpaceCloud,
                    navigationIconContentColor = SpaceStar,
                    actionIconContentColor = SpaceStar
                )
            )
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                onTitleChange(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged {
                                    if (!it.isFocused) dataStoreViewModel.setUnSavedTitle(title)
                                },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.hint_title),
                                    color = Color.Gray
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = SpacePurple,
                                unfocusedIndicatorColor = SpaceBorder.copy(alpha = 0.5f),
                                cursorColor = SpaceStar
                            ),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = link,
                            onValueChange = {
                                onLinkChange(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focus ->
                                    if (!focus.isFocused) dataStoreViewModel.setUnSavedLink(link)
                                },
                            placeholder = {
                                Text(
                                    text = getString(R.string.input_url),
                                    color = Color.Gray
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = SpacePurple,
                                unfocusedIndicatorColor = SpaceBorder.copy(alpha = 0.5f),
                                cursorColor = SpaceStar
                            ),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        InputField(memo, onTextChange = { value ->
                            onTextChange.invoke(value)
                        })

                        Spacer(modifier = Modifier.height(8.dp))

                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Gray.copy(alpha = 0.25f),
                            thickness = 1.dp
                        )

                if (isEditMode) {
                    val formattedDate = remember(lastUpdatedTime) {
                        val locale = Locale.getDefault()
                        val pattern = when (locale.language) {
                            "ko" -> "yyyy년 MM월 dd일 HH시 mm분" // 한국어
                            "ja" -> "yyyy年 MM月 dd日 HH時 mm分" // 일본어
                            else -> "yyyy/MM/dd HH:mm" // 기본 (영어)
                        }
                        val sdf = SimpleDateFormat(pattern, locale)
                        sdf.format(Date(lastUpdatedTime))
                    }

                    Text(
                        text = String.format(
                            stringResource(R.string.label_last_updated),
                            formattedDate
                        ),
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
        }
    }

    @Composable
    fun TitleInputField(title: String, onTitleChange: (String) -> Unit) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_title),
                    color = Color.Gray
                )
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
            .fillMaxWidth()
            .fillMaxHeight()
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

        val placeholderColor = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = textFieldModifier,
            placeholder = {
                Text(text = getString(R.string.input_memo), color = placeholderColor)
            },
            textStyle = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = SpacePurple,
                unfocusedIndicatorColor = SpaceBorder.copy(alpha = 0.5f),
                cursorColor = SpaceStar
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}