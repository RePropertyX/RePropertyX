package com.github.repropertyx

import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T: Any, V> byField(
    cache: Boolean = true,
    name: T.(String) -> String = { it },
    field: Class<T>.(String) -> Field,
) = object : ReadWriteProperty<T, V> {
    private var cachedField: Field? = null
        set(value) { field = value?.also { it.isAccessible = true } }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        (cachedField.takeIf { cache } ?: thisRef.javaClass.field(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .get(thisRef) as V

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        (cachedField.takeIf { cache } ?: thisRef.javaClass.field(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .set(thisRef, value)
    }
}

/**
 * Usage:
 *
 * ```
 * byDeclaredField<T, V>(
 *   cache = cache,
 *   name = name
 * ).orNull()
 * ```
 *
 * ```
 * byDeclaredField<T, V>(
 *   cache = cache,
 *   name = name
 * ).orNull().readOnly()
 * ```
 *
 * ```
 * byDeclaredField<T, V>(
 *   cache = cache,
 *   name = name
 * ).orNull { e, _ -> if (e is NoSuchFieldException) null else throw e }.readOnly()
 * ```
 */
fun <T: Any, V: Any> byDeclaredField(cache: Boolean = true, name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { getDeclaredField(it) }
    )

fun <T: Any, V> byFirstDeclaredField(cache: Boolean = true, name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { firstDeclaredField(it) }
    )

fun <T: Any, V> byField(cache: Boolean = true, name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { getField(it) }
    )

fun Class<*>.firstDeclaredField(name: String): Field {
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

fun Class<*>.allDeclaredFields(name: String): Sequence<Field> = sequence {
    var clazz: Class<*>? = this@allDeclaredFields
    while (clazz != null) {
        try {
            yield(clazz.getDeclaredField(name))
        } catch (e: NoSuchFieldException) {
            // Ignore
        }
        clazz = clazz.superclass
    }
}