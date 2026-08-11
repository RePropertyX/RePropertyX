package com.github.repropertyx.room

import com.github.repropertyx.orElse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RoomPropertyXTest {

    private lateinit var fakeDao: FakeUserDao

    @Before
    fun setUp() {
        fakeDao = FakeUserDao()
    }

    @Test
    fun `byDaoProperty delegates get and set to DAO`() {
        var userScore: Int by fakeDao.byDaoProperty(
            get = { getScore() },
            set = { setScore(it) }
        )

        assertEquals(0, userScore)

        userScore = 100
        assertEquals(100, userScore)
    }

    @Test
    fun `byDaoQuery provides read-only access to DAO query`() {
        val userName: String by fakeDao.byDaoQuery { getName() }

        assertEquals("Alice", userName)
    }

    @Test
    fun `byDaoProperty works seamlessly with ReProperty operators`() {
        var status: String by fakeDao.byDaoProperty(
            get = { getStatus() },
            set = { setStatus(it) }
        ).orElse { "Offline" }

        assertEquals("Offline", status)

        status = "Online"
        assertEquals("Online", status)
    }
}

class FakeUserDao {
    private var score: Int = 0
    private var name: String = "Alice"
    private var status: String? = null

    fun getScore(): Int = score
    fun setScore(value: Int) { score = value }

    fun getName(): String = name

    fun getStatus(): String? = status
    fun setStatus(value: String?) { status = value }
}
