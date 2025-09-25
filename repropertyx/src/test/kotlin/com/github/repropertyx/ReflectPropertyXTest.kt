package com.github.repropertyx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

open class Person {
    private var _name: String = "John Doe"
    private var _age: Int = 25
    protected var _email: String = "john@example.com"
    @JvmField var publicField: String = "public data"
}

class User : Person() {
    private var _username: String = "johndoe"
    private var _role: String = "USER"
    @JvmField var score: Double = 0.0
}

// Extension properties using the new API
var Person.name: String by byDeclaredField<Person, String> { "_name" }
var Person.age: Int by byDeclaredField<Person, Int> { "_age" }
val Person.nameReadOnly: String by byDeclaredField<Person, String> { "_name" }.readOnly()
var Person.safeName: String? by byDeclaredField<Person, String> { "_name" }.orNull()
val Person.safeAge: Int? by byDeclaredField<Person, Int> { "_age" }.orNull().readOnly()

// User accessing own fields
var User.username: String by byDeclaredField<User, String> { "_username" }
var User.role: String by byDeclaredField<User, String> { "_role" }

// User accessing parent class fields
var User.parentName: String by byFirstDeclaredField<User, String> { "_name" }
var User.parentAge: Int by byFirstDeclaredField<User, Int> { "_age" }
val User.safeParentName: String? by byFirstDeclaredField<User, String> { "_name" }
    .orNull { e, _ -> if (e is NoSuchFieldException) null else throw e }
    .readOnly()

// Public field access
var User.scoreByField: Double by byField<User, Double> { "score" }
var Person.publicFieldAccess: String by byField<Person, String> { "publicField" }

class PropertyDelegateTest {

    private lateinit var person: Person
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        person = Person()
        user = User()
    }

    // Person Property Delegate Tests

    @Test
    fun testPrivateNameAccess() {
        // Given
        assertEquals("John Doe", person.name)

        // When
        person.name = "Jane Smith"

        // Then
        assertEquals("Jane Smith", person.name)
        assertEquals("Jane Smith", person.nameReadOnly)
    }

    @Test
    fun testPrivateAgeAccess() {
        // Given
        assertEquals(25, person.age)

        // When
        person.age = 30

        // Then
        assertEquals(30, person.age)
    }

    @Test
    fun testSafeNullAccess() {
        // Given
        assertNotNull(person.safeName)
        assertEquals("John Doe", person.safeName)

        // When
        person.name = "Safe Name"

        // Then
        assertEquals("Safe Name", person.safeName)
        assertEquals(Integer.valueOf(25), person.safeAge)
    }

    @Test
    fun testPublicFieldAccess() {
        // Given
        assertEquals("public data", person.publicFieldAccess)

        // When
        person.publicFieldAccess = "modified public data"

        // Then
        assertEquals("modified public data", person.publicFieldAccess)
    }

    @Test
    fun testReadOnlyDelegate() {
        // Given
        person.name = "Original Name"

        // When & Then
        assertEquals("Original Name", person.nameReadOnly)
        // Note: person.nameReadOnly = "..." would be a compilation error
    }

    // User Property Delegate Tests

    @Test
    fun testUserOwnFields() {
        // Test username
        assertEquals("johndoe", user.username)
        user.username = "janesmith"
        assertEquals("janesmith", user.username)

        // Test role
        assertEquals("USER", user.role)
        user.role = "ADMIN"
        assertEquals("ADMIN", user.role)
    }

    @Test
    fun testUserPublicField() {
        // Given
        assertEquals(0.0, user.scoreByField, 0.001)

        // When
        user.scoreByField = 95.5

        // Then
        assertEquals(95.5, user.scoreByField, 0.001)
    }

    // Inheritance Property Delegate Tests

    @Test
    fun testInheritanceAccess() {
        // Test accessing parent's _name field
        assertEquals("John Doe", user.parentName)
        user.parentName = "User Parent Name"
        assertEquals("User Parent Name", user.parentName)

        // Test accessing parent's _age field
        assertEquals(25, user.parentAge)
        user.parentAge = 35
        assertEquals(35, user.parentAge)
    }

    @Test
    fun testSafeInheritanceAccess() {
        // Given
        assertNotNull(user.safeParentName)
        assertEquals("John Doe", user.safeParentName)

        // When
        user.parentName = "Safe Parent Name"

        // Then
        assertEquals("Safe Parent Name", user.safeParentName)
    }

    // Error Handling Tests

    val Person.nonExistentField: String? by byDeclaredField<Person, String> { "_nonExistent" }.orNull()

    @Test
    fun testNoSuchFieldHandling() {
        // This would normally throw NoSuchFieldException, but orNull handles it

        assertNull(person.nonExistentField)
    }

    @Test
    fun testCustomErrorHandling() {
        val customErrorField: String? by person.by<Person, String>(byDeclaredField { "_nonExistent" })
            .orNull { e, property ->
                when (e) {
                    is NoSuchFieldException -> "default_value"
                    else -> throw e
                }
            }


        assertEquals("default_value", customErrorField)
    }

    // Caching Tests

    @Test
    fun testCaching() {
        // Access the same field multiple times
        val name1 = person.name
        val name2 = person.name
        val name3 = person.nameReadOnly

        assertEquals(name1, name2)
        assertEquals(name2, name3)
        assertEquals("John Doe", name1)
    }

    @Test
    fun testNoCaching() {
        // Test with caching disabled
        val nameWithoutCache: String by person.by(byDeclaredField(cache = true) { "_name" })

        assertEquals("John Doe", nameWithoutCache)
        person.name = "Modified"
        assertEquals("Modified", nameWithoutCache)
    }

    // Additional edge case tests

    @Test
    fun testMultipleFieldTypes() {
        // Test different primitive types
        assertEquals("John Doe", person.name) // String
        assertEquals(25, person.age) // Int
        assertEquals(0.0, user.scoreByField, 0.001) // Double
    }

    @Test
    fun testFieldModificationPersistence() {
        // Modify fields and ensure changes persist
        person.name = "Test Name"
        person.age = 40
        user.username = "testuser"
        user.scoreByField = 88.8

        assertEquals("Test Name", person.name)
        assertEquals(40, person.age)
        assertEquals("testuser", user.username)
        assertEquals(88.8, user.scoreByField, 0.001)
    }

    @Test
    fun testNullValueHandling() {
        person.safeName = null
        assertNotNull(person.safeName)

        // Reset to non-null
        person.name = "Reset Name"
        assertEquals("Reset Name", person.safeName)
    }
}
