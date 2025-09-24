package com.github.repropertyx

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * ```
 * fun <V> byAtomic(value: V) = AtomicReference(value).byAtomic()
 * ```
 */
fun <V> AtomicReference<V>.by() = object : ReadWriteProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V = get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        set(value)
    }
}

fun AtomicBoolean.by() = object : ReadWriteProperty<Any?, Boolean> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        set(value)
    }
}

fun AtomicInteger.by() = object : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        set(value)
    }
}

fun AtomicLong.by() = object : ReadWriteProperty<Any?, Long> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long = get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        set(value)
    }
}

fun AtomicIntegerArray.by(index: Int) = object : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = get(index)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        set(index, value)
    }
}

fun AtomicLongArray.by(index: Int) = object : ReadWriteProperty<Any?, Long> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long = get(index)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        set(index, value)
    }
}

/**
 * Usage:
 *
 * ```
 * var atomicInt by 0.byAtomic()
 * ```
 */
fun Int.byAtomic() = AtomicInteger(this).by()

fun Long.byAtomic() = AtomicLong(this).by()

fun Boolean.byAtomic() = AtomicBoolean(this).by()

fun CharSequence.byAtomic() = AtomicReference(this).by()
