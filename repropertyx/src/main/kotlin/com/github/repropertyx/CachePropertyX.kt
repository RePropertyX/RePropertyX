package com.github.repropertyx

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Cache policy configuration for property delegation.
 *
 * @param maxAgeMillis Duration in milliseconds for which the cached value is considered fresh.
 * @param maxStaleMillis Additional duration in milliseconds after [maxAgeMillis] during which stale values can still be served.
 * @param forceCache If true, always serves cached values if available regardless of age.
 */
data class CachePolicy(
    val maxAgeMillis: Long = 0L,
    val maxStaleMillis: Long = 0L,
    val forceCache: Boolean = false
) {
    companion object {
        val NO_CACHE = CachePolicy(maxAgeMillis = 0L, maxStaleMillis = 0L)
        val FORCE_CACHE = CachePolicy(maxAgeMillis = Long.MAX_VALUE, maxStaleMillis = Long.MAX_VALUE, forceCache = true)

        fun maxAge(millis: Long) = CachePolicy(maxAgeMillis = millis)
        fun maxStale(maxAgeMillis: Long, maxStaleMillis: Long) = CachePolicy(maxAgeMillis = maxAgeMillis, maxStaleMillis = maxStaleMillis)
    }
}

/**
 * A property delegate decorator with advanced HTTP-inspired caching mechanisms (maxAge, maxStale, forceCache, invalidate).
 */
class CachedPropertyDelegate<T, V>(
    private val delegate: ReadWriteProperty<T, V>,
    private var policy: CachePolicy = CachePolicy.NO_CACHE,
    private val clock: () -> Long = { System.currentTimeMillis() }
) : ReadWriteProperty<T, V> {

    private var cachedValue: V? = null
    private var hasValue: Boolean = false
    private var cachedAt: Long = 0L

    val isCached: Boolean get() = hasValue
    val lastCachedAt: Long get() = cachedAt

    /**
     * Checks if the cached value is currently fresh according to [policy].
     */
    fun isFresh(currentTime: Long = clock()): Boolean {
        if (!hasValue) return false
        if (policy.forceCache) return true
        if (policy.maxAgeMillis <= 0L) return false
        return (currentTime - cachedAt) <= policy.maxAgeMillis
    }

    /**
     * Checks if the cached value is currently stale but still within [maxStaleMillis].
     */
    fun isStale(currentTime: Long = clock()): Boolean {
        if (!hasValue) return false
        if (isFresh(currentTime)) return false
        if (policy.maxStaleMillis <= 0L) return false
        val totalAllowedAge = policy.maxAgeMillis + policy.maxStaleMillis
        return (currentTime - cachedAt) <= totalAllowedAge
    }

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        val now = clock()
        if (hasValue && (isFresh(now) || policy.forceCache || isStale(now))) {
            @Suppress("UNCHECKED_CAST")
            return cachedValue as V
        }

        // Cache expired or missing -> fetch fresh value from underlying getter
        val freshValue = delegate.getValue(thisRef, property)
        cachedValue = freshValue
        hasValue = true
        cachedAt = now
        return freshValue
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        cachedValue = value
        hasValue = true
        cachedAt = clock()
        delegate.setValue(thisRef, property, value)
    }

    /**
     * Clears the cached value, forcing the next [getValue] call to fetch a fresh value from the underlying getter.
     */
    fun invalidate() {
        cachedValue = null
        hasValue = false
        cachedAt = 0L
    }

    /**
     * Dynamically updates the cache policy (e.g. switching to FORCE_CACHE when offline).
     */
    fun setPolicy(newPolicy: CachePolicy) {
        this.policy = newPolicy
    }
}

/**
 * Wraps a property delegate with advanced caching mechanisms ([maxAgeMillis], [maxStaleMillis], [forceCache]).
 */
fun <T, V> ReadWriteProperty<T, V>.cached(
    maxAgeMillis: Long,
    maxStaleMillis: Long = 0L,
    forceCache: Boolean = false,
    clock: () -> Long = { System.currentTimeMillis() }
): CachedPropertyDelegate<T, V> = CachedPropertyDelegate(
    delegate = this,
    policy = CachePolicy(maxAgeMillis, maxStaleMillis, forceCache),
    clock = clock
)

/**
 * Wraps a property delegate with a custom [CachePolicy].
 */
fun <T, V> ReadWriteProperty<T, V>.cached(
    policy: CachePolicy,
    clock: () -> Long = { System.currentTimeMillis() }
): CachedPropertyDelegate<T, V> = CachedPropertyDelegate(
    delegate = this,
    policy = policy,
    clock = clock
)
