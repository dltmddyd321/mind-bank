package com.example.mindbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mindbank.data.SaveData
import com.example.mindbank.ui.theme.MindBankTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindBankTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun MainGrid() {

    val itemList = listOf(
        SaveData(
            UUID.randomUUID().toString(),
            "테스트입니다!",
            "세부 설명",
            System.currentTimeMillis(),
            System.currentTimeMillis()
        ),
        SaveData(
            UUID.randomUUID().toString(),
            "두 번째 표시",
            "세부 설명",
            System.currentTimeMillis(),
            System.currentTimeMillis()
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 한 줄에 표시할 아이템의 수
        contentPadding = PaddingValues(8.dp) // 그리드의 전체 패딩
    ) {
        items(itemList) { item ->
            MemoItemView(item)
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = data.title,
                    modifier = Modifier.size(100.dp), // 이미지 크기
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = data.title,
                    color = Color.Black,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp) // 버튼 크기를 조절합니다.
                .offset(x = (-12).dp, y = (12).dp) // 아이콘을 카드의 우측 상단에서 조금 내려줍니다.
                .zIndex(1f) // 버튼을 카드 위에 놓습니다.
                .background(
                    color = Color.White.copy(alpha = 0.5f), // 반투명한 흰색 배경
                    shape = CircleShape // 원형 모양
                )
                .padding(6.dp) // IconButton 안쪽에 패딩을 주어 Icon과 배경 사이의 여백을 생성합니다.
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier.size(16.dp),
                tint = Color.Gray // 아이콘 색상을 조절합니다.
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MindBankTheme {
        MainGrid()
    }
}