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

package com.github.repropertyx.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/*
fun <V> mutableStateOf(
    property: ReadWriteProperty<Any?, V>
): MutableState<V> = object : MutableState<V> {
    override var value: V by property
    override fun component1(): V = value
    override fun component2(): (V) -> Unit = { value = it }
}

fun <V> mutableStateOf(
    property: MutableState<V>
): MutableState<V> = object : MutableState<V> {
    override var value: V by property
    override fun component1(): V = value
    override fun component2(): (V) -> Unit = { value = it }
}
*/

fun <V> ReadWriteProperty<Any?, V>.asMutableState(): ReadWriteProperty<Any?, V> {
    return com.github.repropertyx.compose.mutableStateOf(this@asMutableState)
}

fun <V> mutableStateOf(property: ReadWriteProperty<Any?, V>): ReadWriteProperty<Any?, V> {
    val internalState = androidx.compose.runtime.mutableStateOf<V?>(null)
    val initialized = androidx.compose.runtime.mutableStateOf(false)

    return object : ReadWriteProperty<Any?, V> {
        override fun getValue(thisRef: Any?, prop: KProperty<*>): V {
            if (!initialized.value) {
                val initialValue = property.getValue(thisRef, prop)
                internalState.value = initialValue
                initialized.value = true
            }
            @Suppress("UNCHECKED_CAST")
            return internalState.value as V
        }

        override fun setValue(thisRef: Any?, prop: KProperty<*>, value: V) {
            property.setValue(thisRef, prop, value)
            internalState.value = value
        }
    }
}

@Composable
fun <V> ReadWriteProperty<Any?, V>.rememberAsMutableState(): ReadWriteProperty<Any?, V> {
    return remember { this@rememberAsMutableState.asMutableState() }
}

inline fun <reified V> ReadWriteProperty<Any?, V>.cast(): ReadWriteProperty<Any?, V> = this@cast