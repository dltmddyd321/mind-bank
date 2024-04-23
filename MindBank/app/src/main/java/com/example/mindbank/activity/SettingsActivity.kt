package com.example.mindbank.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindbank.activity.ui.theme.MindBankTheme
import androidx.compose.foundation.layout.Row as Row1

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
        item {
            SettingsItem(title = "Account and security", onClick = { /* Handle click */ })
        }
        item {
            SettingsItem(title = "Units", onClick = { /* Handle click */ })
        }
        item {
            SettingsItem(title = "Check for updates", onClick = { /* Handle click */ })
        }
        item {
            SettingsItem(title = "About", onClick = { /* Handle click */ })
        }
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Row1(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp
        )

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to $title",
            tint = Color.Gray
        )
    }
    Divider(color = Color.LightGray, thickness = 1.dp)
}