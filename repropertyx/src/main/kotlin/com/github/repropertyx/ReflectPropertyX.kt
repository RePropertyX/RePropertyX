package com.github.repropertyx

import java.lang.reflect.Field
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T: Any, V> byDeclaredField(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadWriteProperty<T, V> {
    private var cachedField: Field? by mutablePropertyOf<Field?>(null)
        .onEach { it?.isAccessible = true }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getDeclaredField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .get(thisRef) as V

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getDeclaredField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .set(thisRef, value)
    }
}

fun <T: Any, V> byDeclaredFieldOrNull(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadWriteProperty<T, V?> {
    private var cachedField: Field? = null
        set(value) { field = value?.also { it.isAccessible = true } }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V? {
        return try {
            (cachedField.takeIf { cache } ?: thisRef.javaClass.getDeclaredField(name(thisRef, property.name))
                .also { if (cache) cachedField = it })
                .get(thisRef) as? V
        } catch (e: NoSuchFieldException) {
            null
        }
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getDeclaredField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .set(thisRef, value)
    }
}

fun <T: Any, V> byField(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadWriteProperty<T, V> {
    private var cachedField: Field? = null
        set(value) { field = value?.also { it.isAccessible = true } }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .get(thisRef) as V

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .set(thisRef, value)
    }
}

fun <T: Any, V> byFieldOrNull(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadWriteProperty<T, V?> {
    private var cachedField: Field? = null
        set(value) { field = value?.also { it.isAccessible = true } }
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V? =
        try {
            (cachedField.takeIf { cache } ?: thisRef.javaClass.getField(name(thisRef, property.name))
                .also { if (cache) cachedField = it })
                .get(thisRef) as? V
        } catch (e: NoSuchFieldException) {
            null
        }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .set(thisRef, value)
    }
}


fun <T: Any, V> byReadDeclaredField(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadOnlyProperty<T, V> {
    private var cachedField: Field? = null
        set(value) { field = value?.also { it.isAccessible = true } }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getDeclaredField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .get(thisRef) as V
}

fun <T: Any, V> byReadDeclaredFieldOrNull(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadOnlyProperty<T, V?> {
    private var cachedField: Field? = null
        set(value) { field = value?.also { it.isAccessible = true } }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V? {
        return try {
            (cachedField.takeIf { cache } ?: thisRef.javaClass.getDeclaredField(name(thisRef, property.name))
                .also { if (cache) cachedField = it })
                .get(thisRef) as? V
        } catch (e: NoSuchFieldException) {
            null
        }
    }

}

fun <T: Any, V> byReadField(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadOnlyProperty<T, V> {
    private var cachedField: Field? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V =
        (cachedField.takeIf { cache } ?: thisRef.javaClass.getField(name(thisRef, property.name))
            .also { if (cache) cachedField = it })
            .get(thisRef) as V
}

fun <T: Any, V> byReadFieldOrNull(cache: Boolean = true, name: T.(String) -> String = { it }) = object : ReadOnlyProperty<T, V?> {
    private var cachedField: Field? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: T, property: KProperty<*>): V? =
        try {
            (cachedField.takeIf { cache } ?: thisRef.javaClass.getField(name(thisRef, property.name))
                    .also { if (cache) cachedField = it })
                .get(thisRef) as? V
        } catch (e: NoSuchFieldException) {
            null
        }
}
