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
