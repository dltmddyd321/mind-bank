package com.windrr.mindbank.presentation.navigation.component

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { onClick.invoke(data) }) {
        val backgroundColor = hexToColor(data.color)
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(backgroundColor)
        ) {
            val textColor = if (isDarkColor(backgroundColor)) Color.White else Color.Black
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
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

                    var showDialog by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            onEdit.invoke(data)
                        }, modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(16.dp),
                            tint = textColor
                        )
                    }

                    IconButton(
                        onClick = { showDialog = true }, modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(16.dp),
                            tint = textColor
                        )
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                showDialog = false
                            }, title = {
                                Text(text = stringResource(R.string.question_delete))
                            }, confirmButton = {
                                TextButton(
                                    onClick = {
                                        onDelete(data)
                                        showDialog = false
                                    }) {
                                    Text(stringResource(R.string.confirm))
                                }
                            }, dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDialog = false
                                    }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }, properties = DialogProperties(dismissOnClickOutside = false)
                            )
                        }
                    }
                }
                val link = data.link
                if (!link.isNullOrEmpty()) {
                    Divider(
                        color = textColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    var currentUrl by remember { mutableStateOf<String?>(null) }
                    val activity = LocalContext.current as? Activity ?: return@Column

                    if (currentUrl != null) {
                        currentUrl?.let {
                            val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                            activity.startActivity(intent)
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
                if (data.detail.isNotEmpty()) {
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