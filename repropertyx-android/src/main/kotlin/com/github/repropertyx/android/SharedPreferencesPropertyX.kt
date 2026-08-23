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

package com.github.repropertyx.android

import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.repropertyx.by
import com.github.repropertyx.orElse
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a [ReadWriteProperty] delegate for accessing values in [SharedPreferences].
 *
 * @param getter A function to read the value from [SharedPreferences] by key.
 * @param setter A function to write the value to [SharedPreferences.Editor] by key.
 * @param key Optional transformation of the property name into a custom preference key.
 *
 * @return A property delegate bound to [SharedPreferences].
 */
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

fun SharedPreferences.byString(key: (SharedPreferences.(String) -> String)? = null) =
    by(bySharedPreferenceString(key))

fun bySharedPreferenceString(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getStringOrNull, SharedPreferences.Editor::put, key)

fun bySharedPreferenceInt(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getIntOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byInt(key: (SharedPreferences.(String) -> String)? = null) =
    by(bySharedPreferenceInt(key))

fun bySharedPreferenceBoolean(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getBooleanOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byBoolean(key: (SharedPreferences.(String) -> String)? = null) =
    by(bySharedPreferenceBoolean(key))

fun bySharedPreferenceStringSet(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getStringSetOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byStringSet(key: (SharedPreferences.(String) -> String)? = null) =
    by(bySharedPreferenceStringSet(key))

fun bySharedPreferenceFloat(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getFloatOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byFloat(key: (SharedPreferences.(String) -> String)? = null) =
    by(bySharedPreferenceFloat(key))

fun bySharedPreferenceLong(key: (SharedPreferences.(String) -> String)? = null) =
    bySharedPreference(SharedPreferences::getLongOrNull, SharedPreferences.Editor::put, key)

fun SharedPreferences.byLong(key: (SharedPreferences.(String) -> String)? = null) =
    by(bySharedPreferenceLong(key))

fun SharedPreferences.byString(key: String): ReadWriteProperty<SharedPreferences, String?> = byString { key }
fun SharedPreferences.byString(key: String, default: String): ReadWriteProperty<SharedPreferences, String> = byString { key }.orElse { default }

fun SharedPreferences.byInt(key: String): ReadWriteProperty<SharedPreferences, Int?> = byInt { key }
fun SharedPreferences.byInt(key: String, default: Int): ReadWriteProperty<SharedPreferences, Int> = byInt { key }.orElse { default }

fun SharedPreferences.byBoolean(key: String): ReadWriteProperty<SharedPreferences, Boolean?> = byBoolean { key }
fun SharedPreferences.byBoolean(key: String, default: Boolean): ReadWriteProperty<SharedPreferences, Boolean> = byBoolean { key }.orElse { default }

fun SharedPreferences.byLong(key: String): ReadWriteProperty<SharedPreferences, Long?> = byLong { key }
fun SharedPreferences.byLong(key: String, default: Long): ReadWriteProperty<SharedPreferences, Long> = byLong { key }.orElse { default }

fun SharedPreferences.byFloat(key: String): ReadWriteProperty<SharedPreferences, Float?> = byFloat { key }
fun SharedPreferences.byFloat(key: String, default: Float): ReadWriteProperty<SharedPreferences, Float> = byFloat { key }.orElse { default }

/**
 * A scope combining [SharedPreferences] for reading disk values and [SharedPreferences.Editor] for batch writing.
 */
class SharedPreferencesEditorScope(
    val prefs: SharedPreferences,
    val editor: SharedPreferences.Editor
)

/**
 * Executes the given [action] on a [SharedPreferencesEditorScope] with automatic commit or apply.
 */
inline fun SharedPreferences.batch(
    commit: Boolean = false,
    action: SharedPreferencesEditorScope.() -> Unit
) {
    val editor = edit()
    val scope = SharedPreferencesEditorScope(this, editor)
    scope.action()
    if (commit) editor.commit() else editor.apply()
}

inline fun SharedPreferences.editBatch(
    commit: Boolean = false,
    action: SharedPreferencesEditorScope.() -> Unit
) = batch(commit, action)

/**
 * Creates a [ReadWriteProperty] delegate for [SharedPreferencesEditorScope] that reads from [SharedPreferences]
 * when clean, and writes to [SharedPreferences.Editor].
 */
inline fun <T> bySharedPreferenceEditorScope(
    crossinline getter: SharedPreferences.(String) -> T?,
    crossinline setter: SharedPreferences.Editor.(String, T?) -> SharedPreferences.Editor,
    noinline key: (SharedPreferencesEditorScope.(String) -> String)? = null
): ReadWriteProperty<SharedPreferencesEditorScope, T?> =
    object : ReadWriteProperty<SharedPreferencesEditorScope, T?> {
        private var writeOnlyValue: T? = null
        private var isWritten = false

        override fun getValue(thisRef: SharedPreferencesEditorScope, property: KProperty<*>): T? {
            return if (isWritten) {
                writeOnlyValue
            } else {
                val resolvedKey = key?.invoke(thisRef, property.name) ?: property.name
                thisRef.prefs.getter(resolvedKey)
            }
        }

        override fun setValue(thisRef: SharedPreferencesEditorScope, property: KProperty<*>, value: T?) {
            writeOnlyValue = value
            isWritten = true
            val resolvedKey = key?.invoke(thisRef, property.name) ?: property.name
            thisRef.editor.setter(resolvedKey, value)
        }
    }

fun SharedPreferencesEditorScope.byString(key: (SharedPreferencesEditorScope.(String) -> String)? = null): ReadWriteProperty<Any?, String?> =
    by(bySharedPreferenceEditorScopeString(key))

fun bySharedPreferenceEditorScopeString(key: (SharedPreferencesEditorScope.(String) -> String)? = null) =
    bySharedPreferenceEditorScope(SharedPreferences::getStringOrNull, { k, v -> put(k, v) }, key)

fun SharedPreferencesEditorScope.byString(key: String): ReadWriteProperty<Any?, String?> = byString { key }
fun SharedPreferencesEditorScope.byString(key: String, default: String): ReadWriteProperty<Any?, String> = byString { key }.orElse { default }

fun SharedPreferencesEditorScope.byInt(key: (SharedPreferencesEditorScope.(String) -> String)? = null): ReadWriteProperty<Any?, Int?> =
    by(bySharedPreferenceEditorScopeInt(key))

fun bySharedPreferenceEditorScopeInt(key: (SharedPreferencesEditorScope.(String) -> String)? = null) =
    bySharedPreferenceEditorScope(SharedPreferences::getIntOrNull, { k, v -> put(k, v) }, key)

fun SharedPreferencesEditorScope.byInt(key: String): ReadWriteProperty<Any?, Int?> = byInt { key }
fun SharedPreferencesEditorScope.byInt(key: String, default: Int): ReadWriteProperty<Any?, Int> = byInt { key }.orElse { default }

fun SharedPreferencesEditorScope.byBoolean(key: (SharedPreferencesEditorScope.(String) -> String)? = null): ReadWriteProperty<Any?, Boolean?> =
    by(bySharedPreferenceEditorScopeBoolean(key))

fun bySharedPreferenceEditorScopeBoolean(key: (SharedPreferencesEditorScope.(String) -> String)? = null) =
    bySharedPreferenceEditorScope(SharedPreferences::getBooleanOrNull, { k, v -> put(k, v) }, key)

fun SharedPreferencesEditorScope.byBoolean(key: String): ReadWriteProperty<Any?, Boolean?> = byBoolean { key }
fun SharedPreferencesEditorScope.byBoolean(key: String, default: Boolean): ReadWriteProperty<Any?, Boolean> = byBoolean { key }.orElse { default }

/**
 * Creates a [ReadWriteProperty] delegate for mutating values on [SharedPreferences.Editor].
 */
inline fun <T> bySharedPreferenceEditor(
    crossinline setter: SharedPreferences.Editor.(String, T?) -> SharedPreferences.Editor,
    noinline key: (SharedPreferences.Editor.(String) -> String)? = null
): ReadWriteProperty<SharedPreferences.Editor, T?> =
    object : ReadWriteProperty<SharedPreferences.Editor, T?> {
        private var writeOnlyValue: T? = null

        override fun getValue(thisRef: SharedPreferences.Editor, property: KProperty<*>): T? = writeOnlyValue

        override fun setValue(thisRef: SharedPreferences.Editor, property: KProperty<*>, value: T?) {
            writeOnlyValue = value
            thisRef.setter(key?.invoke(thisRef, property.name) ?: property.name, value)
        }
    }

fun SharedPreferences.Editor.byString(key: (SharedPreferences.Editor.(String) -> String)? = null): ReadWriteProperty<Any?, String?> =
    by(bySharedPreferenceEditorString(key))

fun bySharedPreferenceEditorString(key: (SharedPreferences.Editor.(String) -> String)? = null) =
    bySharedPreferenceEditor<String>({ k, v -> put(k, v) }, key)

fun SharedPreferences.Editor.byString(key: String): ReadWriteProperty<Any?, String?> = byString { key }
fun SharedPreferences.Editor.byString(key: String, default: String): ReadWriteProperty<Any?, String> = byString { key }.orElse { default }

fun SharedPreferences.Editor.byInt(key: (SharedPreferences.Editor.(String) -> String)? = null): ReadWriteProperty<Any?, Int?> =
    by(bySharedPreferenceEditorInt(key))

fun bySharedPreferenceEditorInt(key: (SharedPreferences.Editor.(String) -> String)? = null) =
    bySharedPreferenceEditor<Int>({ k, v -> put(k, v) }, key)

fun SharedPreferences.Editor.byInt(key: String): ReadWriteProperty<Any?, Int?> = byInt { key }
fun SharedPreferences.Editor.byInt(key: String, default: Int): ReadWriteProperty<Any?, Int> = byInt { key }.orElse { default }

fun SharedPreferences.Editor.byBoolean(key: (SharedPreferences.Editor.(String) -> String)? = null): ReadWriteProperty<Any?, Boolean?> =
    by(bySharedPreferenceEditorBoolean(key))

fun bySharedPreferenceEditorBoolean(key: (SharedPreferences.Editor.(String) -> String)? = null) =
    bySharedPreferenceEditor<Boolean>({ k, v -> put(k, v) }, key)

fun SharedPreferences.Editor.byBoolean(key: String): ReadWriteProperty<Any?, Boolean?> = byBoolean { key }
fun SharedPreferences.Editor.byBoolean(key: String, default: Boolean): ReadWriteProperty<Any?, Boolean> = byBoolean { key }.orElse { default }

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