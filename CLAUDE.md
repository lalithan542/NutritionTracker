# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on connected Android device
./gradlew :composeApp:installDebug

# Run all tests
./gradlew :composeApp:allTests

# Run Android unit tests only
./gradlew :composeApp:testDebugUnitTest

# Run a single test class
./gradlew :composeApp:testDebugUnitTest --tests "com.nutrition.tracker.domain.usecase.SearchFoodsUseCaseTest"

# Lint
./gradlew :composeApp:lintDebug

# iOS framework (requires macOS + Xcode)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Key Versions

- Kotlin: 2.2.21 | KSP: 2.2.21-2.0.21
- Compose Multiplatform: 1.9.3
- Hilt: 2.56 (KSP-based, not KAPT)
- Ktor: 3.3.2 | AGP: 8.11.2

## Architecture

This is a **Kotlin Multiplatform** project with a single `:composeApp` module targeting Android and iOS.

### Layer split across source sets

```
commonMain  →  domain layer only (pure Kotlin, no DI framework)
androidMain →  data layer + presentation layer + UI (Hilt DI)
iosMain     →  iOS-specific Ktor engine only
```

**`commonMain/domain/`** is the only code shared with iOS. It contains:
- `model/` — plain data classes (`Food`, `FoodEntry`, `UserProfile`, `MealType`, etc.)
- `repository/` — interfaces only (`NutritionRepository`, `ProfileRepository`)
- `usecase/` — thin operator-fun wrappers over repository calls; each use case is a single class with one `operator fun invoke`

**`androidMain/`** implements everything else:
- `data/repository/` — `@Singleton` Hilt-injected implementations (currently in-memory with `MutableStateFlow`)
- `data/remote/GeminiNutritionService` — calls Gemini Vision API (Ktor + base64 image); parses JSON response into `ImageNutritionResult`
- `di/` — three Hilt modules: `RepositoryModule` (`@Binds`), `UseCaseModule` (`@Provides`), `NetworkModule` (singleton `HttpClient`)
- `presentation/*/` — one `@HiltViewModel` per screen; state is a plain data class exposed as `StateFlow`
- `ui/` — Compose screens consume ViewModels via `hiltViewModel()`

### Data flow pattern

```
Screen (collectAsState) ← StateFlow ← ViewModel (viewModelScope)
                                           ↓ invoke use case
                                       UseCase(repository interface)
                                           ↓
                                       RepositoryImpl (MutableStateFlow / Ktor)
```

ViewModels combine multiple flows with `combine` + `flatMapLatest`. `DashboardViewModel` uses `flatMapLatest` on `selectedDate` so the entries flow re-subscribes automatically when the date changes.

### Image Nutrition feature

`ImageNutritionScreen` → camera (`TakePicture`) or gallery (`GetContent`) → `Bitmap` → `ImageNutritionViewModel.onImageCaptured` → `GeminiNutritionService.analyzeImage` (Gemini 1.5 Flash, vision prompt) → structured `ImageNutritionResult` → user confirms → `AddFoodEntryUseCase`.

The Gemini API key is read from `BuildConfig.GEMINI_API_KEY`, which is injected at build time. Supply it via `gradle.properties` (local, git-ignored) or an environment variable:

```properties
# gradle.properties (local)
GEMINI_API_KEY=your_key_here
```

Or pass it on the command line: `./gradlew assembleDebug -PGEMINI_API_KEY=your_key_here`

### Adding a new screen

1. Add a data class `XState` and `@HiltViewModel class XViewModel` under `androidMain/presentation/x/`
2. Add a `@Composable fun XScreen` under `androidMain/ui/screens/`
3. Add a `Screen.X` object in `AppNavigation.kt` and wire into `NavHost` in `MainActivity.kt`
4. If new repository operations are needed, add to the interface in `commonMain`, implement in `androidMain/data/repository/`, and add a use case in `commonMain/usecase/`

### FileProvider

Camera photos are written to `context.cacheDir` and exposed via `androidx.core.content.FileProvider` with authority `${applicationId}.provider`. The path config is at `androidMain/res/xml/file_provider_paths.xml`.
