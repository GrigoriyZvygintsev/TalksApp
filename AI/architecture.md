# Architecture — TalksApp

## Паттерн: MVVM

```
UI Layer (Compose Screens)
    ↕ observe StateFlow
ViewModel Layer
    ↕ call
Repository Layer
    ↕ read
Data Layer (JSON in assets/)
```

---

## Структура файлов

```
app/src/main/
├── java/com/gzvyagintsev/talks/
│   ├── MainActivity.kt              ← точка входа, NavHost
│   ├── navigation/
│   │   └── AppNavigation.kt         ← граф навигации
│   ├── data/
│   │   ├── model/
│   │   │   ├── Talk.kt              ← data class
│   │   │   └── Meme.kt              ← data class
│   │   └── repository/
│   │       ├── TalksRepository.kt   ← читает talks.json из assets
│   │       └── MemesRepository.kt
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── Color.kt             ← цветовая палитра
│   │   │   ├── Type.kt              ← типографика
│   │   │   └── Theme.kt             ← MaterialTheme
│   │   ├── components/
│   │   │   ├── TalkCard.kt          ← переиспользуемая карточка
│   │   │   ├── LevelBadge.kt        ← бейдж уровня
│   │   │   └── TagChip.kt           ← чип тега
│   │   └── screens/
│   │       ├── splash/
│   │       │   └── SplashScreen.kt
│   │       ├── home/
│   │       │   ├── HomeScreen.kt
│   │       │   └── HomeViewModel.kt
│   │       ├── talks/
│   │       │   ├── TalksListScreen.kt
│   │       │   ├── TalksListViewModel.kt
│   │       │   ├── TalkDetailScreen.kt
│   │       │   └── TalkDetailViewModel.kt
│   │       ├── memes/
│   │       │   ├── MemesScreen.kt
│   │       │   └── MemesViewModel.kt
│   │       └── feedback/
│   │           ├── FeedbackScreen.kt
│   │           └── FeedbackViewModel.kt
└── assets/
    ├── talks.json                   ← данные докладов
    └── memes.json                   ← данные мемов
```

---

## Навигация

```kotlin
// Маршруты
sealed class Screen(val route: String) {
    object Splash     : Screen("splash")
    object Home       : Screen("home")
    object TalksList  : Screen("talks")
    object TalkDetail : Screen("talk/{slug}")
    object Memes      : Screen("memes")
    object Feedback   : Screen("feedback")
}
```

**Поток навигации:**
```
Splash (2s)
    ↓
Home
    ├─→ TalksList → TalkDetail
    ├─→ Memes
    └─→ Feedback
```

Bottom Navigation: Home / Talks / Memes / Feedback

---

## Data Models

```kotlin
// Talk.kt
data class Talk(
    val slug: String,
    val title: String,
    val date: String,
    val duration: String,
    val level: String,         // "Junior" | "Middle" | "Senior"
    val formats: List<String>,
    val tags: List<String>,
    val summary: String,
    val description: TalkDescription,
    val outline: List<String>
)

data class TalkDescription(
    val audience: String,
    val topics: String,
    val takeaway: String
)
```

---

## State Management

```kotlin
// TalksListViewModel.kt
data class TalksUiState(
    val talks: List<Talk> = emptyList(),
    val isLoading: Boolean = true,
    val selectedLevel: String? = null   // фильтр
)

class TalksListViewModel(
    private val repository: TalksRepository
) : ViewModel() {
    val uiState: StateFlow<TalksUiState> = ...
}
```

---

## Appium Integration Notes

Для успешного тестирования через Appium:

1. **appPackage:** `com.gzvyagintsev.talks`
2. **appActivity:** `com.gzvyagintsev.talks.MainActivity`
3. **APK path:** `app/build/outputs/apk/debug/app-debug.apk`

Каждый экран имеет корневой контейнер с `testTag("screen_<name>")` — по нему Appium определяет текущий экран.

Все testTag задокументированы в [design.md](design.md).

---

## Зависимости (build.gradle.kts)

```kotlin
// Обязательные
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.x")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.x")
implementation("com.google.code.gson:gson:2.10.x")
implementation("io.coil-kt:coil-compose:2.x")

// Тесты
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.x")
```
