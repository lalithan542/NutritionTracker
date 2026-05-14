# NutritionTracker

A **Kotlin Multiplatform** nutrition tracking app for Android (iOS scaffold included) built with Clean Architecture, Jetpack Compose, Hilt, and Gemini Vision AI.

> **Generated with [Claude Code](https://claude.ai/code)** — Anthropic's AI coding assistant.

---

## Features

- **Dashboard** — daily macro summary (calories, protein, carbs, fat) with per-meal breakdown and date navigation
- **Food Search** — search from a built-in food database and log servings with custom amounts
- **AI Food Scanner** — photograph or pick a meal from the gallery; Gemini Vision identifies the food and estimates nutrition automatically
- **User Profile** — enter age, weight, height, gender, and activity level to get auto-calculated BMI, TDEE (Mifflin-St Jeor), and macro goals
- **Meal Logging** — log food under Breakfast, Lunch, Dinner, or Snacks with adjustable serving sizes

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2.21 |
| UI | Compose Multiplatform 1.9.3 (Material 3) |
| DI | Hilt 2.56 (KSP) |
| Networking | Ktor 3.3.2 |
| AI / Vision | Gemini Vision API (`gemini-3.1-flash-lite`) |
| Async | Kotlin Coroutines + StateFlow |
| Build | AGP 8.11.2, KSP 2.2.21-2.0.5 |

---

## Architecture

Clean Architecture split across KMP source sets:

```
commonMain  →  domain layer (models, repository interfaces, use cases)
androidMain →  data layer + presentation (ViewModels) + Compose UI
iosMain     →  iOS Ktor engine stub
```

```
Screen (collectAsState)
    └── ViewModel (StateFlow, viewModelScope)
            └── UseCase (operator fun invoke)
                    └── Repository interface
                            └── RepositoryImpl (in-memory StateFlow / Ktor)
```

---

## Getting Started

### Prerequisites

- Android Studio Meerkat or later
- Android SDK 26+
- A [Gemini API key](https://aistudio.google.com/apikey) (free tier)

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/lalithan542/NutritionTracker.git
   cd NutritionTracker
   ```

2. **Add your Gemini API key**
   ```bash
   cp gradle.properties.template gradle.properties
   # Edit gradle.properties and replace the placeholder:
   # GEMINI_API_KEY=your_actual_key_here
   ```
   `gradle.properties` is gitignored and will never be committed.

3. **Build & install**
   ```bash
   ./gradlew :composeApp:installDebug
   ```

   Or pass the key directly without creating `gradle.properties`:
   ```bash
   ./gradlew :composeApp:installDebug -PGEMINI_API_KEY=your_key_here
   ```

---

## Project Structure

```
composeApp/src/
├── commonMain/kotlin/com/nutrition/tracker/
│   └── domain/
│       ├── model/          # Food, FoodEntry, UserProfile, MealType, …
│       ├── repository/     # NutritionRepository, ProfileRepository (interfaces)
│       └── usecase/        # One class per use case
└── androidMain/kotlin/com/nutrition/tracker/
    ├── data/
    │   ├── remote/         # GeminiNutritionService (Ktor + Vision API)
    │   └── repository/     # Repository implementations
    ├── di/                 # Hilt modules (Network, Repository, UseCase)
    ├── presentation/       # HiltViewModels per screen
    └── ui/
        ├── navigation/     # AppNavigation (NavHost)
        ├── screens/        # Dashboard, Search, LogFood, ImageNutrition, Profile
        └── components/     # MacroProgressCard, …
```

---

## API Key Security

- The Gemini API key is injected at build time via `BuildConfig.GEMINI_API_KEY`
- `gradle.properties` (which holds the key) is listed in `.gitignore` and will never be pushed
- `gradle.properties.template` is the only committed file — it contains a placeholder, not a real key

---

## License

MIT
