package com.github.yongjhih.delegatex

import kotlin.test.*
import kotlin.test.Test
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DelegatesTest {
    
    @Test
    fun `stringDelegate with default value`() {
        val testClass = TestClass()
        
        assertEquals("default", testClass.stringWithDefault)
        
        testClass.stringWithDefault = "new_value"
        assertEquals("new_value", testClass.stringWithDefault)
    }
    
    @Test
    fun `stringDelegate with null default`() {
        val testClass = TestClass()
        
        assertNull(testClass.stringWithNullDefault)
        
        testClass.stringWithNullDefault = "new_value"
        assertEquals("new_value", testClass.stringWithNullDefault)
        
        testClass.stringWithNullDefault = null
        assertNull(testClass.stringWithNullDefault)
    }
    
    @Test
    fun `intDelegate with default value`() {
        val testClass = TestClass()
        
        assertEquals(42, testClass.intWithDefault)
        
        testClass.intWithDefault = 100
        assertEquals(100, testClass.intWithDefault)
    }
    
    @Test
    fun `intDelegate with zero default`() {
        val testClass = TestClass()
        
        assertEquals(0, testClass.intWithZeroDefault)
        
        testClass.intWithZeroDefault = 50
        assertEquals(50, testClass.intWithZeroDefault)
    }
    
    @Test
    fun `booleanDelegate with default value`() {
        val testClass = TestClass()
        
        assertEquals(true, testClass.booleanWithDefault)
        
        testClass.booleanWithDefault = false
        assertEquals(false, testClass.booleanWithDefault)
    }
    
    @Test
    fun `booleanDelegate with false default`() {
        val testClass = TestClass()
        
        assertEquals(false, testClass.booleanWithFalseDefault)
        
        testClass.booleanWithFalseDefault = true
        assertEquals(true, testClass.booleanWithFalseDefault)
    }
    
    @Test
    fun `longDelegate with default value`() {
        val testClass = TestClass()
        
        assertEquals(123456789L, testClass.longWithDefault)
        
        testClass.longWithDefault = 987654321L
        assertEquals(987654321L, testClass.longWithDefault)
    }
    
    @Test
    fun `longDelegate with zero default`() {
        val testClass = TestClass()
        
        assertEquals(0L, testClass.longWithZeroDefault)
        
        testClass.longWithZeroDefault = 999L
        assertEquals(999L, testClass.longWithZeroDefault)
    }
    
    @Test
    fun `doubleDelegate with default value`() {
        val testClass = TestClass()
        
        assertEquals(3.14159, testClass.doubleWithDefault)
        
        testClass.doubleWithDefault = 2.71828
        assertEquals(2.71828, testClass.doubleWithDefault)
    }
    
    @Test
    fun `doubleDelegate with zero default`() {
        val testClass = TestClass()
        
        assertEquals(0.0, testClass.doubleWithZeroDefault)
        
        testClass.doubleWithZeroDefault = 1.5
        assertEquals(1.5, testClass.doubleWithZeroDefault)
    }
    
    @Test
    fun `floatDelegate with default value`() {
        val testClass = TestClass()
        
        assertEquals(2.5f, testClass.floatWithDefault)
        
        testClass.floatWithDefault = 1.75f
        assertEquals(1.75f, testClass.floatWithDefault)
    }
    
    @Test
    fun `floatDelegate with zero default`() {
        val testClass = TestClass()
        
        assertEquals(0.0f, testClass.floatWithZeroDefault)
        
        testClass.floatWithZeroDefault = 3.14f
        assertEquals(3.14f, testClass.floatWithZeroDefault)
    }
    
    @Test
    fun `delegate with default value`() {
        val testClass = TestClass()
        
        assertEquals("generic_default", testClass.genericWithDefault)
        
        testClass.genericWithDefault = "new_generic_value"
        assertEquals("new_generic_value", testClass.genericWithDefault)
    }
    
    @Test
    fun `delegate with null default`() {
        val testClass = TestClass()
        
        assertNull(testClass.genericWithNullDefault)
        
        testClass.genericWithNullDefault = "new_value"
        assertEquals("new_value", testClass.genericWithNullDefault)
        
        testClass.genericWithNullDefault = null
        assertNull(testClass.genericWithNullDefault)
    }
    
    @Test
    fun `nullableDelegate starts as null`() {
        val testClass = TestClass()
        
        assertNull(testClass.nullableValue)
        
        testClass.nullableValue = "not_null_anymore"
        assertEquals("not_null_anymore", testClass.nullableValue)
        
        testClass.nullableValue = null
        assertNull(testClass.nullableValue)
    }
    
    @Test
    fun `delegates maintain separate state`() {
        val testClass1 = TestClass()
        val testClass2 = TestClass()
        
        testClass1.stringWithDefault = "value1"
        testClass2.stringWithDefault = "value2"
        
        assertEquals("value1", testClass1.stringWithDefault)
        assertEquals("value2", testClass2.stringWithDefault)
    }
    
    @Test
    fun `delegates handle different types correctly`() {
        val testClass = TestClass()
        
        testClass.stringWithDefault = "string_value"
        testClass.intWithDefault = 42
        testClass.booleanWithDefault = true
        testClass.longWithDefault = 123L
        testClass.doubleWithDefault = 3.14
        testClass.floatWithDefault = 2.5f
        
        assertEquals("string_value", testClass.stringWithDefault)
        assertEquals(42, testClass.intWithDefault)
        assertEquals(true, testClass.booleanWithDefault)
        assertEquals(123L, testClass.longWithDefault)
        assertEquals(3.14, testClass.doubleWithDefault)
        assertEquals(2.5f, testClass.floatWithDefault)
    }
    
    private class TestClass {
        var stringWithDefault: String by stringDelegate("default")
        var stringWithNullDefault: String? by stringDelegate(null)
        
        var intWithDefault: Int by intDelegate(42)
        var intWithZeroDefault: Int by intDelegate(0)
        
        var booleanWithDefault: Boolean by booleanDelegate(true)
        var booleanWithFalseDefault: Boolean by booleanDelegate(false)
        
        var longWithDefault: Long by longDelegate(123456789L)
        var longWithZeroDefault: Long by longDelegate(0L)
        
        var doubleWithDefault: Double by doubleDelegate(3.14159)
        var doubleWithZeroDefault: Double by doubleDelegate(0.0)
        
        var floatWithDefault: Float by floatDelegate(2.5f)
        var floatWithZeroDefault: Float by floatDelegate(0.0f)
        
        var genericWithDefault: String by delegate("generic_default")
        var genericWithNullDefault: String? by delegate(null)
        
        var nullableValue: String? by delegate()
    }
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