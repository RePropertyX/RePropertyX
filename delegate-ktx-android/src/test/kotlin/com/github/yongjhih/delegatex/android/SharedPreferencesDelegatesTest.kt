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

package com.github.yongjhih.delegatex.android

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SharedPreferencesDelegatesTest {
    
    private lateinit var memorySharedPreferences: MemorySharedPreferences
    
    @BeforeEach
    fun setUp() {
        memorySharedPreferences = MemorySharedPreferences()
    }
    
    @Test
    fun `bySharedPreferenceString stores and retrieves string values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting and getting string value
        testClass.username = "test_user"
        assertEquals("test_user", testClass.username)
        
        // Test getting value after setting
        testClass.username = "updated_user"
        assertEquals("updated_user", testClass.username)
    }
    
    @Test
    fun `bySharedPreferenceString handles null values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting null value
        testClass.username = null
        assertNull(testClass.username)
        
        // Test setting non-null value after null
        testClass.username = "test_user"
        assertEquals("test_user", testClass.username)
    }
    
    @Test
    fun `bySharedPreferenceString returns null when key doesn't exist`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Should return null when key doesn't exist
        assertNull(testClass.username)
    }
    
    @Test
    fun `bySharedPreferenceInt stores and retrieves int values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting and getting int value
        testClass.age = 25
        assertEquals(25, testClass.age)
        
        // Test getting value after setting
        testClass.age = 30
        assertEquals(30, testClass.age)
    }
    
    @Test
    fun `bySharedPreferenceInt handles negative values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting negative value
        testClass.age = -5
        assertEquals(-5, testClass.age)
        
        // Test setting zero
        testClass.age = 0
        assertEquals(0, testClass.age)
    }
    
    @Test
    fun `bySharedPreferenceInt returns null when key doesn't exist`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Should return null when key doesn't exist
        assertNull(testClass.age)
    }
    
    @Test
    fun `bySharedPreferenceBoolean stores and retrieves boolean values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting and getting boolean value
        testClass.isEnabled = true
        assertTrue(testClass.isEnabled == true)
        
        // Test getting value after setting
        testClass.isEnabled = false
        assertFalse(testClass.isEnabled == true)
    }
    
    @Test
    fun `bySharedPreferenceBoolean handles false values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting false value
        testClass.isEnabled = false
        assertFalse(testClass.isEnabled == true)
        
        // Test setting true after false
        testClass.isEnabled = true
        assertTrue(testClass.isEnabled == true)
    }
    
    @Test
    fun `bySharedPreferenceBoolean returns null when key doesn't exist`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Should return null when key doesn't exist
        assertNull(testClass.isEnabled)
    }
    
    @Test
    fun `bySharedPreferenceLong stores and retrieves long values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting and getting long value
        testClass.timestamp = 1234567890L
        assertEquals(1234567890L, testClass.timestamp)
        
        // Test getting value after setting
        testClass.timestamp = 9876543210L
        assertEquals(9876543210L, testClass.timestamp)
    }
    
    @Test
    fun `bySharedPreferenceLong handles large values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting large value
        testClass.timestamp = Long.MAX_VALUE
        assertEquals(Long.MAX_VALUE, testClass.timestamp)
        
        // Test setting minimum value
        testClass.timestamp = Long.MIN_VALUE
        assertEquals(Long.MIN_VALUE, testClass.timestamp)
    }
    
    @Test
    fun `bySharedPreferenceLong returns null when key doesn't exist`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Should return null when key doesn't exist
        assertNull(testClass.timestamp)
    }
    
    @Test
    fun `bySharedPreferenceFloat stores and retrieves float values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting and getting float value
        testClass.rating = 4.5f
        assertEquals(4.5f, testClass.rating)
        
        // Test getting value after setting
        testClass.rating = 3.7f
        assertEquals(3.7f, testClass.rating)
    }
    
    @Test
    fun `bySharedPreferenceFloat handles decimal values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting decimal value
        testClass.rating = 3.14159f
        assertEquals(3.14159f, testClass.rating)
        
        // Test setting negative decimal
        testClass.rating = -2.71828f
        assertEquals(-2.71828f, testClass.rating)
    }
    
    @Test
    fun `bySharedPreferenceFloat returns null when key doesn't exist`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Should return null when key doesn't exist
        assertNull(testClass.rating)
    }
    
    @Test
    fun `bySharedPreferenceStringSet stores and retrieves string set values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting and getting string set value
        val testSet = setOf("item1", "item2", "item3")
        testClass.tags = testSet
        assertEquals(testSet, testClass.tags)
        
        // Test getting value after setting
        val updatedSet = setOf("item4", "item5")
        testClass.tags = updatedSet
        assertEquals(updatedSet, testClass.tags)
    }
    
    @Test
    fun `bySharedPreferenceStringSet handles null values`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting null value
        testClass.tags = null
        assertNull(testClass.tags)
        
        // Test setting non-null value after null
        val testSet = setOf("item1", "item2")
        testClass.tags = testSet
        assertEquals(testSet, testClass.tags)
    }
    
    @Test
    fun `bySharedPreferenceStringSet returns null when key doesn't exist`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Should return null when key doesn't exist
        assertNull(testClass.tags)
    }
    
    @Test
    fun `multiple properties maintain separate state`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test that different properties use different keys
        testClass.username = "user1"
        testClass.age = 25
        testClass.isEnabled = true
        
        assertEquals("user1", testClass.username)
        assertEquals(25, testClass.age)
        assertTrue(testClass.isEnabled == true)
        
        // Verify they maintain separate state
        testClass.username = "user2"
        testClass.age = 30
        testClass.isEnabled = false
        
        assertEquals("user2", testClass.username)
        assertEquals(30, testClass.age)
        assertFalse(testClass.isEnabled == true)
    }
    
    @Test
    fun `properties with custom key functions`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test setting value with custom key function
        testClass.customKeyProperty = "custom_value"
        assertEquals("custom_value", testClass.customKeyProperty)
        
        // Test getting value with custom key function
        testClass.customKeyProperty = "updated_value"
        assertEquals("updated_value", testClass.customKeyProperty)
    }
    
    @Test
    fun `properties persist across multiple instances`() {
        val testClass1 = TestClass(memorySharedPreferences)
        val testClass2 = TestClass(memorySharedPreferences)
        
        // Set value in first instance
        testClass1.username = "shared_user"
        
        // Should be available in second instance
        assertEquals("shared_user", testClass2.username)
        
        // Update in second instance
        testClass2.username = "updated_shared_user"
        
        // Should be reflected in first instance
        assertEquals("updated_shared_user", testClass1.username)
    }
    
    @Test
    fun `properties with different types maintain type safety`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test different types
        testClass.username = "string_value"
        testClass.age = 42
        testClass.isEnabled = true
        testClass.timestamp = 123456789L
        testClass.rating = 4.2f
        testClass.tags = setOf("tag1", "tag2")
        
        assertEquals("string_value", testClass.username)
        assertEquals(42, testClass.age)
        assertTrue(testClass.isEnabled == true)
        assertEquals(123456789L, testClass.timestamp)
        assertEquals(4.2f, testClass.rating)
        assertEquals(setOf("tag1", "tag2"), testClass.tags)
    }
    
    @Test
    fun `null values are properly handled for all types`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test null values for all types
        testClass.username = null
        testClass.age = null
        testClass.isEnabled = null
        testClass.timestamp = null
        testClass.rating = null
        testClass.tags = null
        
        assertNull(testClass.username)
        assertNull(testClass.age)
        assertNull(testClass.isEnabled)
        assertNull(testClass.timestamp)
        assertNull(testClass.rating)
        assertNull(testClass.tags)
    }
    
    @Test
    fun `custom key function transforms property names`() {
        val testClass = TestClass(memorySharedPreferences)
        
        // Test that custom key function transforms property names
        testClass.customKeyProperty = "test_value"
        
        // The custom key function should transform the property name
        // Check that the value is stored with the transformed key
        assertTrue(memorySharedPreferences.contains("custom_key_name"))
        assertEquals("test_value", memorySharedPreferences.getString("custom_key_name", null))
    }

    @Test
    fun `custom key`() {
        memorySharedPreferences.uid = "0"
        memorySharedPreferences.token = "0"
        assertEquals(memorySharedPreferences.token, "0")

        memorySharedPreferences.uid = "1"
        memorySharedPreferences.token = "1"
        assertEquals(memorySharedPreferences.token, "1")

        memorySharedPreferences.uid = "0"
        assertEquals(memorySharedPreferences.token, "0")
        memorySharedPreferences.uid = "1"
        assertEquals(memorySharedPreferences.token, "1")
    }

    private class TestClass(private val prefs: SharedPreferences) {
        var username: String? by prefs.byString()
        var age: Int? by prefs.byInt()
        var isEnabled: Boolean? by prefs.byBoolean()
        var timestamp: Long? by prefs.byLong()
        var rating: Float? by prefs.byFloat()
        var tags: Set<String>? by prefs.byStringSet()
        var customKeyProperty: String? by prefs.byString { "custom_key_name" }
    }
}

