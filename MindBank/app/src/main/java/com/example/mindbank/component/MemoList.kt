package com.example.mindbank.component

import android.app.Activity
import android.content.Intent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.mindbank.activity.AddMemoActivity
import com.example.mindbank.activity.WebViewActivity
import com.example.mindbank.data.Memo
import com.example.mindbank.data.Task
import com.example.mindbank.util.hexToColor
import com.example.mindbank.util.isDarkColor

@Composable
fun MemoItemView(data: Memo, onEdit: (Memo) -> Unit, onDelete: (Memo) -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        val backgroundColor = hexToColor(data.color)
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // 그림자 깊이
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
                // 제목과 닫기 버튼을 한 줄에 배치
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically // 세로 가운데 정렬
                ) {
                    // 제목 텍스트
                    Text(
                        text = data.title,
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f) // Text가 Row 안에서 남은 공간을 채움
                    )

                    var showDialog by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            onEdit.invoke(data)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(16.dp),
                            tint = textColor
                        )
                    }

                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.size(24.dp)
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
                                    showDialog = false // 다이얼로그 닫기
                                },
                                title = {
                                    Text(text = "삭제하시겠습니까?")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            onDelete(data) // 삭제 확인 시 onDelete 실행
                                            showDialog = false // 다이얼로그 닫기
                                        }
                                    ) {
                                        Text("확인")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showDialog = false // 취소 시 다이얼로그 닫기
                                        }
                                    ) {
                                        Text("취소")
                                    }
                                },
                                properties = DialogProperties(dismissOnClickOutside = false) // 다이얼로그 외부 클릭 방지
                            )
                        }
                    }
                }

                // 구분선 추가
                Divider(
                    color = textColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                var currentUrl by remember { mutableStateOf<String?>(null) }
                val activity = LocalContext.current as? Activity ?: return@Column

                if (currentUrl != null) {
                    currentUrl?.let {
                        val intent = Intent(activity, WebViewActivity::class.java)
                        intent.putExtra("url", it)
                        activity.startActivity(intent)
                    }
                } else {
                    // 링크 텍스트를 표시
                    HyperlinkText(
                        modifier = Modifier.fillMaxWidth(),
                        text = data.detail,
                        style = MaterialTheme.typography.bodyMedium
                    ) {
                        currentUrl = it
                    }
                }
            }
        }
    }
}