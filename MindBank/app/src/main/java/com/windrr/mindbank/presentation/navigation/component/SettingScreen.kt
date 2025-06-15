package com.windrr.mindbank.presentation.navigation.component

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.windrr.mindbank.R
import com.windrr.mindbank.presentation.navigation.theme.MindBankTheme
import com.windrr.mindbank.util.AppLanguageState
import java.util.Locale

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    languageState: AppLanguageState,
    onConfirmDelete: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        MindBankTheme {
            Scaffold(topBar = {
                MainTopBar(stringResource(R.string.settings_title))
            }, content = {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(onConfirmDelete = {
                        onConfirmDelete.invoke()
                    })
                }
            })
        }
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

    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.select_language)) }, text = {
        Column {
            options.forEach { language ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (language == selectedOption),
                            onClick = { selectedOption = language })
                        .padding(vertical = 4.dp)) {
                    RadioButton(
                        selected = (language == selectedOption),
                        onClick = { selectedOption = language })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = language)
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = { onConfirm(selectedOption) }) {
            Text(stringResource(R.string.confirm))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        }
    })
}

@Composable
fun SettingsScreen(onConfirmDelete: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var showDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(prefs.getString("language", Locale.getDefault().language) ?: "ko") }
    LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
        item {
            DeleteButton(
                title = stringResource(R.string.delete_all_data), onConfirmDelete = onConfirmDelete
            )
            PasswordEditBtn(
                title = stringResource(R.string.language_change), onClick = {
                    showDialog = true
                })
        }
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
    Column(modifier = Modifier
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