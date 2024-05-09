package com.example.mindbank.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindbank.activity.ui.theme.MindBankTheme
import com.example.mindbank.db.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@AndroidEntryPoint
@ExperimentalMaterial3Api
class AddActivity : ComponentActivity() {

    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AutoBackUpCheckDialog(dataStoreViewModel)
                    BackHandlerWithQuestionDialog(false)
                    InputScreen(dataStoreViewModel)
                }
            }
        }
    }
}

@Composable
fun AutoBackUpCheckDialog(viewModel: DataStoreViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var unSavedData: String

    LaunchedEffect(key1 = Unit) {
        unSavedData = viewModel.getUnSavedData()
        if (unSavedData.isNotEmpty()) showDialog = true
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
        }, title = {
            Text(text = "백업 데이터 불러오기")
        }, confirmButton = {
            TextButton(onClick = {
                //TODO: 백업 데이터 불러오기
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
    val activity = LocalContext.current as? Activity

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
        }, confirmButton = {
            TextButton(onClick = { activity?.finish() }) { Text("확인") }
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
fun InputScreen(viewModel: DataStoreViewModel) {
    var text by remember { mutableStateOf("") }
    Scaffold(topBar = {
        AddTopBar(onSave = {
            viewModel.setUnSavedData(text)
        })
    }) {
        InputField(text, onTextChange = { value ->
            text = value
        }, it, viewModel)
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddTopBar(onSave: () -> Unit) {

    var showBackDialog by remember { mutableStateOf(false) }
    if (showBackDialog) {
        BackHandlerWithQuestionDialog(true)
    }

    TopAppBar(title = {
        Text(text = "Save", color = MaterialTheme.colorScheme.onPrimary)
    }, navigationIcon = {
        IconButton(onClick = { showBackDialog = true }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
    }, actions = {
        Button(onClick = onSave) {
            Text("Save")
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onSecondary
    )
    )
}

@FlowPreview
@Composable
fun InputField(
    text: String,
    onTextChange: (String) -> Unit,
    paddingValues: PaddingValues,
    viewModel: DataStoreViewModel
) {
    val textFieldModifier = Modifier
        .padding(paddingValues)
        .padding(16.dp)
        .fillMaxWidth()
        .fillMaxHeight()
        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
        .onFocusChanged {
            if (!it.isFocused) viewModel.setUnSavedData(text)
        }

    val textStyle = TextStyle(
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
        color = Color.Black
    )

    LaunchedEffect(key1 = text) {
        snapshotFlow { text }
            .debounce(1000)
            .collect {
                viewModel.setUnSavedData(it)
            }
    }

    BasicTextField(
        value = text, onValueChange = onTextChange, modifier = textFieldModifier,
        decorationBox = { innerTextField ->
            if (text.isEmpty()) Text("What's happening?", color = Color.Gray)
            innerTextField()
        }, textStyle = textStyle
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MindBankTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ReCompositionTest()
        }
    }
}

@Composable
fun ReCompositionTest() {
    //상태 저장 변수 선언
    var text by remember { mutableStateOf("안녕하세요?") }

    //다크 모드 사용 여부 파악
    val isSystemInDarkTheme = isSystemInDarkTheme()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { text = "반갑습니다!" }) {
            Text("클릭하세요")
        }
        TbText(
            modifier = Modifier.padding(top = 16.dp),
            text = text,
            isDarkMode = isSystemInDarkTheme,
            labelSize = 20.sp,
            labelWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TbLogo(
    isDarkMode: Boolean,
    logoIcon: Painter,
    logoSize: Dp,
    labelSize: TextUnit,
    labelWeight: FontWeight
) {
    Box(
        modifier = Modifier
            .background(
                if (isDarkMode) Color.Black else Color.White, shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = logoIcon,
                contentDescription = "Logo",
                modifier = Modifier.size(logoSize)
            )
            TbText(
                modifier = Modifier.padding(start = 8.dp),
                text = "TimeBlocks",
                isDarkMode = isDarkMode,
                labelSize = labelSize,
                labelWeight = labelWeight
            )
        }
    }
}

@Composable
fun TbText(
    modifier: Modifier,
    text: String,
    isDarkMode: Boolean,
    labelSize: TextUnit,
    labelWeight: FontWeight,
    labelAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        color = if (isDarkMode) Color.White else Color.Black,
        fontSize = labelSize,
        fontWeight = labelWeight,
        textAlign = labelAlign,
        modifier = modifier
    )
}