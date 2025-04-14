package com.example.mindbank.presentation.navigation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mindbank.R
import com.example.mindbank.data.Memo
import com.example.mindbank.viewmodel.MemoViewModel
import kotlinx.coroutines.launch

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

@ExperimentalMaterial3Api
@Composable
fun MainGrid(memoViewModel: MemoViewModel, searchText: String, onEdit: (Memo) -> Unit) {
    val itemList by memoViewModel.memos.collectAsState()
    val filteredList = if (searchText.isNotEmpty()) itemList.filter {
        it.title.contains(searchText, ignoreCase = true) || it.detail.contains(
            searchText,
            ignoreCase = true
        )
    } else itemList

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var selectedMemo by remember { mutableStateOf<Memo?>(null) }

    selectedMemo?.let { memo ->
        DataSheet(memo, sheetState) {
            selectedMemo = null
            coroutineScope.launch { sheetState.hide() }
        }
    }

    if (filteredList.isNotEmpty()) {
        LazyVerticalStaggeredGrid (
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { item ->
                MemoItemView(item, onClick = {
                    selectedMemo = item
                    coroutineScope.launch { sheetState.show() }
                }, onEdit = {
                    onEdit.invoke(it)
                }, onDelete = {
                    memoViewModel.deleteData(item)
                })
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
        ) {
            Text(text = stringResource(R.string.empty_memo), textAlign = TextAlign.Center)
        }
    }
}