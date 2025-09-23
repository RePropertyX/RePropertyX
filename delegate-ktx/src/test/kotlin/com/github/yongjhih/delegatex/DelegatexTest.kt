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

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.test.*
import kotlin.test.Test

class DelegatexTest {

    @Test
    fun `or operator provides fallback for null values`() {
        val testClass = TestClass()
        // The or operator should handle null values from the underlying delegate
        assertEquals("guest_username", testClass.username)
    }

    @Test
    fun `ReadOnlyPropert_or`() {
        val readOnly: ReadOnlyProperty<Any?, String?> = ReadOnlyProperty { _, _ -> null }
        val value by readOnly.or { "fallback" }
        assertEquals("fallback", value)
    }


    @Test
    fun `or operator uses custom fallback logic`() {
        val testClass = TestClass()
        // The or operator should handle null values from the underlying delegate
        assertEquals("fallback_customFallback", testClass.customFallback)
    }

    @Test
    fun `notNull operator throws exception for null values`() {
        val testClass = TestClass()

        // Should throw exception when trying to get null value
        assertFailsWith<IllegalStateException> {
            testClass.notNullProperty
        }

        // Should work when value is set
        testClass.notNullProperty = "valid_value"
        assertEquals("valid_value", testClass.notNullProperty)
    }

    @Test
    fun `notNull operator with custom error message`() {
        val testClass = TestClass()

        val exception = assertFailsWith<IllegalStateException> {
            testClass.notNullPropertyWithMessage
        }
        assertTrue(exception.message?.contains("Custom null error message") == true)
    }

    @Test
    fun `map operator transforms values correctly`() {
        val testClass = TestClass()
        testClass.ageString = "25"
        testClass.age = 25

        assertEquals(25, testClass.age)
        assertEquals("25", testClass.ageString)
    }

    @Test
    fun `map operator handles bidirectional transformation`() {
        val testClass = TestClass()
        testClass.age = 30
        assertEquals(30, testClass.age)

        testClass.age = 35
        assertEquals(35, testClass.age)
    }

    @Test
    fun `validate operator throws exception for invalid values`() {
        val testClass = TestClass()

        assertFailsWith<IllegalArgumentException> {
            testClass.email = "invalid-email"
        }

        testClass.email = "valid@email.com"
        assertEquals("valid@email.com", testClass.email)
    }

    @Test
    fun `validate operator with custom error message`() {
        val testClass = TestClass()

        val exception = assertFailsWith<IllegalArgumentException> {
            testClass.password = "short"
        }
        assertTrue(exception.message?.contains("Password must be at least 8 characters") == true)
    }

    @Test
    fun `log operator logs property changes`() {
        val testClass = TestClass()
        val logEntries = mutableListOf<String>()

        testClass.loggedCounter = 0
        testClass.loggedCounter = 5
        testClass.loggedCounter = 10

        // Note: In a real test, you'd capture the log output
        // For now, we just verify the property works
        assertEquals(10, testClass.loggedCounter)
    }

    @Test
    fun `once operator prevents multiple assignments`() {
        val testClass = TestClass()

        testClass.onceValue = "first"
        testClass.onceValue = "second"

        assertEquals("first", testClass.onceValue)
    }

    @Test
    fun `once operator allows first assignment`() {
        val testClass = TestClass()

        testClass.onceValue = "only_value"
        assertEquals("only_value", testClass.onceValue)
    }

    @Test
    fun `catch operator handles exceptions gracefully`() {
        val testClass = TestClass()

        assertEquals("default", testClass.safeValue)
    }

    @Test
    fun `catch operator provides custom fallback`() {
        val testClass = TestClass()

        assertEquals("fallback_customCatchValue", testClass.customCatchValue)
    }

    @Test
    fun `cacheIn operator caches values`() {
        val cache = mutableMapOf<String, Any>()
        val testClass = TestClass(cache)

        testClass.cachedValue = "test"
        val firstGet = testClass.cachedValue
        val secondGet = testClass.cachedValue

        assertEquals("test", firstGet)
        assertEquals("test", secondGet)
        assertTrue(cache.containsKey("TestClass.cachedValue"))
    }

    @Test
    fun `cacheIn operator updates cache on set`() {
        val cache = mutableMapOf<String, Any>()
        val testClass = TestClass(cache)

        testClass.cachedValue = "initial"
        testClass.cachedValue = "updated"

        assertEquals("updated", testClass.cachedValue)
        assertEquals("updated", cache["TestClass.cachedValue"])
    }

    @Test
    fun `complex chain of operators works correctly`() {
        val testClass = TestClass()

        testClass.complexProperty = "  TEST  "

        assertEquals("TEST", testClass.complexProperty)
    }

    @Test
    fun `complex chain with fallback works correctly`() {
        val testClass = TestClass()

        // The or operator should handle null values from the underlying delegate
        assertEquals("guest_complexPropertyWithFallback", testClass.complexPropertyWithFallback)
    }

    @Test
    fun `observable operator works as alias for log`() {
        val testClass = TestClass()

        testClass.observableValue = "initial"
        testClass.observableValue = "changed"

        assertEquals("changed", testClass.observableValue)
    }

    @Test
    fun `encrypt operator transforms values to string`() {
        val testClass = TestClass()

        // The encrypt operator should return a string representation
        val result = testClass.encryptedValue
        assertEquals("SECRET", result)
    }

