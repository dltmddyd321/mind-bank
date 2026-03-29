package com.windrr.mindbank.presentation.ui.component

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.util.hexToColor
import com.windrr.mindbank.util.isDarkColor

@Composable
fun MemoItemView(
    data: Memo,
    onClick: (Memo) -> Unit,
    onEdit: (Memo) -> Unit,
    onDelete: (Memo) -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor = hexToColor(data.color)
    val textColor = if (isDarkColor(backgroundColor)) Color.White else Color.Black
    val hasLink = !data.link.isNullOrEmpty()
    val hasDetail = data.detail.isNotEmpty()

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(text: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("memo", text))
        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.question_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(data)
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { onClick(data) }
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                // 제목 행: 제목 + ⋮ 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.title,
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More",
                                modifier = Modifier.size(18.dp),
                                tint = textColor
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // 편집
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_edit)) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_edit_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit(data)
                                }
                            )

                            // 링크 복사
                            if (hasLink) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.copy_link)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_content_copy),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        copyToClipboard(data.link!!)
                                    }
                                )
                            }

                            // 내용 복사
                            if (hasDetail) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.copy_content)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_content_copy),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        copyToClipboard(data.detail)
                                    }
                                )
                            }

                            // 링크 공유
                            if (hasLink) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.share_link)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Share,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, data.link)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, null))
                                    }
                                )
                            }

                            Divider()

                            // 삭제
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.action_delete),
                                        color = Color.Red
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_close_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Red
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }

                // 링크 영역
                val link = data.link
                if (!link.isNullOrEmpty()) {
                    Divider(
                        color = textColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    var currentUrl by remember { mutableStateOf<String?>(null) }
                    val activity = context as? Activity ?: return@Column

                    if (currentUrl != null) {
                        currentUrl?.let {
                            activity.startActivity(Intent(Intent.ACTION_VIEW, it.toUri()))
                            currentUrl = null
                        }
                    } else {
                        HyperlinkText(
                            modifier = Modifier.fillMaxWidth(),
                            text = link,
                            style = MaterialTheme.typography.bodyMedium
                        ) {
                            currentUrl = it
                        }
                    }
                }

                // 내용 영역
                if (hasDetail) {
                    Divider(
                        color = textColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = data.detail,
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
