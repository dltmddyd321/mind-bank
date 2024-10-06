package com.example.mindbank.navigation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mindbank.R
import com.example.mindbank.activity.AddActivity
import com.example.mindbank.data.SaveData
import com.example.mindbank.db.DataViewModel
import com.example.mindbank.viewmodel.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Memos",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .weight(1f)
                .padding(6.dp)
        )
        IconButton(onClick = { /* 메뉴 아이콘 클릭 동작 */ }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu"
            )
        }
    }
}

@Composable
fun MainGrid(dataViewModel: DataViewModel) {
    val isLoading = remember { mutableStateOf(true) }
    val itemList = mutableListOf<SaveData>()

    itemList.add(
        SaveData(
            title = "테스트", detail = "메모", dtUpdated = System.currentTimeMillis(),
            dtCreated = System.currentTimeMillis(), color = "#FF0000"
        )
    )

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.IO) {
            itemList.addAll(dataViewModel.getAllData())
        }
        isLoading.value = false // 로딩 상태 업데이트
    }

    if (isLoading.value) {
        Box(
            contentAlignment = Alignment.Center, // Box 안의 내용을 중앙에 정렬
            modifier = Modifier.fillMaxSize() // Box를 화면 전체 크기로 확장
        ) {
            CircularProgressIndicator()
        }
    } else {
        if (itemList.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 한 줄에 표시할 아이템의 수
                contentPadding = PaddingValues(8.dp) // 그리드의 전체 패딩
            ) {
                items(itemList) { item ->
                    MemoItemView(item)
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "저장된 메모가 없습니다.\n새로운 메모를 추가해보세요.", textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun MemoItemView(data: SaveData) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(10.dp), // 모서리의 둥근 정도 설정
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // 그림자 깊이
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(), // Card 외부의 패딩
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth() // Column이 전체 너비를 채우도록 설정
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
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f) // Text가 Row 안에서 남은 공간을 채움
                    )

                    // 닫기 버튼
                    IconButton(
                        onClick = { /* TODO: 닫기 액션 */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray // 아이콘 색상을 조절
                        )
                    }
                }

                // 구분선 추가
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp) // 구분선 위아래에 패딩 추가
                )

                // 하단 메모 내용 표시
                Text(
                    text = data.detail, // 메모 내용이 들어갈 변수
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth() // 메모 텍스트를 가득 채움
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(dataViewModel: DataViewModel, navController: NavController) {
    val viewModel = SearchViewModel()
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val countriesList by viewModel.countriesList.collectAsState()

    Scaffold(
        topBar = {
            Column {
                MainTopBar()
                SearchBar(
                    query = searchText,
                    onQueryChange = viewModel::onSearchTextChange,
                    onSearch = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = dataViewModel.searchByKeyword(it)
                        }
                    },
                    active = isSearching,
                    onActiveChange = { viewModel.onToggleSearch() },
                    placeholder = { Text(text = "검색어를 입력하시오.") },
                    trailingIcon = { Icon(imageVector = Icons.Default.Search, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    LazyColumn {
                        items(countriesList) { country ->
                            Text(
                                text = country, modifier = Modifier.padding(
                                    start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp
                                )
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = { FloatingButton(false, navController) },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (!isSearching) MainGrid(dataViewModel)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FloatingButton(isAddMode: Boolean, navController: NavController) {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            val intent = Intent(context, AddActivity::class.java)
            context.startActivity(intent)
//            navController.navigate(Screen.Notes.route) {
//                popUpTo(navController.graph.startDestinationId) {
//                    saveState = true // 상태 유지
//                }
//                launchSingleTop = true
//                restoreState = true // 이전 상태 복원
//            }
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = if (isAddMode) Icons.Default.Check else Icons.Rounded.Add,
            contentDescription = "Add FAB",
            tint = Color.White,
        )
    }
}