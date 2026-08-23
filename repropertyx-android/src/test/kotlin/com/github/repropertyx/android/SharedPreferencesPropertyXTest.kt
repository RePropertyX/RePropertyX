package com.github.repropertyx.android

import android.content.SharedPreferences
import com.github.repropertyx.orElse
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SharedPreferencesPropertyXTest {

    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        mockPrefs = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
    }

    @Test
    fun testSharedPreferencesEditorBatchMutation() {
        mockEditor.apply {
            var username: String? by byString()
            var age: Int by byInt("user_age", default = 18)

            username = "Andrew"
            age = 25
        }.apply()

        verify(mockEditor).putString("username", "Andrew")
        verify(mockEditor).putInt("user_age", 25)
        verify(mockEditor).apply()
    }

    @Test
    fun testSharedPreferencesEditorExtensionPropertyBatchMutation() {
        mockPrefs.edit().apply {
            username = "Andrew"
            userAge = 25
        }.apply()

        verify(mockEditor).putString("username", "Andrew")
        verify(mockEditor).putInt("user_age", 25)
        verify(mockEditor).apply()
    }

    @Test
    fun testSharedPreferencesEditorScopeDiskReadBeforeMutation() {
        `when`(mockPrefs.contains("username")).thenReturn(true)
        `when`(mockPrefs.getString("username", null)).thenReturn("OldUser")

        mockPrefs.batch {
            var username: String? by byString()
            org.junit.Assert.assertEquals("OldUser", username)

            username = "NewUser"
            org.junit.Assert.assertEquals("NewUser", username)
        }

        verify(mockEditor).putString("username", "NewUser")
        verify(mockEditor).apply()
    }
}

private var SharedPreferences.Editor.username: String? by bySharedPreferenceEditorString()
private var SharedPreferences.Editor.userAge: Int by bySharedPreferenceEditorInt { "user_age" }.orElse { 18 }
