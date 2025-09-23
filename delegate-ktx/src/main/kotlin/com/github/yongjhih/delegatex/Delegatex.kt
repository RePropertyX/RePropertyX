/*
 * Copyright 2024 yongjhih
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.repropertyx

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns a non-null property by providing a fallback when the original delegate returns null.
 *
 * @param orElse A function that provides a fallback value based on the property name
 * @return A ReadWriteProperty that never returns null
 */
fun <P, R> ReadWriteProperty<P, R?>.or(orElse: P.(String) -> R): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return this@or.getValue(thisRef, property) ?: thisRef.orElse(property.name)
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            this@or.setValue(thisRef, property, value)
        }
    }
}

/**
 * Returns a non-null property by providing a fallback when the original delegate returns null.
 *
 * @param orElse A function that provides a fallback value based on the property name
 * @return A ReadOnlyProperty that never returns null
 */
fun <P, R> ReadOnlyProperty<P, R?>.or(orElse: P.(String) -> R): ReadOnlyProperty<P, R> =
    ReadOnlyProperty { thisRef, property -> this@or.getValue(thisRef, property) ?: thisRef.orElse(property.name) }

/**
 * Ensures a property is never null by throwing an exception if a null value is attempted.
 *
 * @param message Optional custom error message for null value exceptions
 * @return A ReadWriteProperty that throws an exception for null values
 */
fun <P, R> ReadWriteProperty<P, R?>.notNull(message: String? = null): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return this@notNull.getValue(thisRef, property) 
                ?: throw IllegalStateException(message ?: "Property '${property.name}' cannot be null")
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            this@notNull.setValue(thisRef, property, value)
        }
    }
}

/**
 * Transforms the value on get/set using provided transformation functions.
 *
 * @param to Function to transform the stored value to the desired type
 * @param from Function to transform the desired type back to the stored type
 * @return A ReadWriteProperty with type transformation
 */
fun <P, V, R> ReadWriteProperty<P, V>.map(
    to: P.(V) -> R,
    from: V.(R) -> V
): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return thisRef.to(this@map.getValue(thisRef, property))
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            this@map.setValue(thisRef, property, this@map.getValue(thisRef, property).from(value))
        }
    }
}

/**
 * Validates values on read/write using the provided validator function.
 *
 * @param validator Function that validates the value and throws an exception if invalid
 * @return A ReadWriteProperty with validation
 */
fun <P, R> ReadWriteProperty<P, R>.validate(validator: P.(R) -> Unit): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            val value = this@validate.getValue(thisRef, property)
            thisRef.validator(value)
            return value
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            thisRef.validator(value)
            this@validate.setValue(thisRef, property, value)
        }
    }
}


/**
 * Logs property changes using the provided listener function.
 *
 * @param listener Function called when the property value changes
 * @return A ReadWriteProperty with logging
 */
fun <P, R> ReadWriteProperty<P, R>.log(listener: P.(old: R, new: R) -> Unit): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return this@log.getValue(thisRef, property)
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            val oldValue = this@log.getValue(thisRef, property)
            this@log.setValue(thisRef, property, value)
            thisRef.listener(oldValue, value)
        }
    }
}

/**
 * Alias for log() - observes property changes using the provided listener function.
 *
 * @param listener Function called when the property value changes
 * @return A ReadWriteProperty with observation
 */
fun <P, R> ReadWriteProperty<P, R>.observable(listener: P.(old: R, new: R) -> Unit): ReadWriteProperty<P, R> =
    log(listener)

class ObservedProperty<P, R>(private val prop: ReadWriteProperty<P, R>) : ReadWriteProperty<P, R> {
    val observers = mutableSetOf<(old: R, new: R) -> Unit>()

    override fun getValue(thisRef: P, property: KProperty<*>): R =
        prop.getValue(thisRef, property)

    override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
        val oldValue = prop.getValue(thisRef, property)
        prop.setValue(thisRef, property, value)
        observers.forEach { it(oldValue, value) }
    }
}

fun <P, R> ReadWriteProperty<P, R>.observed(): ObservedProperty<P, R> =
    ObservedProperty(this)


