package com.github.yongjhih.delegatex.android

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun SharedPreferences.byString(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<Any, String?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String? =
            getStringOrNull(key?.invoke(this@byString, property.name) ?: property.name)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) =
            edit { put(key?.invoke(this@byString, property.name) ?: property.name, value) }
    }

fun bySharedPreferenceString(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<SharedPreferences, String?> {
    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): String? =
        thisRef.getStringOrNull(key?.invoke(thisRef, property.name) ?: property.name)

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: String?) =
        thisRef.edit { put(key?.invoke(thisRef, property.name) ?: property.name, value) }
}

fun bySharedPreferenceInt(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<SharedPreferences, Int?> {
    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): Int? =
        thisRef.getIntOrNull(key?.invoke(thisRef, property.name) ?: property.name)

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: Int?) =
        thisRef.edit { put(key?.invoke(thisRef, property.name) ?: property.name, value) }
}

fun SharedPreferences.byInt(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<Any, Int?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Int? =
            getIntOrNull(key?.invoke(this@byInt, property.name) ?: property.name)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int?) =
            edit { put(key?.invoke(this@byInt, property.name) ?: property.name, value) }
    }

fun bySharedPreferenceBoolean(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<SharedPreferences, Boolean?> {
    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): Boolean? =
        thisRef.getBooleanOrNull(key?.invoke(thisRef, property.name) ?: property.name)

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: Boolean?) =
        thisRef.edit { put(key?.invoke(thisRef, property.name) ?: property.name, value) }
}

fun SharedPreferences.byBoolean(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<Any, Boolean?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean? =
            getBooleanOrNull(key?.invoke(this@byBoolean, property.name) ?: property.name)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean?) =
            edit { put(key?.invoke(this@byBoolean, property.name) ?: property.name, value) }
    }

fun bySharedPreferenceStringSet(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<SharedPreferences, Set<String>?> {
        override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): Set<String>? =
            thisRef.getStringSet(key?.invoke(thisRef, property.name) ?: property.name, null)

        override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: Set<String>?) =
            thisRef.edit { put(key?.invoke(thisRef, property.name) ?: property.name, value) }
    }

fun SharedPreferences.byStringSet(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<Any, Set<String>?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Set<String>? =
            getStringSetOrNull(key?.invoke(this@byStringSet, property.name) ?: property.name)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>?) =
            edit { put(key?.invoke(this@byStringSet, property.name) ?: property.name, value) }
    }

fun bySharedPreferenceFloat(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<SharedPreferences, Float?> {
    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): Float? =
        thisRef.getFloatOrNull(key?.invoke(thisRef, property.name) ?: property.name)

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: Float?) =
        thisRef.edit { put(key?.invoke(thisRef, property.name) ?: property.name, value) }
}

fun SharedPreferences.byFloat(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<Any, Float?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Float? =
            getFloatOrNull(key?.invoke(this@byFloat, property.name) ?: property.name)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Float?) =
            edit { put(key?.invoke(this@byFloat, property.name) ?: property.name, value) }
    }

fun bySharedPreferenceLong(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<SharedPreferences, Long?> {
    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): Long? =
        thisRef.getLongOrNull(key?.invoke(thisRef, property.name) ?: property.name)

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: Long?) =
        thisRef.edit { put(key?.invoke(thisRef, property.name) ?: property.name, value) }
}

fun SharedPreferences.byLong(key: (SharedPreferences.(String) -> String)? = null) =
    object : ReadWriteProperty<Any, Long?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Long? =
            getLongOrNull(key?.invoke(this@byLong, property.name) ?: property.name)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Long?) =
            edit { put(key?.invoke(this@byLong, property.name) ?: property.name, value) }
    }

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