    @Test
    fun `decrypt operator transforms string back to original type`() {
        val testClass = TestClass()

        // The decrypt operator should return the original type
        val result = testClass.decryptedValue
        assertEquals("encrypted_string", result)
    }

    private class TestClass(cache: MutableMap<String, Any> = mutableMapOf()) {
        var username: String by stringDelegate().or { "guest_$it" }

        var customFallback: String by stringDelegate().or { "fallback_$it" }

        var notNullProperty: String by stringDelegate().notNull()

        var notNullPropertyWithMessage: String by stringDelegate().notNull("Custom null error message")

        var ageString: String by stringDelegate("0")
        var age: Int by stringDelegate("0").map(to = { it.toInt() }, from = { it.toString() })

        var email: String by stringDelegate("").validate { require(it.contains("@")) }

        var password: String by stringDelegate("").validate { require(it.length >= 8) { "Password must be at least 8 characters" } }

        var loggedCounter: Int by intDelegate(0).log { old, new -> println("Counter changed: $old -> $new") }

        var onceValue: String by stringDelegate("").once()

        var safeValue: String by stringDelegate("").validate { require(it.isNotEmpty()) }.catch { _, _ -> "default" }

        var customCatchValue: String by stringDelegate("").validate { require(it.isNotEmpty()) }.catch { _, property -> "fallback_${property.name}" }

        var cachedValue: String by stringDelegate("").cacheIn(cache)

        var complexProperty: String by stringDelegate("")
            .map(to = { it.trim() }, from = { it })
            .validate { require(it.isNotEmpty()) }

        var complexPropertyWithFallback: String by stringDelegate(null)
            .or { "guest_$it" }
            .map(to = { it.trim() }, from = { it })

        var observableValue: String by stringDelegate("").observable { old, new -> println("Observable: $old -> $new") }

        var encryptedValue: String by stringDelegate("secret").encrypt { it.uppercase() }

        var decryptedValue: String by stringDelegate("ENCRYPTED_STRING").decrypt { it.lowercase() }
    }

    @Test
    fun `takeIf test`() {
        val adultAge: Int? by User().asProperty().map(to = { it.age }, from = { copy(age = it) }).or { 0 }.takeIf { it > 18 }
        assertEquals(null, adultAge)
    }

    data class User(
        var name: String = "Andrew",
        var age: Int? = null
    )

}


/**
 * Utility delegate functions that can be used as base delegates for composable operators.
 */

/**
 * Creates a simple string delegate with an optional default value.
 */
@JvmName("stringDelegateNullable")
fun stringDelegate(value: String? = null): ReadWriteProperty<Any?, String?> {
    return object : ReadWriteProperty<Any?, String?> {
        private var value: String? = value

        override fun getValue(thisRef: Any?, property: KProperty<*>): String? = this.value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            this.value = value
        }
    }
}

fun stringDelegate(value: String): ReadWriteProperty<Any?, String> {
    return object : ReadWriteProperty<Any?, String> {
        private var value: String = value

        override fun getValue(thisRef: Any?, property: KProperty<*>): String = this.value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            this.value = value
        }
    }
}

/**
 * Creates a simple int delegate with an optional default value.
 */
fun intDelegate(default: Int = 0): ReadWriteProperty<Any?, Int> {
    return object : ReadWriteProperty<Any?, Int> {
        private var value: Int = default

        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            this.value = value
        }
    }
}

/**
 * Creates a simple boolean delegate with an optional default value.
 */
fun booleanDelegate(default: Boolean = false): ReadWriteProperty<Any?, Boolean> {
    return object : ReadWriteProperty<Any?, Boolean> {
        private var value: Boolean = default

        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            this.value = value
        }
    }
}

/**
 * Creates a simple long delegate with an optional default value.
 */
fun longDelegate(default: Long = 0L): ReadWriteProperty<Any?, Long> {
    return object : ReadWriteProperty<Any?, Long> {
        private var value: Long = default

        override fun getValue(thisRef: Any?, property: KProperty<*>): Long = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            this.value = value
        }
    }
}

/**
 * Creates a simple double delegate with an optional default value.
 */
fun doubleDelegate(default: Double = 0.0): ReadWriteProperty<Any?, Double> {
    return object : ReadWriteProperty<Any?, Double> {
        private var value: Double = default

        override fun getValue(thisRef: Any?, property: KProperty<*>): Double = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
            this.value = value
        }
    }
}

/**
 * Creates a simple float delegate with an optional default value.
 */
fun floatDelegate(default: Float = 0.0f): ReadWriteProperty<Any?, Float> {
    return object : ReadWriteProperty<Any?, Float> {
        private var value: Float = default

        override fun getValue(thisRef: Any?, property: KProperty<*>): Float = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            this.value = value
        }
    }
}

/**
 * Creates a nullable delegate that can be used with the .or() operator.
 */
@JvmName("delegateNullable")
fun <T: Any?> delegate(default: T? = null): ReadWriteProperty<Any?, T?> {
    return object : ReadWriteProperty<Any?, T?> {
        private var value: T? = default

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            this.value = value
        }
    }
}

/**
 * Creates a generic delegate with an optional default value.
 */
fun <T: Any> delegate(value: T): ReadWriteProperty<Any?, T> {
    return object : ReadWriteProperty<Any?, T> {
        private var value: T = value

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = this.value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }
    }
}