/**
 * Ensures the property can only be set once. Subsequent set operations are ignored.
 *
 * @return A ReadWriteProperty that can only be set once
 */
fun <P, R> ReadWriteProperty<P, R>.once(): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        private var hasBeenSet = false

        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return this@once.getValue(thisRef, property)
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            if (!hasBeenSet) {
                this@once.setValue(thisRef, property, value)
                hasBeenSet = true
            }
        }
    }
}

/**
 * Provides exception handling for property operations.
 *
 * @param handler Function that handles exceptions and returns a fallback value
 * @return A ReadWriteProperty with exception handling
 */
fun <P, R> ReadWriteProperty<P, R>.catch(handler: (Throwable, KProperty<*>) -> R): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return try {
                this@catch.getValue(thisRef, property)
            } catch (e: Throwable) {
                handler(e, property)
            }
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            try {
                this@catch.setValue(thisRef, property, value)
            } catch (e: Throwable) {
                handler(e, property)
            }
        }
    }
}

/**
 * Caches property values in the provided cache map.
 *
 * @param cache Mutable map to store cached values
 * @return A ReadWriteProperty with caching
 */
fun <P, R> ReadWriteProperty<P, R>.cacheIn(cache: MutableMap<String, Any>): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            val key = "${thisRef!!::class.simpleName}.${property.name}"
            return if (cache.containsKey(key)) {
                @Suppress("UNCHECKED_CAST")
                cache[key] as R
            } else {
                val value = this@cacheIn.getValue(thisRef, property)
                cache[key] = value as Any
                value
            }
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            val key = "${thisRef!!::class.simpleName}.${property.name}"
            cache[key] = value as Any
            this@cacheIn.setValue(thisRef, property, value)
        }
    }
}

/**
 * Encrypts property values using the provided encryptor function.
 *
 * @param encryptor Function that encrypts the value to a string
 * @return A ReadWriteProperty with encryption
 */
fun <P, R> ReadWriteProperty<P, R>.encrypt(encryptor: (R) -> String): ReadWriteProperty<P, String> {
    return object : ReadWriteProperty<P, String> {
        override fun getValue(thisRef: P, property: KProperty<*>): String {
            val value = this@encrypt.getValue(thisRef, property)
            return encryptor(value)
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: String) {
            // Note: This is a simplified implementation. In practice, you'd need to handle decryption
            // This is just a placeholder for the concept
            throw UnsupportedOperationException("Encrypted properties cannot be set directly")
        }
    }
}

/**
 * Decrypts string property values using the provided decryptor function.
 *
 * @param decryptor Function that decrypts the string to the desired type
 * @return A ReadWriteProperty with decryption
 */
fun <P, R> ReadWriteProperty<P, String>.decrypt(decryptor: (String) -> R): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            val encryptedValue = this@decrypt.getValue(thisRef, property)
            return decryptor(encryptedValue)
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            // Note: This is a simplified implementation. In practice, you'd need to handle encryption
            // This is just a placeholder for the concept
            throw UnsupportedOperationException("Decrypted properties cannot be set directly")
        }
    }
}

fun <P, R: Any> ReadWriteProperty<P, R>.setIf(test: (R) -> Boolean): ReadWriteProperty<P, R> {
    return object : ReadWriteProperty<P, R> {
        override fun getValue(thisRef: P, property: KProperty<*>): R {
            return this@setIf.getValue(thisRef, property)
        }

        override fun setValue(thisRef: P, property: KProperty<*>, value: R) {
            if (test(value)) {
                this@setIf.setValue(thisRef, property, value)
            }
        }
    }
}

fun <P, R> ReadWriteProperty<P, R>.takeIf(test: (R) -> Boolean): ReadWriteProperty<P, R?> {
    return object : ReadWriteProperty<P, R?> {
        override fun getValue(thisRef: P, property: KProperty<*>): R? =
            this@takeIf.getValue(thisRef, property).takeIf(test)

        override fun setValue(thisRef: P, property: KProperty<*>, value: R?) {
            if (value != null && test(value)) {
                this@takeIf.setValue(thisRef, property, value)
            }
        }
    }
}

