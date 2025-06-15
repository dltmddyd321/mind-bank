package com.windrr.mindbank.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.windrr.mindbank.presentation.ui.component.WebViewScreen

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url") ?: run {
            finish()
            return
        }
        setContent {
            WebViewScreen(url = url, onBack = { finish() })
        }
    }
}