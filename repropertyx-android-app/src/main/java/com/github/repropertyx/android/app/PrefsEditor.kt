/*
 * Copyright 2025 RePropertyX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.repropertyx.android.app

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import com.github.repropertyx.android.byBoolean
import com.github.repropertyx.android.byFloat
import com.github.repropertyx.android.byInt
import com.github.repropertyx.android.byLong
import com.github.repropertyx.android.byString
import com.github.repropertyx.compose.byMutableState

/**
 * Demo class showing MutableState and SharedPreferences PropertyX binding.
 * Changes to the MutableState are automatically persisted to SharedPreferences.
 */
class PrefsEditor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("repropertyx_demo", Context.MODE_PRIVATE)

    // String preference with MutableState binding
    var username: MutableState<String?> by byMutableState(prefs.byString())

    // Int preference for user age
    var userAge: MutableState<Int?> by byMutableState(prefs.byInt())

    // Boolean preference for notifications
    var notificationsEnabled: MutableState<Boolean?> by byMutableState(prefs.byBoolean())

    // Float preference for volume level
    var volumeLevel: MutableState<Float?> by byMutableState(prefs.byFloat())

    // Long preference for last login timestamp
    var lastLoginTime: MutableState<Long?> by byMutableState(prefs.byLong())

    // Custom key transformation example
    var themeMode: MutableState<String?> by byMutableState(prefs.byString { "theme_$it" })

    // Another custom key example with prefix
    var maxRetries: MutableState<Int?> by byMutableState(prefs.byInt { "network_max_retries" })

    /**
     * Demonstrates programmatic value changes that are automatically persisted.
     */
    fun setDefaultValues() {
        username.value = "DefaultUser"
        userAge.value = 25
        notificationsEnabled.value = true
        volumeLevel.value = 0.8f
        lastLoginTime.value = System.currentTimeMillis()
        themeMode.value = "dark"
        maxRetries.value = 3
    }

    /**
     * Clear all preferences by setting them to null.
     */
    fun clearAll() {
        username.value = null
        userAge.value = null
        notificationsEnabled.value = null
        volumeLevel.value = null
        lastLoginTime.value = null
        themeMode.value = null
        maxRetries.value = null
    }

    /**
     * Get current values as a formatted string for display.
     */
    fun getCurrentValuesString(): String {
        return buildString {
            appendLine("Current Preferences:")
            appendLine("Username: ${username.value}")
            appendLine("Age: ${userAge.value}")
            appendLine("Notifications: ${notificationsEnabled.value}")
            appendLine("Volume: ${volumeLevel.value}")
            appendLine("Last Login: ${lastLoginTime.value}")
            appendLine("Theme: ${themeMode.value}")
            appendLine("Max Retries: ${maxRetries.value}")
        }
    }

    /**
     * Direct SharedPreferences access to show that changes are persisted.
     */
    fun getDirectFromPrefs(): String {
        return buildString {
            appendLine("Direct from SharedPreferences:")
            appendLine("Username: ${prefs.getString("username", null)}")
            appendLine("Age: ${if (prefs.contains("userAge")) prefs.getInt("userAge", 0) else null}")
            appendLine("Notifications: ${if (prefs.contains("notificationsEnabled")) prefs.getBoolean("notificationsEnabled", false) else null}")
            appendLine("Volume: ${if (prefs.contains("volumeLevel")) prefs.getFloat("volumeLevel", 0f) else null}")
            appendLine("Last Login: ${if (prefs.contains("lastLoginTime")) prefs.getLong("lastLoginTime", 0L) else null}")
            appendLine("Theme: ${prefs.getString("theme_themeMode", null)}")
            appendLine("Max Retries: ${if (prefs.contains("network_max_retries")) prefs.getInt("network_max_retries", 0) else null}")
        }
    }
}