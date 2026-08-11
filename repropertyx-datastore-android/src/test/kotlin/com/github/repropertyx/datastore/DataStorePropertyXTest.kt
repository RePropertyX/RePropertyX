package com.github.repropertyx.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.github.repropertyx.orElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DataStorePropertyXTest {

    private lateinit var fakeDataStore: FakeDataStore

    @Before
    fun setUp() {
        fakeDataStore = FakeDataStore()
    }

    @Test
    fun `byString delegates get and set to DataStore`() {
        var username: String? by fakeDataStore.byString("username")

        assertNull(username)

        username = "Alice"
        assertEquals("Alice", username)

        username = "Bob"
        assertEquals("Bob", username)
    }

    @Test
    fun `byString with fallback default returns default when null`() {
        var nickname: String by fakeDataStore.byString("nickname").orElse { "Guest" }

        assertEquals("Guest", nickname)

        nickname = "Charlie"
        assertEquals("Charlie", nickname)
    }

    @Test
    fun `byInt delegates get and set to DataStore`() {
        var userAge: Int by fakeDataStore.byInt("user_age", 18)

        assertEquals(18, userAge)

        userAge = 25
        assertEquals(25, userAge)
    }

    @Test
    fun `byBoolean delegates get and set to DataStore`() {
        var isDarkMode: Boolean by fakeDataStore.byBoolean("dark_mode", false)

        assertEquals(false, isDarkMode)

        isDarkMode = true
        assertEquals(true, isDarkMode)
    }
}

class FakeDataStore : DataStore<Preferences> {
    private val prefsState = MutableStateFlow<Preferences>(emptyPreferences())

    override val data: Flow<Preferences> = prefsState

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val newPrefs = transform(prefsState.value)
        prefsState.value = newPrefs
        return newPrefs
    }
}
