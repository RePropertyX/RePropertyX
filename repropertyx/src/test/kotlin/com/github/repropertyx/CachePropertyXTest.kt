package com.github.repropertyx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CachePropertyXTest {

    private var getterCallCount = 0
    private var simulatedTime = 1000L

    @BeforeEach
    fun setUp() {
        getterCallCount = 0
        simulatedTime = 1000L
    }

    private fun createTestDelegate(initial: String = "v1") = propertyOf(
        get = {
            getterCallCount++
            "$initial-$getterCallCount"
        },
        set = { }
    )

    @Test
    fun `maxAge prevents calling getter until age exceeds maxAge`() {
        val cachedDelegate = createTestDelegate().cached(
            maxAgeMillis = 5000L,
            clock = { simulatedTime }
        )
        var value: String by cachedDelegate

        // First access fetches from getter
        assertEquals("v1-1", value)
        assertEquals(1, getterCallCount)

        // Access within maxAge (1000 + 3000 = 4000 <= 6000) uses cache
        simulatedTime = 4000L
        assertEquals("v1-1", value)
        assertEquals(1, getterCallCount)

        // Access after maxAge (1000 + 6000 = 7000 > 6000) calls getter again
        simulatedTime = 7000L
        assertEquals("v1-2", value)
        assertEquals(2, getterCallCount)
    }

    @Test
    fun `maxStale serves stale cache within maxAge plus maxStale window`() {
        val cachedDelegate = createTestDelegate().cached(
            maxAgeMillis = 5000L,
            maxStaleMillis = 3000L,
            clock = { simulatedTime }
        )
        var value: String by cachedDelegate

        assertEquals("v1-1", value) // at 1000ms
        assertTrue(cachedDelegate.isFresh(1000L))

        // Exceeded maxAge (6000ms), but within maxStale (1000 + 5000 + 3000 = 9000ms)
        simulatedTime = 7000L
        assertFalse(cachedDelegate.isFresh(7000L))
        assertTrue(cachedDelegate.isStale(7000L))
        assertEquals("v1-1", value) // Serves stale cache!
        assertEquals(1, getterCallCount)

        // Exceeded maxStale (10000ms > 9000ms) -> fetches fresh value
        simulatedTime = 10000L
        assertFalse(cachedDelegate.isStale(10000L))
        assertEquals("v1-2", value)
        assertEquals(2, getterCallCount)
    }

    @Test
    fun `forceCache always serves cached value regardless of age`() {
        val cachedDelegate = createTestDelegate().cached(
            maxAgeMillis = 1000L,
            forceCache = true,
            clock = { simulatedTime }
        )
        var value: String by cachedDelegate

        assertEquals("v1-1", value)

        // Even after 100 seconds, forceCache returns cached value
        simulatedTime = 100000L
        assertEquals("v1-1", value)
        assertEquals(1, getterCallCount)
    }

    @Test
    fun `invalidate forces getter invocation on next access`() {
        val cachedDelegate = createTestDelegate().cached(
            maxAgeMillis = 100000L,
            clock = { simulatedTime }
        )
        var value: String by cachedDelegate

        assertEquals("v1-1", value)
        assertEquals(1, getterCallCount)

        cachedDelegate.invalidate()
        assertFalse(cachedDelegate.isCached)

        assertEquals("v1-2", value)
        assertEquals(2, getterCallCount)
    }

    @Test
    fun `dynamic setPolicy changes cache behavior at runtime`() {
        val cachedDelegate = createTestDelegate().cached(
            maxAgeMillis = 5000L,
            clock = { simulatedTime }
        )
        var value: String by cachedDelegate

        assertEquals("v1-1", value)

        simulatedTime = 10000L // Exceeded maxAge
        cachedDelegate.setPolicy(CachePolicy.FORCE_CACHE)

        assertEquals("v1-1", value) // Force cache active!
        assertEquals(1, getterCallCount)
    }
}
