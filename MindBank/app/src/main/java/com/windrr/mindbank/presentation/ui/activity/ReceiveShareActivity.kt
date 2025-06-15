package com.windrr.mindbank.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.presentation.ui.activity.ui.theme.MindBankTheme
import com.windrr.mindbank.util.toHex
import com.windrr.mindbank.viewmodel.MemoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class ReceiveShareActivity : ComponentActivity() {

    private val memoViewModel: MemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedUrl = if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else null
        setContent {
            MindBankTheme {
                val context = LocalContext.current
                var isDone by remember { mutableStateOf(false) }

                if (!sharedUrl.isNullOrEmpty()) {
                    LaunchedEffect(sharedUrl) {
                        val linkMemo = Memo(
                            title = "외부 링크",
                            detail = "",
                            dtCreated = System.currentTimeMillis(),
                            dtUpdated = System.currentTimeMillis(),
                            color = Color.White.toHex(),
                            link = sharedUrl
                        )
                        memoViewModel.insertData(linkMemo)
                        isDone = true
                    }

                    if (isDone) {
                        LaunchedEffect(Unit) {
                            Toast.makeText(context, "링크가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                            delay(500L)
                            finish()
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LaunchedEffect(Unit) {
                        delay(500L)
                        finish()
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("공유된 URL이 없습니다.")
                    }
                }
            }
        }
    }
}