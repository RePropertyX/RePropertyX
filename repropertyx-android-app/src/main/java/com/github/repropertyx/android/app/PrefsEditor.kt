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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.github.repropertyx.android.byBoolean
import com.github.repropertyx.android.byFloat
import com.github.repropertyx.android.byInt
import com.github.repropertyx.android.byLong
import com.github.repropertyx.android.bySharedPreferenceString
import com.github.repropertyx.android.byString

/**
 * Demo class showing MutableState and SharedPreferences PropertyX binding.
 * Changes to the MutableState are automatically persisted to SharedPreferences.
 */
class PrefsEditor(prefs: SharedPreferences): SharedPreferences by prefs {
    //var SharedPreferences.username: MutableState<String?> by byMutableStateTyped<SharedPreferences, String?>(bySharedPreferenceString())
    // String preference with MutableState binding
    var username: String? by byString()

    // Int preference for user age
    var userAge: Int? by byInt()

    // Boolean preference for notifications
    var notificationsEnabled: Boolean? by byBoolean()

    // Float preference for volume level
    var volumeLevel: Float? by byFloat()

    // Long preference for last login timestamp
    var lastLoginTime: Long? by byLong()

    // Custom key transformation example
    var themeMode: String? by byString { "theme_$it" }

    // Another custom key example with prefix
    var maxRetries: Int? by byInt { "network_max_retries" }

    /**
     * Demonstrates programmatic value changes that are automatically persisted.
     */
    fun setDefaultValues() {
        username = "DefaultUser"
        userAge = 25
        notificationsEnabled = true
        volumeLevel = 0.8f
        lastLoginTime = System.currentTimeMillis()
        themeMode = "dark"
        maxRetries = 3
    }

    /**
     * Clear all preferences by setting them to null.
     */
    fun clearAll() {
        username = null
        userAge = null
        notificationsEnabled = null
        volumeLevel = null
        lastLoginTime = null
        themeMode = null
        maxRetries = null
    }

    /**
     * Get currents as a formatted string for display.
     */
    fun getCurrentValuesString(): String {
        return buildString {
            appendLine("Current Preferences:")
            appendLine("Username: ${username}")
            appendLine("Age: ${userAge}")
            appendLine("Notifications: ${notificationsEnabled}")
            appendLine("Volume: ${volumeLevel}")
            appendLine("Last Login: ${lastLoginTime}")
            appendLine("Theme: ${themeMode}")
            appendLine("Max Retries: ${maxRetries}")
        }
    }

    /**
     * Direct SharedPreferences access to show that changes are persisted.
     */
    fun getDirectFromPrefs(): String {
        return buildString {
            appendLine("Direct from SharedPreferences:")
            appendLine("Username: ${getString("username", null)}")
            appendLine("Age: ${if (contains("userAge")) getInt("userAge", 0) else null}")
            appendLine("Notifications: ${if (contains("notificationsEnabled")) getBoolean("notificationsEnabled", false) else null}")
            appendLine("Volume: ${if (contains("volumeLevel")) getFloat("volumeLevel", 0f) else null}")
            appendLine("Last Login: ${if (contains("lastLoginTime")) getLong("lastLoginTime", 0L) else null}")
            appendLine("Theme: ${getString("theme_themeMode", null)}")
            appendLine("Max Retries: ${if (contains("network_max_retries")) getInt("network_max_retries", 0) else null}")
        }
    }
}