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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Converts any ReadWriteProperty into a MutableState.
 * Changes to the MutableState will be reflected in the underlying property delegate.
 */
fun <T> ReadWriteProperty<Any?, T>.asMutableState(
    initialValue: T
): ReadWriteProperty<Any?, MutableState<T>> = object : ReadWriteProperty<Any?, MutableState<T>> {
    private var mutableState: MutableState<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<T> {
        if (mutableState == null) {
            val currentValue = try {
                this@asMutableState.getValue(thisRef, property)
            } catch (e: Exception) {
                initialValue
            }
            mutableState = StatefulProperty(currentValue) { newValue ->
                this@asMutableState.setValue(thisRef, property, newValue)
            }
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<T>) {
        mutableState = value
        this@asMutableState.setValue(thisRef, property, value.value)
    }
}

/**
 * A MutableState implementation that syncs changes back to a property delegate.
 */
private class StatefulProperty<T>(
    initialValue: T,
    private val onValueChanged: (T) -> Unit
) : MutableState<T> {
    private var _value by mutableStateOf(initialValue)

    override var value: T
        get() = _value
        set(newValue) {
            if (_value != newValue) {
                _value = newValue
                onValueChanged(newValue)
            }
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

/**
 * Converts any ReadWriteProperty into a read-only State.
 */
fun <T> ReadWriteProperty<Any?, T>.asState(
    initialValue: T
): ReadWriteProperty<Any?, State<T>> = object : ReadWriteProperty<Any?, State<T>> {
    private var state: State<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): State<T> {
        if (state == null) {
            val currentValue = try {
                this@asState.getValue(thisRef, property)
            } catch (e: Exception) {
                initialValue
            }
            state = StatefulReadOnlyProperty(currentValue)
        }
        return state!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: State<T>) {
        state = value
        this@asState.setValue(thisRef, property, value.value)
    }
}

/**
 * A read-only State implementation.
 */
private class StatefulReadOnlyProperty<T>(
    initialValue: T
) : State<T> {
    private var _value by mutableStateOf(initialValue)

    override val value: T get() = _value

    fun updateValue(newValue: T) {
        _value = newValue
    }
}