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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.repropertyx.android.byBoolean
import com.github.repropertyx.android.byFloat
import com.github.repropertyx.android.byInt
import com.github.repropertyx.android.byLong
import com.github.repropertyx.android.byString
import com.github.repropertyx.compose.changesComposed
import com.github.repropertyx.compose.rememberPropertyState
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val prefs = getSharedPreferences("demo", Context.MODE_PRIVATE)
                    val prefsEditor = remember { PrefsEditor(prefs) }
                    PrefsEditorScreen(prefs, prefsEditor)
                }
            }
        }
    }
}

@Composable
private fun PrefsEditorScreen(prefs: SharedPreferences, prefsEditor: PrefsEditor) {
    //var usernameA: String? by rememberProperty(Unit) { mutableStateOf(prefs.byString()) }
    //var usernameB: String? by rememberProperty(Unit) { prefs.byString() }
    var username: String? by rememberPropertyState(prefs.changesComposed()) { prefs.byString() }
    var userAge: Int? by rememberPropertyState(prefs.changesComposed()) { prefs.byInt() }
    var notificationsEnabled: Boolean? by rememberPropertyState(prefs.changesComposed()) { prefs.byBoolean() }
    var volumeLevel: Float? by rememberPropertyState(prefs.changesComposed()) { prefs.byFloat() }
    var lastLoginTime: Long? by rememberPropertyState(prefs.changesComposed()) { prefs.byLong() }
    var themeMode: String? by rememberPropertyState(prefs.changesComposed()) { prefs.byString { "theme_$it" } }
    var maxRetries: Int? by rememberPropertyState(prefs.changesComposed()) { prefs.byInt { "network_max_retries" } }

    // Without observing prefs changes
    /*
    var username: String? by remember { androidx.compose.runtime.mutableStateOf(null) }
    var userAge: Int? by remember { androidx.compose.runtime.mutableStateOf(null) }
    var notificationsEnabled: Boolean? by remember { androidx.compose.runtime.mutableStateOf(null) }
    var volumeLevel: Float? by remember { androidx.compose.runtime.mutableStateOf(null) }
    var lastLoginTime: Long? by remember { androidx.compose.runtime.mutableStateOf(null) }
    var themeMode: String? by remember { androidx.compose.runtime.mutableStateOf(null) }
    var maxRetries: Int? by remember { androidx.compose.runtime.mutableStateOf(null) }
    */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "RePropertyX Compose Demo",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "MutableState ↔ SharedPreferences Binding",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // User Profile Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = username ?: "",
                    onValueChange = { username = it.takeIf { it.isNotBlank() } },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userAge?.toString() ?: "",
                    onValueChange = { text ->
                        userAge = text.toIntOrNull()
                    },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Settings Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Notifications")
                    Switch(
                        checked = notificationsEnabled ?: false,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Volume Level: ${((volumeLevel ?: 0.5f) * 100).roundToInt()}%")
                Slider(
                    value = volumeLevel ?: 0.5f,
                    onValueChange = { volumeLevel = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = themeMode ?: "",
                    onValueChange = { themeMode = it.takeIf { it.isNotBlank() } },
                    label = { Text("Theme Mode") },
                    placeholder = { Text("light, dark, auto") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { prefsEditor.setDefaultValues() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Set Defaults")
            }

            OutlinedButton(
                onClick = { prefsEditor.clearAll() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear All")
            }
        }

        // Debug Information
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Debug Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "From MutableState:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = prefsEditor.getCurrentValuesString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Direct from SharedPreferences:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = prefsEditor.getDirectFromPrefs(),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Info Text
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "💡 Changes to the UI controls are automatically persisted to SharedPreferences via MutableState binding. No manual save/load needed!",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
