# DelegateX

A **Kotlin-first property delegation toolkit** that makes your code cleaner, safer, and more expressive.

DelegateX brings the power of Kotlin's `ReadWriteProperty` and `provideDelegate` features to life by letting you easily build **reactive, composable, and testable property delegates**.

---

## 🚀 Why DelegateX?

Instead of manually writing boilerplate `get` / `set` logic for properties, DelegateX lets you:

- **Compose delegates** just like functional streams: `.map()`, `.distinctUntilChanged()`, `.onEach()`
- **Animate values** with `.animated()` — no more writing manual `ValueAnimator` code.
- **Store state safely** with `bySharedPreferences()` and other storage delegates.
- **Write tests with confidence** — small, composable delegates are easy to mock.

---

## 📦 Installation

```kotlin
dependencies {
    implementation("com.github.yongjhih:delegatex:<latest-version>")
}
```

*(Replace `<latest-version>` with the latest published version on Maven Central or Jitpack.)*

---

## ✨ Quick Example

### Before DelegateX

```kotlin
val prefs = context.getSharedPreferences("user", MODE_PRIVATE)
var userToken: String?
    get() = prefs.getString("user_token", null)
    set(value) { prefs.edit().putString("user_token", value).apply() }
```

### After DelegateX

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

DelegateX ships with comprehensive unit tests covering core delegation logic and SharedPreferences behaviors.
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

DelegateX helps you **think in terms of property semantics** instead of boilerplate — making your Kotlin code more declarative, maintainable, and fun.
