package com.windrr.mindbank.presentation.ui.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.windrr.mindbank.R
import android.widget.Toast
import com.windrr.mindbank.presentation.ui.activity.PasswordMenuActivity
import com.windrr.mindbank.util.AppLanguageState
import com.windrr.mindbank.viewmodel.BackupViewModel
import com.windrr.mindbank.viewmodel.DataStoreViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
private fun AppLockSection(
    appLockEnabled: Boolean,
    onOpenPasswordMenu: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenPasswordMenu() } // 섹션 전체 클릭 시 메뉴 이동
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.app_lock_title), // "앱 잠금 설정" 등으로 변경 권장
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            // 현재 잠금 활성화 상태를 텍스트로 표시 (선택 사항)
            Text(
                text = if (appLockEnabled) "ON" else "OFF",
                color = if (appLockEnabled) Color.Green else Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun LanguageSelectorDialog(
    currentSelection: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val options = listOf("한국어", "English", "日本語", "Tiếng Việt")
    var selectedOption by remember { mutableStateOf(currentSelection) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column {
                options.forEach { language ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (language == selectedOption),
                                onClick = { selectedOption = language })
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (language == selectedOption),
                            onClick = { selectedOption = language })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = language)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedOption) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    dataStoreViewModel: DataStoreViewModel,
    onConfirmDelete: () -> Unit,
) {
    val context = LocalContext.current
    val backupViewModel: BackupViewModel = hiltViewModel()
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var showDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember {
        mutableStateOf(
            prefs.getString(
                "language",
                Locale.getDefault().language
            ) ?: "ko"
        )
    }

    var appLockEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var hasPinOrBiometric by remember { mutableStateOf(false) }
    val biometricAvailable = remember {
        BiometricManager.from(context)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        coroutineScope.launch {
            runCatching { backupViewModel.import(context, uri) }
                .onSuccess { (memos, todos) ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.backup_import_success, memos, todos),
                        Toast.LENGTH_LONG
                    ).show()
                }
                .onFailure {
                    Toast.makeText(context, context.getString(R.string.backup_import_fail), Toast.LENGTH_SHORT).show()
                }
        }
    }

    suspend fun reloadLockState() {
        appLockEnabled = dataStoreViewModel.isAppLockEnabled()
        biometricEnabled = dataStoreViewModel.isBiometricEnabled()
        val passwordHash = dataStoreViewModel.getPasswordHash()
        val passwordSalt = dataStoreViewModel.getPasswordSalt()
        val legacyPassword = dataStoreViewModel.getPassWord()
        val hasPin = passwordHash.isNotEmpty() || passwordSalt.isNotEmpty() || legacyPassword.isNotEmpty()
        hasPinOrBiometric = hasPin || (biometricAvailable && biometricEnabled)
    }

    LaunchedEffect(Unit) { reloadLockState() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch { reloadLockState() }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            item {
                // 1. 앱 잠금 메뉴 진입 버튼
                AppLockSection(
                    appLockEnabled = appLockEnabled,
                    onOpenPasswordMenu = {
                        context.startActivity(Intent(context, PasswordMenuActivity::class.java))
                    }
                )

                // 2. 언어 변경 버튼
                PasswordEditBtn(
                    title = stringResource(R.string.language_change),
                    onClick = { showDialog = true }
                )

                // 3. 데이터 내보내기
                PasswordEditBtn(
                    title = stringResource(R.string.backup_export),
                    onClick = {
                        if (!isExporting) {
                            isExporting = true
                            coroutineScope.launch {
                                runCatching { backupViewModel.export(context) }
                                    .onSuccess { uri ->
                                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "application/json"
                                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, context.getString(R.string.backup_export_chooser)))
                                    }
                                    .onFailure {
                                        Toast.makeText(context, context.getString(R.string.backup_export_fail), Toast.LENGTH_SHORT).show()
                                    }
                                isExporting = false
                            }
                        }
                    }
                )

                // 4. 데이터 가져오기
                PasswordEditBtn(
                    title = stringResource(R.string.backup_import),
                    onClick = { showImportConfirmDialog = true }
                )

                // 5. 데이터 삭제 버튼
                DeleteButton(
                    title = stringResource(R.string.delete_all_data),
                    onConfirmDelete = onConfirmDelete
                )
            }
        }
    }
    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showImportConfirmDialog = false },
            title = { Text(stringResource(R.string.backup_import)) },
            text = { Text(stringResource(R.string.backup_import_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showImportConfirmDialog = false
                    importLauncher.launch(arrayOf("application/json", "*/*"))
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirmDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDialog) {
        LanguageSelectorDialog(
            currentSelection = selectedLanguage,
            onDismiss = { showDialog = false },
            onConfirm = { lang ->
                selectedLanguage = lang
                showDialog = false
                val langCode = when (lang) {
                    "한국어" -> "ko"
                    "English" -> "en"
                    "日本語" -> "ja"
                    "Tiếng Việt" -> "vi"
                    else -> "en"
                }
                AppLanguageState().updateLocale(context, langCode)
                (context as? Activity)?.recreate()
            })
    }
}

@Composable
fun DeleteButton(title: String, onConfirmDelete: () -> Unit) {
    var expand by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { expand = !expand })
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_cleaning_services_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title, color = Color.White, fontSize = 18.sp, modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        AnimatedVisibility(visible = expand) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }

    if (showDialog) {
        ConfirmDeleteDialog(onConfirm = {
            showDialog = false
            onConfirmDelete()
        }, onDismiss = { showDialog = false })
    }
}

@Composable
fun PasswordEditBtn(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title, color = Color.White, fontSize = 18.sp, modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to $title",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() }, title = {
        Text(
            text = stringResource(R.string.confirm_delete),
            style = MaterialTheme.typography.titleMedium
        )
    }, text = {
        Text(text = stringResource(R.string.confirm_delete_description))
    }, confirmButton = {
        Button(onClick = onConfirm) {
            Text(text = stringResource(R.string.confirm), color = Color.White)
        }
    }, dismissButton = {
        OutlinedButton(onClick = onDismiss) {
            Text(text = stringResource(R.string.cancel))
        }
    })
}