package com.windrr.mindbank.state

//Intent : 사용자의 액션이나 이벤트를 정의
sealed class AdviceIntent {
    object  FetchAdvice : AdviceIntent() // API 요청을 보내는 Intent
}