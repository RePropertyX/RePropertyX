package com.github.repropertyx.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.repropertyx.orElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a ReadWriteProperty delegate backed by Jetpack DataStore<Preferences> and a Preferences.Key.
 */
fun <T> DataStore<Preferences>.byPreference(
    key: Preferences.Key<T>,
    default: T? = null
): ReadWriteProperty<Any?, T?> = object : ReadWriteProperty<Any?, T?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return runBlocking {
            data.first()[key] ?: default
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        runBlocking {
            edit { preferences ->
                if (value == null) {
                    preferences.remove(key)
                } else {
                    preferences[key] = value
                }
            }
        }
    }
}

// String Overloads
fun DataStore<Preferences>.byString(key: Preferences.Key<String>): ReadWriteProperty<Any?, String?> =
    byPreference(key)

fun DataStore<Preferences>.byString(key: Preferences.Key<String>, default: String): ReadWriteProperty<Any?, String> =
    byPreference(key).orElse { default }

fun DataStore<Preferences>.byString(name: String): ReadWriteProperty<Any?, String?> =
    byString(stringPreferencesKey(name))

fun DataStore<Preferences>.byString(name: String, default: String): ReadWriteProperty<Any?, String> =
    byString(stringPreferencesKey(name), default)

// Int Overloads
fun DataStore<Preferences>.byInt(key: Preferences.Key<Int>): ReadWriteProperty<Any?, Int?> =
    byPreference(key)

fun DataStore<Preferences>.byInt(key: Preferences.Key<Int>, default: Int): ReadWriteProperty<Any?, Int> =
    byPreference(key).orElse { default }

fun DataStore<Preferences>.byInt(name: String): ReadWriteProperty<Any?, Int?> =
    byInt(intPreferencesKey(name))

fun DataStore<Preferences>.byInt(name: String, default: Int): ReadWriteProperty<Any?, Int> =
    byInt(intPreferencesKey(name), default)

// Boolean Overloads
fun DataStore<Preferences>.byBoolean(key: Preferences.Key<Boolean>): ReadWriteProperty<Any?, Boolean?> =
    byPreference(key)

fun DataStore<Preferences>.byBoolean(key: Preferences.Key<Boolean>, default: Boolean): ReadWriteProperty<Any?, Boolean> =
    byPreference(key).orElse { default }

fun DataStore<Preferences>.byBoolean(name: String): ReadWriteProperty<Any?, Boolean?> =
    byBoolean(booleanPreferencesKey(name))

fun DataStore<Preferences>.byBoolean(name: String, default: Boolean): ReadWriteProperty<Any?, Boolean> =
    byBoolean(booleanPreferencesKey(name), default)

// Long Overloads
fun DataStore<Preferences>.byLong(key: Preferences.Key<Long>): ReadWriteProperty<Any?, Long?> =
    byPreference(key)

fun DataStore<Preferences>.byLong(key: Preferences.Key<Long>, default: Long): ReadWriteProperty<Any?, Long> =
    byPreference(key).orElse { default }

fun DataStore<Preferences>.byLong(name: String): ReadWriteProperty<Any?, Long?> =
    byLong(longPreferencesKey(name))

fun DataStore<Preferences>.byLong(name: String, default: Long): ReadWriteProperty<Any?, Long> =
    byLong(longPreferencesKey(name), default)

// Float Overloads
fun DataStore<Preferences>.byFloat(key: Preferences.Key<Float>): ReadWriteProperty<Any?, Float?> =
    byPreference(key)

fun DataStore<Preferences>.byFloat(key: Preferences.Key<Float>, default: Float): ReadWriteProperty<Any?, Float> =
    byPreference(key).orElse { default }

fun DataStore<Preferences>.byFloat(name: String): ReadWriteProperty<Any?, Float?> =
    byFloat(floatPreferencesKey(name))

fun DataStore<Preferences>.byFloat(name: String, default: Float): ReadWriteProperty<Any?, Float> =
    byFloat(floatPreferencesKey(name), default)

// Double Overloads
fun DataStore<Preferences>.byDouble(key: Preferences.Key<Double>): ReadWriteProperty<Any?, Double?> =
    byPreference(key)

fun DataStore<Preferences>.byDouble(key: Preferences.Key<Double>, default: Double): ReadWriteProperty<Any?, Double> =
    byPreference(key).orElse { default }

fun DataStore<Preferences>.byDouble(name: String): ReadWriteProperty<Any?, Double?> =
    byDouble(doublePreferencesKey(name))

fun DataStore<Preferences>.byDouble(name: String, default: Double): ReadWriteProperty<Any?, Double> =
    byDouble(doublePreferencesKey(name), default)

/**
 * Returns a Flow observing value changes for the specified Preferences.Key in DataStore.
 */
fun <T> DataStore<Preferences>.asFlow(key: Preferences.Key<T>, default: T? = null): Flow<T?> =
    data.map { preferences -> preferences[key] ?: default }

/**
 * Returns a Flow observing value changes for the specified string key in DataStore.
 */
fun DataStore<Preferences>.asStringFlow(name: String, default: String? = null): Flow<String?> =
    asFlow(stringPreferencesKey(name), default)

/**
 * Returns a Flow observing value changes for the specified int key in DataStore.
 */
fun DataStore<Preferences>.asIntFlow(name: String, default: Int? = null): Flow<Int?> =
    asFlow(intPreferencesKey(name), default)

/**
 * Returns a Flow observing value changes for the specified boolean key in DataStore.
 */
fun DataStore<Preferences>.asBooleanFlow(name: String, default: Boolean? = null): Flow<Boolean?> =
    asFlow(booleanPreferencesKey(name), default)
