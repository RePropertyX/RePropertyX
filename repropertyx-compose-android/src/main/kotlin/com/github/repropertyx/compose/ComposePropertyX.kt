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
import androidx.compose.runtime.mutableStateOf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A MutableState implementation that automatically syncs changes to SharedPreferences.
 */
private class PreferenceMutableState<T>(
    initialValue: T,
    private val thisRef: Any?,
    private val property: KProperty<*>,
    private val delegate: ReadWriteProperty<Any?, T>
) : MutableState<T> {
    private val _state = mutableStateOf(initialValue)

    override var value: T
        get() = _state.value
        set(newValue) {
            if (_state.value != newValue) {
                _state.value = newValue
                // Automatically sync to SharedPreferences
                delegate.setValue(thisRef, property, newValue)
            }
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

/**
 * Creates a MutableState that is backed by any property delegate.
 * Changes to the MutableState will be persisted via the delegate.
 */
fun <T> byMutableState(
    delegate: ReadWriteProperty<Any?, T>
): ReadWriteProperty<Any?, MutableState<T>> = object : ReadWriteProperty<Any?, MutableState<T>> {
    private var mutableState: MutableState<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<T> {
        if (mutableState == null) {
            val initialValue = delegate.getValue(thisRef, property)
            mutableState = PreferenceMutableState(initialValue, thisRef, property, delegate)
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<T>) {
        mutableState = value
        delegate.setValue(thisRef, property, value.value)
    }
}

