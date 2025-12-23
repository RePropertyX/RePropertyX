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

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Usage:
 *
 * ```
 * var activity: Activity? by byWeakReference()
 * ```
 */
fun <T> byWeakReference(value: T? = null, queue: ReferenceQueue<in T?>? = null) =
    object : ReadWriteProperty<Any, T?> {
        private var ref: WeakReference<T?> = WeakReference(value, queue)
        override fun getValue(thisRef: Any, property: KProperty<*>): T? = ref.get()
        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            ref = WeakReference(value, queue)
        }
    }

operator fun <T> WeakReference<T>.getValue(thisRef: Any, property: KProperty<*>): T? = get()

fun <T, V: AutoCloseable> ReadWriteProperty<T, V>.closable(): ReadWriteProperty<T, V> =
    onEachBefore { it.close() }
