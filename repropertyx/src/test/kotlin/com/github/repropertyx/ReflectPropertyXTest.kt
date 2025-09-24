package com.github.repropertyx

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

open class User {
    private var _name: String? = null
}

class UserChild(
    @JvmField
    val lastName: String? = null,
) : User() {
    @JvmField
    var displayName: String? = null
}

var User.name: String? by byDeclaredField { "_name" }

var UserChild.displayNameByField: String? by byField { "displayName" }
val UserChild.lastNameByReadField: String? by byReadField { "lastName" }


class ReflectPropertyXTest {

    @Test
    fun `test byDeclaredField`() {
        val user = User()
        assertNull(user.name)

        user.name = "Hello"
        assertEquals("Hello", user.name)

        user.name = null
        assertNull(user.name)
    }

    @Test
    fun `test byField`() {
        val user = UserChild()
        assertNull(user.displayNameByField)

        user.displayNameByField = "Hello"
        assertEquals("Hello", user.displayNameByField)

        user.displayNameByField = null
        assertNull(user.displayNameByField)
    }

    @Test
    fun `test byReadField`() {
        val user = UserChild(lastName = "Yongjhih")

        assertEquals("Yongjhih", user.lastNameByReadField)
    }
}