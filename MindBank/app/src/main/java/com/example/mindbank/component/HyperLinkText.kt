package com.example.mindbank.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun HyperlinkText(
    modifier: Modifier,
    text: String,
    style: TextStyle,
    onLinkClicked: (String) -> Unit
) {
    val linkRegex = "(https?://[\\w-]+(\\.[\\w-]+)+(:\\d+)?(/\\S*)?)".toRegex()

    // AnnotatedString을 통해 링크 부분에 annotation 추가
    val annotatedString = remember(text) {
        buildAnnotatedString {
            var lastIndex = 0
            linkRegex.findAll(text).forEach { matchResult ->
                val startIndex = matchResult.range.first
                val endIndex = matchResult.range.last + 1
                val linkText = matchResult.value

                // 링크가 아닌 일반 텍스트 추가
                append(text.substring(lastIndex, startIndex))

                // 링크 텍스트 추가
                pushStringAnnotation(
                    tag = "URL", // 사용할 태그 이름
                    annotation = linkText // 링크 텍스트를 annotation으로 추가
                )
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(linkText)
                }
                pop()

                lastIndex = endIndex
            }
            // 마지막 남은 텍스트 추가
            append(text.substring(lastIndex, text.length))
        }
    }

    // ClickableText를 이용해 annotation을 감지하고 링크 클릭 처리
    ClickableText(
        modifier = modifier,
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onLinkClicked(annotation.item) // 링크 클릭 시 실행할 동작
                }
        },
        style = style
    )
}