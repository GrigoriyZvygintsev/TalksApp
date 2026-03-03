# Design System — TalksApp

## Принцип

Дизайн повторяет сайт gzvyagintsev.dev — glassmorphism + тёплые тона.
Адаптирован под Material 3 (Material You) для Android.

---

## Цветовая палитра

| Роль | HEX | Использование |
|---|---|---|
| Background | `#F4EFEB` | Основной фон (тёплый кремовый) |
| Accent / Primary | `#FFCE32` | Кнопки, теги, акценты (не текст!) |
| Surface | `#FFFFFF` с opacity 0.7 | Карточки (glass-эффект) |
| On Background | `#1A1A1A` | Основной текст |
| On Surface | `#333333` | Текст на карточках |
| Secondary Text | `#888888` | Мета-инфо (дата, уровень) |
| Error | `#E53935` | Ошибки формы |
| Tag Background | `#FFCE32` с opacity 0.2 | Фон тегов |

**Важно:** `#FFCE32` никогда не используется как цвет текста на светлом фоне.

---

## Типографика

| Стиль | Размер | Вес | Использование |
|---|---|---|---|
| DisplayLarge | 32sp | Bold | Имя на Home экране |
| HeadlineMedium | 24sp | SemiBold | Заголовок доклада |
| TitleMedium | 18sp | Medium | Карточки в списке |
| BodyLarge | 16sp | Regular | Основной текст |
| BodyMedium | 14sp | Regular | Описания, outline |
| LabelSmall | 12sp | Medium | Теги, мета-инфо |

Шрифт: системный (Roboto на Android)

---

## Компоненты

### TalkCard (карточка доклада)
```
┌─────────────────────────────────┐
│ [Уровень: Junior]    [60 мин]   │  ← теги
│                                 │
│ Python — фундамент для          │  ← title (TitleMedium)
│ автоматизатора                  │
│                                 │
│ Полный путь от основ Python...  │  ← summary (BodyMedium)
│                                 │
│ [Python] [OOP] [Decorators]    │  ← tags
│                      13 фев 26  │  ← date
└─────────────────────────────────┘
```
- Фон: белый с opacity 0.8, blur
- Радиус: 16dp
- Тень: elevation 2dp
- testTag: `card_talk_<slug>`

### LevelBadge (бейдж уровня)
- Junior → зелёный фон
- Middle → жёлтый фон (`#FFCE32`)
- Senior → красный фон
- testTag: `badge_level_<slug>`

### TagChip (тег)
- Фон: `#FFCE32` с opacity 0.15
- Текст: `#1A1A1A`, LabelSmall
- Радиус: 8dp

### FeedbackForm (форма обратной связи)
```
┌─────────────────────────────────┐
│  Имя                            │
│  ┌───────────────────────────┐  │
│  │                           │  │
│  └───────────────────────────┘  │
│                                 │
│  Сообщение                      │
│  ┌───────────────────────────┐  │
│  │                           │  │
│  │                           │  │
│  └───────────────────────────┘  │
│                                 │
│       [  Отправить  ]           │
└─────────────────────────────────┘
```
- testTag: `input_name`, `input_message`, `btn_submit`

---

## Навигационная панель (BottomNavigation)

Иконки снизу:
- 🏠 Home — `nav_home`
- 📋 Доклады — `nav_talks`
- 😄 Мемы — `nav_memes`
- ✉️ Контакт — `nav_contact`

---

## Анимации

- Переходы между экранами: `fadeIn` / `fadeOut` (150ms)
- Появление карточек в списке: staggered (задержка 50ms между карточками)
- Нажатие кнопки: `scale` 0.95 при нажатии
- Никаких тяжёлых анимаций — приложение должно быть отзывчивым для тестов
