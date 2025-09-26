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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun <V> ReadWriteProperty<Any?, V>.asMutableState(): ReadWriteProperty<Any?, V> {
    val originalProperty = this@asMutableState
    val compositionLocalState = remember { mutableStateOf<V?>(null) }
    val initialized = remember { mutableStateOf(false) }

    return remember(originalProperty) {
        object : ReadWriteProperty<Any?, V> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): V {
                if (!initialized.value) {
                    val initialValue = originalProperty.getValue(thisRef, property)
                    compositionLocalState.value = initialValue
                    initialized.value = true
                }
                @Suppress("UNCHECKED_CAST")
                return compositionLocalState.value as V
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
                originalProperty.setValue(thisRef, property, value)
                compositionLocalState.value = value
            }
        }
    }
}

fun <V> mutableStateOf(property: ReadWriteProperty<Any?, V>): ReadWriteProperty<Any?, V> {
    val internalState = mutableStateOf<V?>(null)
    val initialized = mutableStateOf(false)

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