# Mind Bank
링크 및 간단한 메모를 단순하고 빠르게 저장 가능한 서비스입니다.<br />
<p align="left">
  <a href="https://play.google.com/store/apps/details?id=com.windrr.jibrro">
    <img src="./screen_shot/google_play.png" alt="Download on Google Play" width="200"/>
  </a>
</p>
<br />

## Mind Bank
Stack
- Kotlin
- Room, DataStore
- Hilt, Refrofit, Compose
- MVVM, MVI

DB로 데이터 추출
Compose UI -> DataViewModel(Hilt) -> DataBase

API를 통한 데이터 수신
Compose UI.State -> ApiViewModel(Hilt) -> Intent -> ApiService

### 메모 입력 기능 
- 매번 메모 입력 화면에 진입 시, LaunchedEffect를 통해 DataStore에서 임시 저장 데이터가 있는지 검사
- ColorPicker와 TextField를 통해 색상 및 내용 지정이 가능
- 최종 저장 시, Room DB 저장 처리

### Table
![스크린샷 2024-10-13 오후 3 58 13](https://github.com/user-attachments/assets/fc144a5f-fdd7-4f06-8099-57456d7e27d7)

