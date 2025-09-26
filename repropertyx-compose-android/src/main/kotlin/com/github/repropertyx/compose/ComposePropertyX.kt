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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.github.repropertyx.android.byBoolean
import com.github.repropertyx.android.byFloat
import com.github.repropertyx.android.byInt
import com.github.repropertyx.android.byLong
import com.github.repropertyx.android.byString
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a MutableState that is backed by a SharedPreferences property delegate.
 * Changes to the MutableState will be persisted to SharedPreferences and vice versa.
 */
fun SharedPreferences.byMutableState(
    delegate: ReadWriteProperty<Any?, String?>
): ReadWriteProperty<Any?, MutableState<String?>> = object : ReadWriteProperty<Any?, MutableState<String?>> {
    private var mutableState: MutableState<String?>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<String?> {
        if (mutableState == null) {
            val initialValue = delegate.getValue(thisRef, property)
            mutableState = mutableStateOf(initialValue)
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<String?>) {
        mutableState = value
        delegate.setValue(thisRef, property, value.value)
    }
}

/**
 * Creates a MutableState<String?> backed by SharedPreferences string value.
 */
fun SharedPreferences.byMutableStringState(
    key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<Any?, MutableState<String?>> {
    val delegate = byString(key)
    return byMutableState(delegate)
}

/**
 * Creates a MutableState<Int?> backed by SharedPreferences int value.
 */
fun SharedPreferences.byMutableIntState(
    key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<Any?, MutableState<Int?>> = object : ReadWriteProperty<Any?, MutableState<Int?>> {
    private var mutableState: MutableState<Int?>? = null
    private val delegate = byInt(key)

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<Int?> {
        if (mutableState == null) {
            val initialValue = delegate.getValue(thisRef, property)
            mutableState = mutableStateOf(initialValue)
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<Int?>) {
        mutableState = value
        delegate.setValue(thisRef, property, value.value)
    }
}

/**
 * Creates a MutableState<Boolean?> backed by SharedPreferences boolean value.
 */
fun SharedPreferences.byMutableBooleanState(
    key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<Any?, MutableState<Boolean?>> = object : ReadWriteProperty<Any?, MutableState<Boolean?>> {
    private var mutableState: MutableState<Boolean?>? = null
    private val delegate = byBoolean(key)

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<Boolean?> {
        if (mutableState == null) {
            val initialValue = delegate.getValue(thisRef, property)
            mutableState = mutableStateOf(initialValue)
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<Boolean?>) {
        mutableState = value
        delegate.setValue(thisRef, property, value.value)
    }
}

/**
 * Creates a MutableState<Long?> backed by SharedPreferences long value.
 */
fun SharedPreferences.byMutableLongState(
    key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<Any?, MutableState<Long?>> = object : ReadWriteProperty<Any?, MutableState<Long?>> {
    private var mutableState: MutableState<Long?>? = null
    private val delegate = byLong(key)

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<Long?> {
        if (mutableState == null) {
            val initialValue = delegate.getValue(thisRef, property)
            mutableState = mutableStateOf(initialValue)
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<Long?>) {
        mutableState = value
        delegate.setValue(thisRef, property, value.value)
    }
}

/**
 * Creates a MutableState<Float?> backed by SharedPreferences float value.
 */
fun SharedPreferences.byMutableFloatState(
    key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<Any?, MutableState<Float?>> = object : ReadWriteProperty<Any?, MutableState<Float?>> {
    private var mutableState: MutableState<Float?>? = null
    private val delegate = byFloat(key)

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableState<Float?> {
        if (mutableState == null) {
            val initialValue = delegate.getValue(thisRef, property)
            mutableState = mutableStateOf(initialValue)
        }
        return mutableState!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableState<Float?>) {
        mutableState = value
        delegate.setValue(thisRef, property, value.value)
    }
}