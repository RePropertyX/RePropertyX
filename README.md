<p align="center">
  <img src="art/logo.svg" alt="RePropertyX Logo" width="200"/>
</p>

# RePropertyX

<p align="left">
  <a href="https://jitpack.io/#RePropertyX/RePropertyX"><img src="https://jitpack.io/v/RePropertyX/RePropertyX.svg" alt="JitPack"/></a>
  <a href="https://javadoc.jitpack.io/com/github/RePropertyX/RePropertyX/repropertyx/1.0.0/javadoc/"><img src="https://img.shields.io/badge/javadoc-repropertyx-brightgreen.svg" alt="Javadocs Core"/></a>
  <a href="https://javadoc.jitpack.io/com/github/RePropertyX/RePropertyX/repropertyx-android/1.0.0/javadoc/"><img src="https://img.shields.io/badge/javadoc-repropertyx--android-brightgreen.svg" alt="Javadocs Android"/></a>
  <a href="https://javadoc.jitpack.io/com/github/RePropertyX/RePropertyX/repropertyx-compose-android/1.0.0/javadoc/"><img src="https://img.shields.io/badge/javadoc-repropertyx--compose--android-brightgreen.svg" alt="Javadocs Compose"/></a>
  <a href="https://repropertyx.github.io/RePropertyX/"><img src="https://img.shields.io/badge/Website-Official%20Site-7F52FF.svg?logo=googlechrome&logoColor=white" alt="Official Website"/></a>
  <a href="https://jitci.com/gh/RePropertyX/RePropertyX"><img src="https://jitci.com/gh/RePropertyX/RePropertyX/svg" alt="JitCI"/></a>
  <a href="https://github.com/RePropertyX/RePropertyX/actions"><img src="https://github.com/RePropertyX/RePropertyX/actions/workflows/docs.yml/badge.svg" alt="Docs CI"/></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.9.20-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin"/></a>
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Android-API%2021%2B-3DDC84.svg?logo=android&logoColor=white" alt="Android API 21+"/></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-2024.06.00-4285F4.svg?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"/></a>
</p>

A **Kotlin-first property delegation toolkit** that makes your code cleaner, safer, and more expressive.

RePropertyX brings the power of Kotlin's `ReadWriteProperty` and `provideDelegate` features to life by letting you easily build **reactive, composable, and testable property delegates**.

---

## 🚀 Why RePropertyX?

Instead of manually writing boilerplate `get` / `set` logic for properties, RePropertyX lets you:

- **Compose delegates** just like functional streams: `.map()`, `.distinctUntilChanged()`, `.onEach()`
- **Animate values** with `.animated()` — no more writing manual `ValueAnimator` code.
- **Store state safely** with `bySharedPreferences()` and other storage delegates.
- **Write tests with confidence** — small, composable delegates are easy to mock.

---

## 📦 Installation

Add JitPack to your project repository list:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add dependencies to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Core JVM property delegates
    implementation("com.github.RePropertyX.RePropertyX:repropertyx:1.0.0")

    // Android extensions (SharedPreferences, View animations)
    implementation("com.github.RePropertyX.RePropertyX:repropertyx-android:1.0.0")

    // Compose extensions (MutableState & Compose bindings)
    implementation("com.github.RePropertyX.RePropertyX:repropertyx-compose-android:1.0.0")
}
```

---

## 🌐 Website & Documentation

- 🚀 **Official Website & Interactive Guides**: [repropertyx.github.io/RePropertyX](https://repropertyx.github.io/RePropertyX/)
- 📖 **Multi-Module Dokka API Reference**: [repropertyx.github.io/RePropertyX/api](https://repropertyx.github.io/RePropertyX/api)

---

## ✨ Quick Example

### Before RePropertyX

```kotlin
val prefs = context.getSharedPreferences("user", MODE_PRIVATE)
var userToken: String?
    get() = prefs.getString("user_token", null)
    set(value) { prefs.edit().putString("user_token", value).apply() }
var userNickname: String
    get() = prefs.getString("userNickname_${userToken}", null) ?: "unknown"
    set(value) { prefs.edit().putString("userNickname_${userToken}", value).apply() }
```

### After RePropertyX

```kotlin
var userToken: String? by prefs.byString()
var userNickname: String by prefs.byString { "${it}_${userToken}" }.or { "unknown" }
```

Or fully reactive:

```kotlin
var peekHeight by propertyOf(
    get = { bottomSheetBehavior.peekHeight },
    set = { bottomSheetBehavior.peekHeight = it },
)
.distinctUntilChanged()
.animated()
.onEach { println("PeekHeight changed to $it") }

peekHeight = 200 // smooth animation + event callback!
```

---

## 💡 Key APIs

| API                                   | Description                                               |
|---------------------------------------|-----------------------------------------------------------|
| `propertyOf(get, set)`                | Create a property delegate from any getter & setter pair. |
| `.map(transform)`                     | Transform values between exposed and stored types.        |
| `.distinctUntilChanged()`             | Prevent redundant updates.                                |
| `.onEach { }`                         | Observe every value change.                               |
| `.onEachBefore { }`                   | Run side effects **before** applying a value.             |
| `.animated()`                         | Animate value changes with `ValueAnimator`.               |
| `mutablePropertyOf(initialValue)`     | Create a simple in-memory mutable property delegate.      |
| `bySharedPreference`, `byString`, ... | Property delegates for Android SharedPreferences.         |
| `.closable()`                         | Automatically close a closable when assigning a new one.  |

---

## 🔧 Advanced Usage

### 1. Animated Delegation

```kotlin
var viewAnimatedY by propertyOf(
    get = { view.translationY },
    set = { view.translationY = it },
).animated()

viewAnimatedY = 200f // Smoothly animates from current value to 200f
```

### 2. SharedPreferences Composable Delegates

```kotlin
var userId: Long by prefs.byLong("user_id", defaultValue = -1L)
var loggedIn: Boolean by prefs.byBoolean("logged_in")
```

### 3. Auto-Cancelable Animator Property

```kotlin
var runningAnimator: ValueAnimator? by mutablePropertyOf<ValueAnimator?>(null)
    .onEachBefore { it?.cancel() }

runningAnimator = ValueAnimator.ofFloat(0f, 1f).apply { start() }
```

---

## ✅ Testing & Quality

RePropertyX ships with comprehensive unit tests covering core delegation logic and SharedPreferences behaviors.
This ensures your code stays predictable and maintainable.

---

## 🤝 Contributing

Contributions are welcome!

- Open issues for bugs and feature requests.
- Submit PRs with tests to keep coverage strong.

---

## 📄 License

Apache 2.0 - see [LICENSE](LICENSE) for details.

---

RePropertyX helps you **think in terms of property semantics** instead of boilerplate — making your Kotlin code more declarative, maintainable, and fun.
