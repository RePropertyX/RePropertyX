package com.github.repropertyx.room

import com.github.repropertyx.orElse
import com.github.repropertyx.propertyOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Interface for Room DAOs that manage key-value preference stores.
 * Implementing this interface allows DAOs to delegate properties directly to Room key-value tables.
 */
interface RoomPreferenceStore {
    fun getPreference(key: String): String?
    fun setPreference(key: String, value: String?)
}

/**
 * Delegates a property to a Room Key-Value preference table with custom type conversion.
 */
fun <T> RoomPreferenceStore.byRoomPreference(
    key: String,
    default: T? = null,
    to: (String) -> T,
    from: (T) -> String
): ReadWriteProperty<Any?, T?> = propertyOf(
    get = {
        getPreference(key)?.let(to) ?: default
    },
    set = { value ->
        setPreference(key, value?.let(from))
    }
)

// Preference convenience delegates
fun RoomPreferenceStore.byString(key: String): ReadWriteProperty<Any?, String?> =
    byRoomPreference(key, to = { it }, from = { it })

fun RoomPreferenceStore.byString(key: String, default: String): ReadWriteProperty<Any?, String> =
    byString(key).orElse { default }

fun RoomPreferenceStore.byInt(key: String): ReadWriteProperty<Any?, Int?> =
    byRoomPreference(key, to = { it.toInt() }, from = { it.toString() })

fun RoomPreferenceStore.byInt(key: String, default: Int): ReadWriteProperty<Any?, Int> =
    byInt(key).orElse { default }

fun RoomPreferenceStore.byBoolean(key: String): ReadWriteProperty<Any?, Boolean?> =
    byRoomPreference(key, to = { it.toBoolean() }, from = { it.toString() })

fun RoomPreferenceStore.byBoolean(key: String, default: Boolean): ReadWriteProperty<Any?, Boolean> =
    byBoolean(key).orElse { default }

fun RoomPreferenceStore.byLong(key: String): ReadWriteProperty<Any?, Long?> =
    byRoomPreference(key, to = { it.toLong() }, from = { it.toString() })

fun RoomPreferenceStore.byLong(key: String, default: Long): ReadWriteProperty<Any?, Long> =
    byLong(key).orElse { default }

fun RoomPreferenceStore.byFloat(key: String): ReadWriteProperty<Any?, Float?> =
    byRoomPreference(key, to = { it.toFloat() }, from = { it.toString() })

fun RoomPreferenceStore.byFloat(key: String, default: Float): ReadWriteProperty<Any?, Float> =
    byFloat(key).orElse { default }

/**
 * Creates an asynchronous, reactive property delegate backed by a Room Flow query and a suspend update function.
 *
 * Reads return the latest cached state without blocking the UI thread.
 * Writes update local state immediately and dispatch Room database updates asynchronously on Dispatchers.IO.
 *
 * @param queryFlow Room Flow observing database changes
 * @param initialValue Initial state before the first Flow emission
 * @param scope CoroutineScope (e.g. viewModelScope) used to collect Flow and launch background writes
 * @param onUpdate Suspend lambda performing the Room DB insert/update operation
 */
fun <V> byRoomState(
    queryFlow: Flow<V>,
    initialValue: V,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    onUpdate: suspend (V) -> Unit
): ReadWriteProperty<Any?, V> = object : ReadWriteProperty<Any?, V> {
    private var currentValue: V = initialValue

    init {
        scope.launch(dispatcher) {
            queryFlow.collect { newValue ->
                currentValue = newValue
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V = currentValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        currentValue = value
        scope.launch(dispatcher) {
            onUpdate(value)
        }
    }
}
