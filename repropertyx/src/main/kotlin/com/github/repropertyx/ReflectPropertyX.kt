package com.github.repropertyx

import java.lang.reflect.Field
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T: Any, V> byField(
    crossinline field: Class<T>.(String) -> Field,
    cache: Boolean = true,
    crossinline name: T.(String) -> String = { it },
) = object : ReadWriteProperty<T, V> {
    private var cachedField: Field? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        cachedField(thisRef, property).get(thisRef) as V

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        cachedField(thisRef, property).set(thisRef, value)
    }

    fun cachedField(thisRef: T, property: KProperty<*>): Field =
        (cachedField.takeIf { cache }
            ?: thisRef.tryRunInfer({ it is NoSuchFieldException })
            { field(name(thisRef, property.name)) }
                .also { it.isAccessible = true }
                .also { if (cache) cachedField = it })
}

inline fun <reified T: Any, R> T.tryRunInfer(
    should: (Exception) -> Boolean = { true },
    block: Class<T>.() -> R,
): R = try {
    javaClass.block()
} catch (e: Exception) {
    if (T::class.java != javaClass && should(e)) T::class.java.block()
    else throw e
}

/**
 * Usage:
 *
 * ```
 * var Person.name: String by byDeclaredField<Person, String?> { "_name" }
 * ```
 *
 * ```
 * val Person.name: String? by byDeclaredField<Person, String?> { "_name" }.orNull().readOnly()
 * ```
 *
 * ```
 * val Person.name: String? by byDeclaredField<Person, String?> { "_name" }
 *   .orNull { e, _ -> if (e is NoSuchFieldException) null else throw e }.readOnly()
 * ```
 */
inline fun <reified T: Any, V> byDeclaredField(cache: Boolean = true, crossinline name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { getDeclaredField(it) }
    )

inline fun <reified T: Any, V> byFirstDeclaredField(cache: Boolean = true, crossinline name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { firstDeclaredField(it) }
    )

inline fun <reified T: Any, V> byField(cache: Boolean = true, crossinline name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { getField(it) }
    )

fun <T> Class<T>.firstDeclaredField(name: String): Field {
    var clazz: Class<*>? = this
    while (clazz != null) {
        try {
            return clazz.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            // ignore
        }
        clazz = clazz.superclass
    }
    throw NoSuchFieldException(name)
}