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

package com.github.repropertyx.android
import com.github.repropertyx.observable
import com.github.repropertyx.observed
import com.github.repropertyx.or
import com.github.repropertyx.takeIf
import com.github.repropertyx.validate
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.properties.Delegates

class SharedPreferencesDelegatexTest {
    
    private val prefs: MemorySharedPreferences by lazy {
        MemorySharedPreferences()
    }
    
    @BeforeEach
    fun setUp() {
        prefs.edit().clear().apply()
    }

    @AfterTest
    fun tearDown() {
        prefs.edit().clear().apply()
    }

    @Test
    fun test() {
        username = "yongjhih"
        prefs.firstName = "Yongjhih"
        prefs.lastName = "Chen"

        assertEquals(prefs.email, "unknown@github.com")
        var oldEmail: String? = null
        var newEmail: String? = null
        emailProperty.observers.add { old, new ->
            oldEmail = old
            newEmail = new
        }

        prefs.email = "yongjhih@github.com"
        assertEquals("unknown@github.com", oldEmail)
        assertEquals("yongjhih@github.com", newEmail)

        prefs.email = "yongjhih_github.com"
        assertEquals(prefs.email, "yongjhih@github.com")
    }

    var username by prefs.byString().or { "unknownUsername" }
    var SharedPreferences.lastName by bySharedPreferenceString().or { "unknownLastName" }
    var SharedPreferences.firstName by bySharedPreferenceString().or { "unknownFirstName" }
    val emailProperty = prefs.byString().or { "unknown@github.com" }
        .takeIf { it.contains("@") }
        .observed()
    var SharedPreferences.email by emailProperty
}
