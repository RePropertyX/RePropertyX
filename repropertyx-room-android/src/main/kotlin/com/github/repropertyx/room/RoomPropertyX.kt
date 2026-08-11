package com.github.repropertyx.room

import androidx.room.RoomDatabase
import com.github.repropertyx.propertyOf
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

/**
 * Creates a ReadWriteProperty delegate linked to Room Database query and update operations.
 * Operations are executed using runBlocking for synchronous property access.
 */
fun <T, V> RoomDatabase.byRoomProperty(
    get: RoomDatabase.() -> V,
    set: RoomDatabase.(V) -> Unit
): ReadWriteProperty<T, V> = propertyOf(
    get = { runBlocking { this@byRoomProperty.get() } },
    set = { value -> runBlocking { this@byRoomProperty.set(value) } }
)

/**
 * Creates a ReadOnlyProperty delegate linked to a Room Database query operation.
 */
fun <T, V> RoomDatabase.byRoomQuery(
    query: RoomDatabase.() -> V
): ReadOnlyProperty<T, V> = ReadOnlyProperty { _, _ ->
    runBlocking { this@byRoomQuery.query() }
}

/**
 * Creates a ReadWriteProperty delegate linked to a DAO getter and setter.
 */
fun <R, V> R.byDaoProperty(
    get: R.() -> V,
    set: R.(V) -> Unit
): ReadWriteProperty<Any?, V> = propertyOf(
    get = { runBlocking { this@byDaoProperty.get() } },
    set = { value -> runBlocking { this@byDaoProperty.set(value) } }
)

/**
 * Creates a ReadOnlyProperty delegate linked to a DAO query method.
 */
fun <R, V> R.byDaoQuery(
    query: R.() -> V
): ReadOnlyProperty<Any?, V> = ReadOnlyProperty { _, _ ->
    runBlocking { this@byDaoQuery.query() }
}
