# CLAUDE.md — TalksApp (Android)

## Session Startup

**Перед началом любой работы** запусти индексацию Serena:

```bash
uvx --from git+https://github.com/oraios/serena serena project index --project "C:\Prog\My_py_project\TalksApp"
```

---

## Project Overview

Native Android приложение для QA-инженера Григория Звягинцева.
Мобильная версия сайта [gzvyagintsev.dev](https://gzvyagintsev.dev).

**Цели проекта:**
1. Мобильное портфолио — доклады, контакты, форма обратной связи
2. Учебная мишень для Appium-тестов на Python
3. Основа для доклада «Мобильное тестирование с Appium»

Подробные правила: [AI/AI_RULES.md](AI/AI_RULES.md)
Контекст проекта: [AI/context.md](AI/context.md)
Дизайн-система: [AI/design.md](AI/design.md)
Архитектура: [AI/architecture.md](AI/architecture.md)
Баги: [AI/bugs.md](AI/bugs.md)

---

## Tech Stack

- **Kotlin** + **Jetpack Compose** — UI
- **Navigation Compose** — навигация между экранами
- **ViewModel + StateFlow** — state management
- **Gson / kotlinx.serialization** — парсинг JSON
- **Coil** — загрузка изображений
- **Material 3** — дизайн-система
- **JUnit 4 + Espresso** — unit/UI тесты (встроенные)
- **Appium + Python** — E2E тесты (внешние, отдельный репо/папка)

---

## MCP Servers (active in this project)

| Server | Purpose |
|---|---|
| **serena** | Semantic code intelligence — навигация по Kotlin/Compose коду |
| **context7** | Live docs — Jetpack Compose, Android SDK, Material 3 |
| **filesystem** | Прямой доступ к файлам проекта |
| **github** | PR, issues, коммиты |

### Как использовать
- `use context7` — когда нужны актуальные доки Compose/Android
- **serena** — для поиска символов, рефакторинга, навигации

---

## Active Skills

### `/frontend-design`
Для UI экранов на Jetpack Compose. Использовать при создании новых экранов.

### `/webapp-testing`
Адаптировать под написание Appium Python тестов.

---

## Commands

```bash
# Сборка
./gradlew assembleDebug

# Запуск тестов (unit)
./gradlew test

# Запуск Espresso тестов (нужен эмулятор)
./gradlew connectedAndroidTest

# Установка APK на эмулятор
adb install app/build/outputs/apk/debug/app-debug.apk

# Appium тесты (из папки tests/)
cd tests && python -m pytest
```

---

## Code Rules (summary)

- Следовать структуре и конвенциям проекта
- Компоненты Compose — маленькие и сфокусированные
- Все интерактивные элементы **обязательно** иметь `contentDescription` (для Appium)
- Никаких новых зависимостей без явного согласования
- Сообщения коммитов — **только на русском**
- `./gradlew lint` после существенных изменений
