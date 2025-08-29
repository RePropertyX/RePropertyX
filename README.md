# 🌀 delegates-ktx


**Composable, decorator-style Kotlin property delegates**. Build safer, cleaner, and more expressive data models with chainable operators like `or`, `map`, `validate`, `log`, `once`, and more.

---

## 🚀 Motivation

Kotlin's `ReadWriteProperty` is powerful, but often repetitive:

- Adding null fallbacks
- Type conversion / formatting
- Validation rules
- Logging or observing changes
- Caching or security

`delegates-ktx` turns property delegates into a **pipeline**, separating concerns and allowing **composable, reusable operators**.

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

2. Type mapping / formatting

```kt
fun <P, A, B> ReadWriteProperty<P, A>.map(to: (A) -> B, from: (B) -> A): ReadWriteProperty<P, B>
```

Transforms the value on get/set.

```kt
var age: Int by stringDelegate("0")
    .map(to = { it.toInt() }, from = { it.toString() })
```

3. Validation

```kt
fun <P, R> ReadWriteProperty<P, R>.validate(validator: (R) -> Unit): ReadWriteProperty<P, R>
```


Validates values on read/write.

```kt
var email: String by stringDelegate("")
    .validate { require(it.contains("@")) }
```

4. Logging / Observing

```kt
fun <P, R> ReadWriteProperty<P, R>.log(listener: (old: R, new: R) -> Unit)
fun <P, R> ReadWriteProperty<P, R>.observable(listener: (old: R, new: R) -> Unit)

var counter: Int by intDelegate(0)
    .observable { old, new -> println("Counter: $old → $new") }
```

5. Lifecycle / state control

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

Always non-null

Trimmed automatically

Validation on length

Logs changes

Can only be set once

💡 Design Principles

Decorator pattern: each operator wraps a base delegate

Composable: chain operators freely

Reusable: works with any ReadWriteProperty, not limited to preferences or databases

Extensible: add your own operators for logging, caching, encryption, or custom validation

🔗 Getting Started

You can implement a simple delegate:

```kt
fun stringDelegate(default: String? = null) = object : ReadWriteProperty<Any?, String?> {
    private var value: String? = default
    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) { this.value = value }
}
```

Then apply the operators to make it powerful and expressive.
