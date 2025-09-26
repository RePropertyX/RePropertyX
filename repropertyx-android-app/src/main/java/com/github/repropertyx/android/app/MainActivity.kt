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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.repropertyx.android.byBoolean
import com.github.repropertyx.android.byInt
import com.github.repropertyx.android.bySharedPreference
import com.github.repropertyx.android.bySharedPreferenceBoolean
import com.github.repropertyx.android.bySharedPreferenceInt
import com.github.repropertyx.android.bySharedPreferenceString
import com.github.repropertyx.android.byString
import com.github.repropertyx.orElse

class MainActivity : ComponentActivity() {

    private lateinit var prefs: SharedPreferences

    var SharedPreferences.username by bySharedPreferenceString{ "username" }.orElse { "" }
    var SharedPreferences.age by bySharedPreferenceInt { "age" }.orElse { 0 }
    var SharedPreferences.enabled by bySharedPreferenceBoolean { "enabled" }.orElse { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("repropertyx_demo", Context.MODE_PRIVATE)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PrefsEditor(
                        initialName = prefs.username,
                        initialAge = prefs.age,
                        initialEnabled = prefs.enabled,
                        onSave = { name, age, enabled ->
                            prefs.username = name
                            prefs.age = age
                            prefs.enabled = enabled
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                        },
                        onLoad = {
                            Triple(prefs.username, prefs.age, prefs.enabled)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrefsEditor(
    initialName: String,
    initialAge: Int,
    initialEnabled: Boolean,
    onSave: (String, Int, Boolean) -> Unit,
    onLoad: () -> Triple<String, Int, Boolean>,
) {
    val (name, setName) = remember { mutableStateOf(initialName) }
    val (ageText, setAgeText) = remember { mutableStateOf(initialAge.toString()) }
    val (enabled, setEnabled) = remember { mutableStateOf(initialEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "RePropertyX SharedPreferences Editor", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        TextField(value = name, onValueChange = setName, label = { Text("Username") })
        Spacer(Modifier.height(8.dp))

        TextField(value = ageText, onValueChange = setAgeText, label = { Text("Age") })
        Spacer(Modifier.height(8.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Enabled")
            Switch(checked = enabled, onCheckedChange = setEnabled)
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val age = ageText.toIntOrNull() ?: 0
            onSave(name, age, enabled)
        }) { Text("Save") }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val (n, a, e) = onLoad()
            setName(n)
            setAgeText(a.toString())
            setEnabled(e)
        }) { Text("Load") }
    }
}