var SharedPreferences.uid: String? by bySharedPreferenceString { "0" }
var SharedPreferences.token: String? by bySharedPreferenceString { "token${uid}" }

/**
 * In-memory implementation of SharedPreferences for testing
 */
class MemorySharedPreferences : SharedPreferences {
    private val data = mutableMapOf<String, Any>()
    private val listeners = mutableListOf<SharedPreferences.OnSharedPreferenceChangeListener>()
    
    override fun getAll(): Map<String, *> = data.toMap()
    
    override fun getString(key: String, defValue: String?): String? {
        return data[key] as? String ?: defValue
    }
    
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        return data[key] as? Set<String> ?: defValues
    }
    
    override fun getInt(key: String, defValue: Int): Int {
        return data[key] as? Int ?: defValue
    }
    
    override fun getLong(key: String, defValue: Long): Long {
        return data[key] as? Long ?: defValue
    }
    
    override fun getFloat(key: String, defValue: Float): Float {
        return data[key] as? Float ?: defValue
    }
    
    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return data[key] as? Boolean ?: defValue
    }
    
    override fun contains(key: String): Boolean {
        return data.containsKey(key)
    }
    
    override fun edit(): Editor {
        return MemoryEditor()
    }
    
    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.add(listener)
    }
    
    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.remove(listener)
    }
    
    private inner class MemoryEditor : Editor {
        private val changes = mutableMapOf<String, Any?>()
        private val removals = mutableSetOf<String>()
        
        override fun putString(key: String, value: String?): Editor {
            if (value == null) {
                removals.add(key)
            } else {
                changes[key] = value
                removals.remove(key)
            }
            return this
        }
        
        override fun putStringSet(key: String, values: Set<String>?): Editor {
            if (values == null) {
                removals.add(key)
            } else {
                changes[key] = values
                removals.remove(key)
            }
            return this
        }
        
        override fun putInt(key: String, value: Int): Editor {
            changes[key] = value
            removals.remove(key)
            return this
        }
        
        override fun putLong(key: String, value: Long): Editor {
            changes[key] = value
            removals.remove(key)
            return this
        }
        
        override fun putFloat(key: String, value: Float): Editor {
            changes[key] = value
            removals.remove(key)
            return this
        }
        
        override fun putBoolean(key: String, value: Boolean): Editor {
            changes[key] = value
            removals.remove(key)
            return this
        }
        
        override fun remove(key: String): Editor {
            removals.add(key)
            changes.remove(key)
            return this
        }
        
        override fun clear(): Editor {
            changes.clear()
            removals.addAll(data.keys)
            return this
        }
        
        override fun commit(): Boolean {
            apply()
            return true
        }
        
        override fun apply() {
            // Apply removals
            removals.forEach { key ->
                data.remove(key)
                listeners.forEach { it.onSharedPreferenceChanged(this@MemorySharedPreferences, key) }
            }
            
            // Apply changes
            changes.forEach { (key, value) ->
                data[key] = value!!
                listeners.forEach { it.onSharedPreferenceChanged(this@MemorySharedPreferences, key) }
            }
            
            // Clear pending changes
            changes.clear()
            removals.clear()
        }
    }
}
