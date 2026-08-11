package com.github.repropertyx.android

import androidx.lifecycle.SavedStateHandle
import com.github.repropertyx.orElse
import com.github.repropertyx.propertyOf
import kotlin.properties.ReadWriteProperty

/**
 * Delegates a property to Android Jetpack [SavedStateHandle] for Process Death state preservation.
 */
fun <V> SavedStateHandle.byProperty(key: String): ReadWriteProperty<Any?, V?> = propertyOf(
    get = { get<V>(key) },
    set = { value -> set(key, value) }
)

/**
 * Delegates a property to Android Jetpack [SavedStateHandle] with a default fallback value.
 */
fun <V : Any> SavedStateHandle.byProperty(key: String, default: V): ReadWriteProperty<Any?, V> =
    byProperty<V>(key).orElse { default }