fun <P, R> ReadOnlyProperty<P, R>.takeIf(test: (R) -> Boolean): ReadOnlyProperty<P, R?> =
    ReadOnlyProperty { thisRef, property -> this@takeIf.getValue(thisRef, property).takeIf(test) }

fun <P, T> ReadWriteProperty<P, String?>.serialized(
    serializer: (T) -> String,
    deserializer: (String) -> T?,
): ReadWriteProperty<P, T?> = object : ReadWriteProperty<P, T?> {
    override fun getValue(thisRef: P, property: KProperty<*>): T? =
        this@serialized.getValue(thisRef, property)?.let { deserializer(it) }

    override fun setValue(thisRef: P, property: KProperty<*>, value: T?) {
        this@serialized.setValue(thisRef, property, value?.let { serializer(it) })
    }
}

/**
 * Returns a new [ReadWriteProperty] that only updates its value
 * if [onEqual] returns false (i.e. the new value is different from the old value).
 *
 * ```
 * var distinctValue by propertyOf(0)
 *     .distinctUntilChanged { old, new -> old == new }
 *     .onEach { println("Changed to $it") }
 * ```
 */
fun <T, V> ReadWriteProperty<T, V>.distinctUntilChanged(
    onEqual: T.(old: V, new: V) -> Boolean = { old, new -> old == new }
): ReadWriteProperty<T, V> = object : ReadWriteProperty<T, V> by this {
    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        val oldValue = this@distinctUntilChanged.getValue(thisRef, property)
        if (!thisRef.onEqual(oldValue, value)) {
            this@distinctUntilChanged.setValue(thisRef, property, value)
        }
    }
}

/**
 * Executes [block] every time the value is updated.
 */
fun <T, V> ReadWriteProperty<T, V>.onEach(
    block: T.(V) -> Unit
): ReadWriteProperty<T, V> = object : ReadWriteProperty<T, V> by this {
    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        this@onEach.setValue(thisRef, property, value)
        thisRef.block(value)
    }
}


fun <T, V> ReadWriteProperty<T, V>.onEachBefore(
    block: T.(V) -> Unit
): ReadWriteProperty<T, V> = object : ReadWriteProperty<T, V> by this {
    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        thisRef.block(value)
        this@onEachBefore.setValue(thisRef, property, value)
    }
}

fun <V> propertyOf(
    get: Any?.(KProperty<*>) -> V,
): ReadOnlyProperty<Any?, V> = ReadOnlyProperty { thisRef, property -> thisRef.get(property) }

fun <V> propertyOf(
    get: Any?.(KProperty<*>) -> V,
    set: Any?.(V) -> Unit,
): ReadWriteProperty<Any?, V> = object : ReadWriteProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V =
        thisRef.get(property)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        thisRef.set(value)
    }
}

fun <V> mutablePropertyOf(
    value: V,
): ReadWriteProperty<V, V> = object : ReadWriteProperty<V, V> {
    private var value: V = value

    override fun getValue(thisRef: V, property: KProperty<*>): V = value

    override fun setValue(thisRef: V, property: KProperty<*>, value: V) {
        this.value = value
    }
}

fun <V> V.asProperty(): ReadWriteProperty<Any?, V> =
    object : ReadWriteProperty<Any?, V> {
        private var value: V = this@asProperty

        override fun getValue(thisRef: Any?, property: KProperty<*>): V = value
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
            this.value = value
        }
    }

/**
 * Allows using StateFlow as a delegated property:
 *
 * val current by someStateFlow
 */
operator fun <T, V> StateFlow<V>.getValue(
    thisRef: T,
    property: KProperty<*>
): V = value

/**
 * Allows using MutableStateFlow as a delegated property:
 *
 * var counter by MutableStateFlow(0)
 */
suspend fun <T, V> MutableStateFlow<V>.setValue(
    thisRef: T,
    property: KProperty<*>,
    value: V
) = emit(value)

fun <T, V> ReadWriteProperty<T, V>.readOnly(): ReadOnlyProperty<T, V> = this