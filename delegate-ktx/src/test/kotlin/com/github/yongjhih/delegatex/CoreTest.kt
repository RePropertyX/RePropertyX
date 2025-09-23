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

import kotlin.test.*
import kotlin.test.Test

class CoreTest {
    @Test
    fun testByWeakReference() {
        class Example {
            var value: String? by byWeakReference()
        }

        val example = Example()
        assertNull(example.value)

        example.value = "Hello"
        assertEquals("Hello", example.value)

        example.value = null
        assertNull(example.value)
    }

    @Test
    fun testByWeakReferenceDefault() {
        class Example {
            var value: String? by byWeakReference("Hello")
        }

        val example = Example()
        assertEquals("Hello", example.value)

        example.value = ""
        assertEquals("", example.value)

        example.value = null
        assertNull(example.value)
    }
}