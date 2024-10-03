package com.example.mindbank.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            SettingsItem(title = "Latest Version", onClick = { /* Handle click */ })
        }
        item {
            SettingsItem(title = "Alarm", onClick = { /* Handle click */ })
        }
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    var expand by remember {
        mutableStateOf(false)
    }
    val height by animateDpAsState(if (expand) 48.dp else 12.dp, label = "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { expand = !expand })
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
            modifier = Modifier.clickable { onClick() },
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to $title",
            tint = Color.Gray
        )
    }
    Spacer(modifier = Modifier.height(height))
    Divider(color = Color.LightGray, thickness = 1.dp)
}