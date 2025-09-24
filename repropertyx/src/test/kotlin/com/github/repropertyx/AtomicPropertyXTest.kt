package com.github.repropertyx

import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test

class AtomicPropertyXTest {
    @Test
    fun testByAtomic() {
        class Example {
            private val atomicString = AtomicReference<String?>()
            var value: String? by atomicString.by()
            var name: String? by "".byAtomic()
            var number: Int by 0.byAtomic()

            var flag: Boolean by false.byAtomic()
        }

        val example = Example()
        assertNull(example.value)
        assertEquals(0, example.number)
        assertFalse(example.flag)

        example.value = "Hello"
        example.name = "Yongjhih"
        example.number = 42
        example.flag = true

        assertEquals("Hello", example.value)
        assertEquals("Yongjhih", example.name)
        assertEquals(42, example.number)
        assertTrue(example.flag)
    }
}