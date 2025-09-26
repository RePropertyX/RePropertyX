# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RePropertyX is a Kotlin-first property delegation toolkit with three main modules:
- **repropertyx**: Core JVM module with composable property delegates
- **repropertyx-android**: Android-specific extensions (SharedPreferences, View properties)
- **repropertyx-android-app**: Sample Android application

## Development Commands

### Build & Test
```bash
./gradlew build          # Build all modules
./gradlew test           # Run all tests
./gradlew :repropertyx:test                    # Test core module only
./gradlew :repropertyx-android:test            # Test Android module only
./gradlew :repropertyx-android:connectedTest   # Run Android instrumentation tests
```

### Documentation
```bash
./gradlew dokkaHtml      # Generate API documentation
```

### Publishing
```bash
./gradlew publishToMavenLocal   # Publish to local Maven repository
```

## Architecture & Key Concepts

### Core Design Pattern
The library uses the **decorator pattern** where property delegates can be chained using extension functions:

```kotlin
var property: String by baseDelegate()
    .orElse { "default" }
    .map({ it.uppercase() }, { it.lowercase() })
    .onEach { println("Changed to: $it") }
```

### Module Structure
- **repropertyx/src/main/kotlin/com/github/repropertyx/**:
  - `PropertyX.kt`: Core extension functions (`.orElse()`, `.map()`, `.onEach()`, etc.)
  - `CorePropertyX.kt`: Basic property delegate implementations
  - `ReflectPropertyX.kt`: Reflection-based property access via `T.by(Property)`
  - `AtomicPropertyX.kt`: Thread-safe atomic property delegates

- **repropertyx-android/src/main/kotlin/com/github/repropertyx/android/**:
  - `SharedPreferencesPropertyX.kt`: SharedPreferences integration
  - `ViewPropertyX.kt`: Android View property delegates with animation support

### Key Extension Functions
- `.orElse()` / `.notNull()`: Null handling and fallbacks
- `.map()`: Type transformation between exposed and stored types
- `.onEach()` / `.onEachBefore()`: Side effects and observation
- `.distinctUntilChanged()`: Prevents redundant updates
- `.animated()`: Animates value changes using ValueAnimator (Android only)
- `.closable()`: Auto-closes Closable resources when replaced

### Property Access Pattern
The `T.by(Property)` pattern allows accessing class properties as delegates:
```kotlin
class User {
    var name: String = ""
}
val user = User()
val nameProperty = user.by(User::name)  // Creates a property delegate
```

## Testing Structure
- **Core tests**: `repropertyx/src/test/kotlin/` - JUnit 5 with Kotlin test
- **Android tests**: Unit tests use JUnit 4, instrumentation tests use AndroidX Test
- Use `useJUnitPlatform()` for core module tests
- Android module uses different test frameworks to avoid capability conflicts

## Build Configuration Notes
- Kotlin 1.9.20 with JVM target 1.8
- Android minSdk 21, targetSdk 34
- Uses Dokka for documentation generation
- Maven publishing configured for both modules
- Android module includes Mockito for testing