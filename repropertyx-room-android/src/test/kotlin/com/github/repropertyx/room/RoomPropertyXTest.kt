package com.github.repropertyx.room

import com.github.repropertyx.orElse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomPropertyXTest {

    private lateinit var fakePrefStore: FakeRoomPrefStore

    @Before
    fun setUp() {
        fakePrefStore = FakeRoomPrefStore()
    }

    @Test
    fun `RoomPreferenceStore delegates byString and byInt to Room table`() {
        var username: String? by fakePrefStore.byString("user_name")
        var score: Int by fakePrefStore.byInt("user_score", default = 0)

        assertNull(username)
        assertEquals(0, score)

        username = "Alice"
        score = 100

        assertEquals("Alice", username)
        assertEquals(100, score)
    }

    @Test
    fun `RoomPreferenceStore works with orElse fallback`() {
        var theme: String by fakePrefStore.byString("app_theme").orElse { "System" }

        assertEquals("System", theme)

        theme = "Dark"
        assertEquals("Dark", theme)
    }

    @Test
    fun `byRoomState provides non-blocking reads and async writes`() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val roomFlow = MutableStateFlow("LIGHT")
        var dbUpdatedValue = ""

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        var currentTheme: String by byRoomState(
            queryFlow = roomFlow,
            initialValue = "LIGHT",
            scope = testScope,
            dispatcher = testDispatcher,
            onUpdate = { dbUpdatedValue = it }
        )

        assertEquals("LIGHT", currentTheme)

        currentTheme = "DARK"
        assertEquals("DARK", currentTheme)
        assertEquals("DARK", dbUpdatedValue)

        roomFlow.value = "CUSTOM"
        assertEquals("CUSTOM", currentTheme)
    }
}

class FakeRoomPrefStore : RoomPreferenceStore {
    private val map = mutableMapOf<String, String>()

    override fun getPreference(key: String): String? = map[key]

    override fun setPreference(key: String, value: String?) {
        if (value == null) {
            map.remove(key)
        } else {
            map[key] = value
        }
    }
}
