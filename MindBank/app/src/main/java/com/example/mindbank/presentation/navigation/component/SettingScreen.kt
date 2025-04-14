package com.example.mindbank.presentation.navigation.component

import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindbank.R
import com.example.mindbank.presentation.navigation.activity.PasswordEditActivity
import com.example.mindbank.presentation.navigation.theme.MindBankTheme

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    onConfirmDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        MindBankTheme {
            Scaffold(
                topBar = {
                    MainTopBar("Settings")
                },
                content = {
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
                }
            )
        }
    }
}


@Composable
fun SettingsScreen(onConfirmDelete: () -> Unit) {
    val context = LocalContext.current
    LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
//        item {
//            VersionCheckButton(title = "최신 버전 확인", onClick = { /* Handle click */ })
//        }
        item {
            DeleteButton(title = "전체 데이터 초기화", onConfirmDelete = onConfirmDelete)
        }
        item {
            PasswordEditBtn(title = "비밀번호 변경 및 등록", onClick = {
                context.startActivity(Intent(context, PasswordEditActivity::class.java))
            })
        }
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
                contentDescription = "Go to $title",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to $title",
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
                        text = "Delete",
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
        ConfirmDeleteDialog(
            onConfirm = {
                showDialog = false
                onConfirmDelete()
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun PasswordEditBtn(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
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
                painter = painterResource(id = R.drawable.baseline_lock_outline_24),
                contentDescription = "Go to $title",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
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
fun VersionCheckButton(title: String, onClick: () -> Unit) {
    var expand by remember { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to $title",
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
                    onClick = { onClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Confirm Delete", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Text(text = "Are you sure you want to delete this? This action cannot be undone.")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Yes", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

private fun setLinkedText(
    textView: TextView,
    map: MutableMap<String, List<String>>,
    result: String,
    onConfirm: (List<String>) -> Unit
) {
    val normalizedResult = result.replace(" ", "")
    val spannableString = SpannableString(result)

    map.forEach { (word, uidList) ->
        val normalizedWord = word.replace(" ", "")
        var normalizedStartIndex = 0

        while (normalizedStartIndex < normalizedResult.length) {
            val matchIndex = normalizedResult.indexOf(normalizedWord, normalizedStartIndex)
            if (matchIndex == -1) break

            var originalStartIndex = 0
            var normalizedCount = 0

            for (i in result.indices) {
                if (!result[i].isWhitespace()) normalizedCount++
                if (normalizedCount == matchIndex + 1) {
                    originalStartIndex = i
                    break
                }
            }

            var originalEndIndex = originalStartIndex
            var normalizedWordCount = 0
            while (originalEndIndex < result.length && normalizedWordCount < normalizedWord.length) {
                if (!result[originalEndIndex].isWhitespace()) normalizedWordCount++
                originalEndIndex++
            }

            if (originalStartIndex < originalEndIndex) {
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onConfirm(uidList)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true
                    }
                }

                spannableString.setSpan(
                    clickableSpan,
                    originalStartIndex,
                    originalEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val boldStyle = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(
                    boldStyle,
                    originalStartIndex,
                    originalEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            normalizedStartIndex = matchIndex + normalizedWord.length
        }
    }
    textView.text = spannableString
    textView.movementMethod = LinkMovementMethod.getInstance()
}