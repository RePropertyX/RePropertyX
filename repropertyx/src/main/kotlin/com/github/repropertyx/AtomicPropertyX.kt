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
fun <V> AtomicReference<V>.by() = object : ReadWriteProperty<AtomicReference<V>, V> {
    override fun getValue(thisRef: AtomicReference<V>, property: KProperty<*>): V = thisRef.get()
    override fun setValue(thisRef: AtomicReference<V>, property: KProperty<*>, value: V) {
        thisRef.set(value)
    }
}

fun AtomicBoolean.by() = object : ReadWriteProperty<AtomicBoolean, Boolean> {
    override fun getValue(thisRef: AtomicBoolean, property: KProperty<*>): Boolean = thisRef.get()
    override fun setValue(thisRef: AtomicBoolean, property: KProperty<*>, value: Boolean) {
        thisRef.set(value)
    }
}

fun AtomicInteger.by() = object : ReadWriteProperty<AtomicInteger, Int> {
    override fun getValue(thisRef: AtomicInteger, property: KProperty<*>): Int = thisRef.get()
    override fun setValue(thisRef: AtomicInteger, property: KProperty<*>, value: Int) {
        thisRef.set(value)
    }
}

fun AtomicLong.by() = object : ReadWriteProperty<AtomicLong, Long> {
    override fun getValue(thisRef: AtomicLong, property: KProperty<*>): Long = thisRef.get()
    override fun setValue(thisRef: AtomicLong, property: KProperty<*>, value: Long) {
        thisRef.set(value)
    }
}

fun AtomicIntegerArray.by(index: Int) = object : ReadWriteProperty<AtomicIntegerArray, Int> {
    override fun getValue(thisRef: AtomicIntegerArray, property: KProperty<*>): Int = thisRef.get(index)
    override fun setValue(thisRef: AtomicIntegerArray, property: KProperty<*>, value: Int) {
        thisRef.set(index, value)
    }
}

fun AtomicLongArray.by(index: Int) = object : ReadWriteProperty<AtomicLongArray, Long> {
    override fun getValue(thisRef: AtomicLongArray, property: KProperty<*>): Long = thisRef.get(index)
    override fun setValue(thisRef: AtomicLongArray, property: KProperty<*>, value: Long) {
        thisRef.set(index, value)
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