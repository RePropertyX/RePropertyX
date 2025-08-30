# 🌀 delegate-ktx


**Composable, decorator-style Kotlin property delegates**. Build safer, cleaner, and more expressive data models with chainable operators like `or`, `map`, `validate`, `log`, `once`, and more.

---


## 🚀 Motivation

Kotlin's `ReadWriteProperty` is powerful, but often repetitive:

- Adding null fallbacks
- Type conversion / formatting
- Validation rules
- Logging or observing changes
- Caching or security

`delegate-ktx` turns property delegates into a **pipeline**, separating concerns and allowing **composable, reusable operators**.

---

## ⚡ Core Concept

- `ReadWriteProperty` = base behavior
- Operator extensions = decorators
- Each operator wraps a delegate, providing additional logic without modifying the original

---

## 🔧 Core API

### 1. Null fallback

```kotlin
fun <P, R> ReadWriteProperty<P, R?>.or(orElse: P.(String) -> R): ReadWriteProperty<P, R>
```

Returns a non-null property by providing a fallback when the original delegate returns null.

```kt
var username: String by nullableDelegate()
    .or { "guest_$it" }
```

### 2. Null assertion

```kotlin
fun <P, R> ReadWriteProperty<P, R?>.notNull(message: String? = null): ReadWriteProperty<P, R>
```

Ensures a property is never null by throwing an exception if a null value is attempted.

```kt
var requiredField: String by nullableDelegate()
    .notNull("Required field cannot be null")
```

3. Type mapping / formatting

```kt
fun <P, A, B> ReadWriteProperty<P, A>.map(to: (A) -> B, from: (B) -> A): ReadWriteProperty<P, B>
```

Transforms the value on get/set.

```kt
var age: Int by stringDelegate("0")
    .map(to = { it.toInt() }, from = { it.toString() })
```

4. Validation

```kt
fun <P, R> ReadWriteProperty<P, R>.validate(validator: (R) -> Unit): ReadWriteProperty<P, R>
```


Validates values on read/write.

```kt
var email: String by stringDelegate("")
    .validate { require(it.contains("@")) }
```

5. Logging / Observing

```kt
fun <P, R> ReadWriteProperty<P, R>.log(listener: (old: R, new: R) -> Unit)
fun <P, R> ReadWriteProperty<P, R>.observable(listener: (old: R, new: R) -> Unit)

var counter: Int by intDelegate(0)
    .observable { old, new -> println("Counter: $old → $new") }
```

6. Lifecycle / state control

```kt
fun <P, R> ReadWriteProperty<P, R>.once(): ReadWriteProperty<P, R>
fun <P, R> ReadWriteProperty<P, R>.catch(handler: (Throwable, KProperty<*>) -> R): ReadWriteProperty<P, R>
```

7. Caching / Security

```kt
fun <P, R> ReadWriteProperty<P, R>.cacheIn(cache: MutableMap<String, Any>)
fun <P, R> ReadWriteProperty<P, R>.encrypt(encryptor: (R) -> String)
fun <P> ReadWriteProperty<P, String>.decrypt(decryptor: (String) -> R)
```

🌟 Example

```kt
var username: String by stringDelegate(null)
    .or { "guest_$it" }
    .map(to = { it.trim() }, from = { it })
    .validate { require(it.length < 20) }
    .log { old, new -> println("username: $old → $new") }
    .once()
```

Effects:

1. Always non-null
2. Trimmed automatically
3. Validation on length
4. Logs changes
5. Can only be set once


```mermaid
flowchart LR
    A[Base ReadWriteProperty] --> B["or (fallback)"]
    B --> C["map (to/from)"]
    C --> D["validate (rule)"]
    D --> E["log (old, new)"]
    E --> F["once()"]
    F --> G[Final Property]

    style A fill:#2e3440,stroke:#d8dee9,stroke-width:1px,color:#d8dee9
    style B fill:#3b4252,stroke:#d8dee9,stroke-width:1px,color:#eceff4
    style C fill:#434c5e,stroke:#d8dee9,stroke-width:1px,color:#eceff4
    style D fill:#4c566a,stroke:#d8dee9,stroke-width:1px,color:#eceff4
    style E fill:#3b4252,stroke:#d8dee9,stroke-width:1px,color:#eceff4
    style F fill:#434c5e,stroke:#d8dee9,stroke-width:1px,color:#eceff4
    style G fill:#2e3440,stroke:#88c0d0,stroke-width:2px,color:#88c0d0
```

And more:

```kt
var username: String by stringDelegate(null)      // Base ReadWriteProperty
    .or { "guest_$it" }                           // fallback if null
    .map(to = { it.trim() }, from = { it })       // type mapping / formatting
    .validate { require(it.length < 20) }        // validation
    .log { old, new -> println("username: $old → $new") } // logging
    .once()                                       // can only set once
    .catch { _, _ -> "default_user" }            // exception handler fallback
    .cacheIn(mutableMapOf())                     // cache in memory
    .encrypt({ plain -> encryptor(plain) })      // encrypt value
    .decrypt({ encrypted -> decryptor(encrypted) }) // decrypt on read

var requiredField: String by stringDelegate(null) // Base ReadWriteProperty
    .notNull("Required field cannot be null")     // throws exception if null
```

💡 Design Principles

- Decorator pattern: each operator wraps a base delegate
- Composable: chain operators freely
- Reusable: works with any ReadWriteProperty, not limited to preferences or databases
- Extensible: add your own operators for logging, caching, encryption, or custom validation

🔗 Getting Started

## Installation

### Core Module (JVM)

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.yongjhih:delegatex:0.1.0-SNAPSHOT")
}
```

### Android Module

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.yongjhih:delegatex-android:0.1.0-SNAPSHOT")
}
```

## Basic Usage

### 1. Import the library

```kotlin
import com.github.yongjhih.delegatex.*
```

### 2. Use built-in delegates

```kotlin
class UserSettings {
    var username: String by stringDelegate(null)
        .or { "guest_$it" }
    
    var age: Int by stringDelegate("0")
        .map(to = { it.toInt() }, from = { it.toString() })
    
    var email: String by stringDelegate("")
        .validate { require(it.contains("@")) }
}
```

### 3. Android SharedPreferences

```kotlin
import com.github.yongjhih.delegatex.android.*

class AndroidSettings(context: Context) {
    private val prefs = context.getDefaultSharedPreferences()
    
    var username: String? by prefs.stringPreference("username")
        .or { "guest_$it" }
    
    var age: Int by prefs.intPreference("age", 0)
        .validate { require(it >= 0) }
    
    var isEnabled: Boolean by prefs.booleanPreference("is_enabled", false)
        .log { old, new -> println("Enabled: $old → $new") }
}
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yongjhih/delegate-ktx.git
cd delegate-ktx

# Build the project
./gradlew build

# Run tests
./gradlew test

# Build Android module
./gradlew :delegate-ktx-android:assembleRelease

# Build core module
./gradlew :delegate-ktx:build
```
