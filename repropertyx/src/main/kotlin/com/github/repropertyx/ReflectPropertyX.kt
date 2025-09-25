package com.github.repropertyx

import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T: Any, V> byField(
    field: Class<T>.(String) -> Field,
    cache: Boolean = true,
    name: T.(String) -> String = { it },
) = object : ReadWriteProperty<T, V> {
    private var cachedField: Field? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        (cachedField.takeIf { cache } ?: thisRef.javaClass.field(name(thisRef, property.name))
            .also { it.isAccessible = true }
            .also { if (cache) cachedField = it })
            .get(thisRef) as V

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        (cachedField.takeIf { cache } ?: thisRef.javaClass.field(name(thisRef, property.name))
            .also { it.isAccessible = true }
            .also { if (cache) cachedField = it })
            .set(thisRef, value)
    }
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
fun <T: Any, V> byDeclaredField(cache: Boolean = true, name: T.(String) -> String = { it }) =
    byField<T, V>(
        cache = cache,
        name = name,
        field = { getDeclaredField(it) }
    )

fun <T: Any, V: Any> T.declaredFieldBy(cache: Boolean = true, name: T.(String) -> String = { it }) =
    by<T, V>(byDeclaredField(cache, name))

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