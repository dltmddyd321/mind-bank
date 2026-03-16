@file:OptIn(ExperimentalMaterial3Api::class)

package com.windrr.mindbank.presentation.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.presentation.ui.theme.SpaceBorder
import com.windrr.mindbank.presentation.ui.theme.SpaceCloud
import com.windrr.mindbank.presentation.ui.theme.SpacePurple
import com.windrr.mindbank.presentation.ui.theme.SpaceStar
import com.windrr.mindbank.presentation.ui.theme.SpaceSurface
import com.windrr.mindbank.presentation.ui.theme.SpaceTheme
import com.windrr.mindbank.util.hexToColor
import com.windrr.mindbank.util.toHex
import com.windrr.mindbank.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddTodoActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()
    private var alarmTime = 0L
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
                TextButton(onClick = { activity.finish() }) { Text(stringResource(R.string.confirm)) }
            }, title = {
                Text(text = stringResource(R.string.cancel_add_todo_title))
            }, text = {
                Text(text = stringResource(R.string.cancel_add_todo_message))
            }, dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.cancel)) }
            })
        }

        BackHandler { showDialog = true }
    }

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceTheme {
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
                        alarmTime = editingData?.alarmTime ?: 0L
                        if (!lastColor.isNullOrEmpty()) circleColor = hexToColor(lastColor)
                        if (editingData != null) isEditMode = true
                    }

                    BackHandlerWithQuestionDialog(false)
                    InputScreen(title, circleColor, isEditMode, onTextChange = {
                        title = it
                    }, onColorChange = {
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
                            })

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onColorChange.invoke(selectedColor)
                                showColorPicker = false
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.confirm), fontSize = 18.sp)
                        }
                    }
                }
            }

            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.todo_title), color = SpaceCloud)
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
                                }) {
                            drawCircle(
                                color = circleColor, radius = size.minDimension / 2
                            )
                        }
                        Button(
                            onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(
                                    context,
                                    getString(R.string.toast_empty_todo),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            val currentTime = System.currentTimeMillis()
                            if (isEditMode) {
                                val task = Task(
                                    id = editId,
                                    title = title,
                                    dtCreated = currentTime,
                                    dtUpdated = currentTime,
                                    color = circleColor.toHex(),
                                    isDone = false,
                                    position = currentTime,
                                    alarmTime
                                )
                                todoViewModel.updateTodo(task)
                            } else {
                                val task = Task(
                                    title = title,
                                    dtCreated = currentTime,
                                    dtUpdated = currentTime,
                                    color = circleColor.toHex(),
                                    isDone = false,
                                    position = currentTime,
                                    alarmTime = alarmTime
                                )
                                todoViewModel.saveTodo(task)
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                InputField(title, onTextChange = { value ->
                    onTextChange.invoke(value)
                })
            }
        }
    }

    @FlowPreview
    @Composable
    fun InputField(
        text: String, onTextChange: (String) -> Unit
    ) {
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeholder = {
                        val placeholderColor = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
                        Text(text = getString(R.string.input_todo), color = placeholderColor)
                    },
                    textStyle = MaterialTheme.typography.titleMedium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = SpacePurple,
                        unfocusedIndicatorColor = SpaceBorder.copy(alpha = 0.5f),
                        cursorColor = SpaceStar
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = false
                )
            }
        }
    }

    @Composable
    fun AlarmSelector(
        lastAlarm: Long, onTimeSelected: (Long) -> Unit
    ) {
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        var selectedTime by remember { mutableStateOf(LocalTime.now()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            if (lastAlarm > 0L) {
                val formattedDate = remember(lastAlarm) {
                    val locale = Locale.getDefault()
                    val pattern = when (locale.language) {
                        "ko" -> "yyyy년 MM월 dd일 HH시 mm분" // 한국어
                        "ja" -> "yyyy年 MM月 dd日 HH時 mm分" // 일본어
                        else -> "yyyy/MM/dd HH:mm" // 기본 (영어)
                    }
                    val sdf = SimpleDateFormat(pattern, locale)
                    sdf.format(Date(lastAlarm))
                }

                Text(
                    text = String.format(stringResource(R.string.alarm_time_prefix), formattedDate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            } else {
                Text(
                    text = stringResource(R.string.add_alarm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    onDateChange = { date ->
                        selectedDate = date
                        showDatePicker = false
                        showTimePicker = true
                    })
            }

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    onTimeSelected = { time ->
                        selectedTime = time
                        showTimePicker = false

                        val zonedDateTime = ZonedDateTime.of(
                            selectedDate, selectedTime, ZoneId.systemDefault()
                        )
                        onTimeSelected(zonedDateTime.toInstant().toEpochMilli())
                    })
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit, onDateChange: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val today = remember { Calendar.getInstance() }

    val dialog = remember {
        DatePickerDialog(
            context, { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                onDateChange(date)
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        )
    }

    DisposableEffect(Unit) {
        dialog.setOnDismissListener { onDismissRequest() }
        dialog.show()
        onDispose { dialog.dismiss() }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit, onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val now = remember { Calendar.getInstance() }

    val dialog = remember {
        TimePickerDialog(
            context, { _, hourOfDay, minute ->
                val time = LocalTime.of(hourOfDay, minute)
                onTimeSelected(time)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
        )
    }

    DisposableEffect(Unit) {
        dialog.setOnDismissListener { onDismissRequest() }
        dialog.show()
        onDispose { dialog.dismiss() }
    }
}