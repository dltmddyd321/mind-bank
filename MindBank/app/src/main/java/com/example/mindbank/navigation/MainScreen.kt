package com.example.mindbank.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mindbank.component.MemoItemView
import com.example.mindbank.data.Memo
import com.example.mindbank.viewmodel.DataViewModel

@Composable
fun MainTopBar(title: String = "Memos") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .weight(1f)
                .padding(6.dp)
        )
    }
}

@Composable
fun MainGrid(dataViewModel: DataViewModel, searchText: String, onEdit: (Memo) -> Unit) {
    val itemList by dataViewModel.memos.collectAsState()

    val filteredList = if (searchText.isNotEmpty()) itemList.filter {
        it.title.contains(searchText, ignoreCase = true) || it.detail.contains(
            searchText,
            ignoreCase = true
        )
    } else itemList

    if (filteredList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { item ->
                MemoItemView(item, onEdit = {
                    onEdit.invoke(it)
                }, onDelete = {
                    dataViewModel.deleteData(item)
                })
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "메모가 없습니다.", textAlign = TextAlign.Center)
        }
    }
}