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

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.remember
import com.github.repropertyx.cast
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
inline fun <reified V> rememberProperty(vararg keys: Any, crossinline block: @DisallowComposableCalls () -> ReadWriteProperty<Any?, V>): ReadWriteProperty<Any?, V> =
    remember(keys) { block() }.cast()

@Composable
inline fun <reified V> rememberProperty(key: Any, crossinline block: @DisallowComposableCalls () -> ReadWriteProperty<Any?, V>): ReadWriteProperty<Any?, V> =
    remember(key) { block() }.cast()

@Composable
fun <V> rememberProperty(
    disposable: DisposableEffectScope.(updateValue: () -> Unit) -> DisposableEffectResult = { _ -> onDispose { } },
    key: Any? = null,
    block: @DisallowComposableCalls () -> ReadWriteProperty<Any?, V>,
): ReadWriteProperty<Any?, V> =
    mutableStateOf(block(), key, disposable).cast()

@Composable
fun <V> mutableStateOf(
    property: ReadWriteProperty<Any?, V>,
    key: Any? = null,
    disposable: DisposableEffectScope.(updateValue: () -> Unit) -> DisposableEffectResult = { _ -> onDispose { } }
): ReadWriteProperty<Any?, V> {
    val internalState = remember { androidx.compose.runtime.mutableStateOf<V?>(null) }
    val initialized = remember { androidx.compose.runtime.mutableStateOf(false) }
    val currentPropRef = remember { arrayOf<KProperty<*>?>(null) }

    val result = remember(key, property) {
        object : ReadWriteProperty<Any?, V> {
            override fun getValue(thisRef: Any?, prop: KProperty<*>): V {
                currentPropRef[0] = prop

                if (!initialized.value) {
                    val initialValue = property.getValue(thisRef, prop)
                    internalState.value = initialValue
                    initialized.value = true
                }

                @Suppress("UNCHECKED_CAST")
                return internalState.value as V
            }

            override fun setValue(thisRef: Any?, prop: KProperty<*>, value: V) {
                // Update both internal state and underlying property
                internalState.value = value
                property.setValue(thisRef, prop, value)
            }
        }
    }

    // Set up listener using the provided setup function
    DisposableEffect(key, property) {
        val updateValue = {
            currentPropRef[0]?.let { prop ->
                try {
                    val newValue = property.getValue(key, prop)
                    // Only update if the value actually changed
                    if (internalState.value != newValue) {
                        internalState.value = newValue
                    }
                } catch (e: Exception) {
                    // Ignore if there's an issue reading the property
                }
            }
        }

        disposable(updateValue)
    }

    return result
}

@Composable
fun SharedPreferences.changesComposed(): DisposableEffectScope.(updateValue: () -> Unit) -> DisposableEffectResult = { updateValue ->
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        updateValue()
    }

    registerOnSharedPreferenceChangeListener(listener)

    onDispose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}