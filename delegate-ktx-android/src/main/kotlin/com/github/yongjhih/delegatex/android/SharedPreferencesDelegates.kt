/*
 * Copyright 2024 yongjhih
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

package com.github.yongjhih.delegatex.android

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <T> bySharedPreference(
    crossinline getter: SharedPreferences.(String) -> T?,
    crossinline setter: SharedPreferences.Editor.(String, T?) -> SharedPreferences.Editor,
    noinline key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<SharedPreferences, T?> =
    object : ReadWriteProperty<SharedPreferences, T?> {
        override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): T? =
            thisRef.getter(key?.invoke(thisRef, property.name) ?: property.name)
        override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: T?) =
            thisRef.edit { setter(key?.invoke(thisRef, property.name) ?: property.name, value) }
    }

inline fun <T> SharedPreferences.by(
    crossinline getter: SharedPreferences.(String) -> T?,
    crossinline setter: SharedPreferences.Editor.(String, T?) -> SharedPreferences.Editor,
    noinline key: (SharedPreferences.(String) -> String)? = null
): ReadWriteProperty<Any, T?> =
    object : ReadWriteProperty<Any, T?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T? =
            getter(key?.invoke(this@by, property.name) ?: property.name)
        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) =
            edit { setter(key?.invoke(this@by, property.name) ?: property.name, value) }
    }

fun SharedPreferences.byString(key: (SharedPreferences.(String) -> String)? = null) =
    by(SharedPreferences::getStringOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceString(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getStringOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceInt(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getIntOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byInt(key: (SharedPreferences.(String) -> String)? = null) =
    by(SharedPreferences::getIntOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceBoolean(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getBooleanOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byBoolean(key: (SharedPreferences.(String) -> String)? = null) =
    by(SharedPreferences::getBooleanOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceStringSet(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getStringSetOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byStringSet(key: (SharedPreferences.(String) -> String)? = null) =
    by(SharedPreferences::getStringSetOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceFloat(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getFloatOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byFloat(key: (SharedPreferences.(String) -> String)? = null) =
    by(SharedPreferences::getFloatOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceLong(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getLongOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byLong(key: (SharedPreferences.(String) -> String)? = null) =
    by(SharedPreferences::getLongOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.getStringOrNull(key: String): String? =
    if (contains(key)) getString(key, null)
    else null

fun SharedPreferences.Editor.put(key: String, value: String?): SharedPreferences.Editor =
    putString(key, value)

fun SharedPreferences.getIntOrNull(key: String): Int? =
    if (contains(key)) getInt(key, 0)
    else null

fun SharedPreferences.Editor.put(key: String, value: Int?): SharedPreferences.Editor =
    if (value == null) remove(key)
    else putInt(key, value)


fun SharedPreferences.getStringSetOrNull(key: String): Set<String>? =
    if (contains(key)) getStringSet(key, null)
    else null


fun SharedPreferences.getLongOrNull(key: String): Long? =
    if (contains(key)) getLong(key, 0)
    else null

fun SharedPreferences.Editor.put(key: String, value: Long?): SharedPreferences.Editor =
    if (value == null) remove(key)
    else putLong(key, value)

fun SharedPreferences.getBooleanOrNull(key: String): Boolean? =
    if (contains(key)) getBoolean(key, false)
    else null

fun SharedPreferences.Editor.put(key: String, value: Boolean?): SharedPreferences.Editor =
    if (value == null) remove(key)
    else putBoolean(key, value)

fun SharedPreferences.Editor.put(key: String, value: Set<String>?): SharedPreferences.Editor =
    putStringSet(key, value)

fun SharedPreferences.getFloatOrNull(key: String): Float? =
    if (contains(key)) getFloat(key, 0f)
    else null

fun SharedPreferences.Editor.put(key: String, value: Float?): SharedPreferences.Editor =
    if (value == null) remove(key)
    else putFloat(key, value)