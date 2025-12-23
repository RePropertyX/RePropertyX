/*
 * Copyright 2025 RePropertyX
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

import kotlin.properties.ReadWriteProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = get()

operator fun <T> ThreadLocal<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T?) = set(value)

/**
 * Usage:
 *
 * ```
 * private val threadLocalInstance by threadLocal { mutableMapOf<Locale, NumberFormat>() }
 * ```
 *
 * Non-nullable version:
 *
 * ```
 * private val threadLocalInstance by byThreadLocal { mutableMapOf<Locale, NumberFormat>() }
 * ```
 */
fun <T> threadLocal(factory: () -> T): ThreadLocal<T> = object : ThreadLocal<T>() {
    override fun initialValue(): T = factory()
}

/**
 * ```
 * private val threadLocalInstance by byThreadLocal { mutableMapOf<Locale, NumberFormat>() }
 * ```
 *
 * ```
 * private val threadLocalInstance by lazy { byThreadLocal { mutableMapOf<Locale, NumberFormat>() } }.by()
 * ```
 */
fun <T> byThreadLocal(factory: () -> T) = threadLocal(factory).by().orElse { factory() }

fun <T> ThreadLocal<T>.by() = object : ReadWriteProperty<Any?, T?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = this@by.getValue(thisRef, property)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) = this@by.setValue(thisRef, property, value)
}