# AI RULES — TalksApp Android

## Project Purpose

- Нативное Android-приложение (Kotlin + Jetpack Compose)
- Мобильное портфолио QA-инженера Григория Звягинцева
- Учебная мишень для Appium-тестов — каждый экран должен быть тестируемым
- Слоган: «Превращаю хаос в стабильные релизы»

---

## Tech Stack Rules

**Обязательный стек:**
- Kotlin (не Java)
- Jetpack Compose (не XML layouts)
- Navigation Compose — навигация
- ViewModel + StateFlow — state management
- Material 3 — дизайн-компоненты
- Gson — парсинг JSON из assets/
- Coil — загрузка картинок

**Запрещено без явного согласования:**
- Добавлять новые зависимости в build.gradle.kts
- Использовать XML layouts (только Compose)
- Использовать Java-классы там, где есть Kotlin-эквивалент
- Подключать сторонние UI-библиотеки (только Material 3 + Compose)

---

## Code Style Rules

- Следовать официальному Kotlin Style Guide
- Composable-функции: PascalCase, аннотация `@Composable`
- ViewModel: суффикс `ViewModel` (напр. `TalksViewModel`)
- Файлы экранов: суффикс `Screen` (напр. `TalksListScreen.kt`)
- Данные: data class, суффикс `Model` не нужен (напр. `Talk`, `Meme`)
- Один файл — один экран или один компонент
- Максимум 200 строк в файле — если больше, разбить на компоненты

## Appium-Friendly Rules (КРИТИЧНО)

Каждый интерактивный элемент **обязан** иметь `testTag` или `contentDescription`:

```kotlin
// ПРАВИЛЬНО
Button(
    onClick = { ... },
    modifier = Modifier.semantics { contentDescription = "talk_item_python_basics" }
) { ... }

// или через testTag
Modifier.testTag("btn_submit_feedback")
```

**Соглашение по именованию testTag:**
- `screen_<name>` — корневой контейнер экрана (напр. `screen_talks_list`)
- `card_talk_<slug>` — карточка доклада
- `btn_<action>` — кнопки (напр. `btn_submit`, `btn_back`)
- `input_<field>` — поля ввода (напр. `input_name`, `input_message`)
- `text_<role>` — текстовые элементы (напр. `text_talk_title`)
- `list_talks` — список докладов
- `img_avatar` — изображение аватара

---

## Content Rules

- Данные докладов — из `app/src/main/assets/talks.json` (копия с сайта)
- Данные мемов — из `app/src/main/assets/memes.json`
- JSON-файлы не генерируются — копируются вручную с сайта
- Никаких сетевых запросов в MVP (всё локально)

---

## Commit Rules

- Сообщения коммитов — **только на русском языке**
- Формат: `тип(scope): описание`
- Типы: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`
- Примеры:
  - `feat(talks): добавлен экран списка докладов`
  - `fix(navigation): исправлен переход на детальный экран`
  - `test(appium): добавлены тесты навигации`

---

## What AI Must NOT Do

- Не менять `build.gradle.kts` без явного запроса
- Не добавлять новые зависимости самостоятельно
- Не убирать `testTag` / `contentDescription` с элементов
- Не использовать `TODO()` в продакшн-коде — реализовать или обсудить
- Не создавать XML layout файлы
