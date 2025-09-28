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

@file:OptIn(ExperimentalFoundationApi::class)

package com.github.repropertyx.android.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import com.github.repropertyx.android.animated
import com.github.repropertyx.android.byBoolean
import com.github.repropertyx.android.byFloat
import com.github.repropertyx.android.byInt
import com.github.repropertyx.android.byLong
import com.github.repropertyx.android.byString
import com.github.repropertyx.android.setOnCheckedChangeListener
import com.github.repropertyx.compose.changesComposed
import com.github.repropertyx.compose.rememberPropertyState
import com.github.repropertyx.distinctUntilChanged
import com.github.repropertyx.onEach
import com.github.repropertyx.propertyOf
import com.github.repropertyx.readWriteProperty
import kotlin.math.roundToInt

// ViewPropertyX extension properties for animated View properties
var View.animatedTranslationX: Float by readWriteProperty<View, Float>(
    get = { translationX },
    set = { translationX = it }
).distinctUntilChanged().animated().distinctUntilChanged()

var View.animatedTranslationY: Float by readWriteProperty<View, Float>(
    get = { translationY },
    set = { translationY = it }
).distinctUntilChanged().animated().distinctUntilChanged()

var View.animatedScaleX: Float by readWriteProperty<View, Float>(
    get = { scaleX },
    set = { scaleX = it }
).animated { _, _ ->
    duration = 500
}

var View.animatedScaleY: Float by readWriteProperty<View, Float>(
    get = { scaleY },
    set = { scaleY = it }
).animated { _, _ ->
    duration = 500
}

var View.animatedAlpha: Float by readWriteProperty<View, Float>(
    get = { alpha },
    set = { alpha = it }
).animated { _, _ ->
    duration = 600
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val prefs = getSharedPreferences("demo", Context.MODE_PRIVATE)
                    val prefsEditor = remember { PrefsEditor(prefs) }
                    MainScreenWithTabs(prefs, prefsEditor)
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun MainScreenWithTabs(prefs: SharedPreferences, prefsEditor: PrefsEditor) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val tabs = listOf("PreferencesPropertyX", "ViewPropertyX")

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PrefsEditorScreen(prefs, prefsEditor)
                1 -> ViewPropertyXDemoScreen()
            }
        }
    }
}

@Composable
private fun PrefsEditorScreen(prefs: SharedPreferences, prefsEditor: PrefsEditor) {
    val context = androidx.compose.ui.platform.LocalContext.current
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

@Composable
private fun ViewPropertyXDemoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "ViewPropertyX Demo",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Animated View Properties with Property Delegation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Animated Translation Demo
        AnimatedTranslationDemo()

        // Animated Scaling Demo
        AnimatedScalingDemo()

        // Animated Alpha Demo
        AnimatedAlphaDemo()

        // CheckBox Property Binding Demo
        CheckBoxPropertyDemo()

        // Info Text
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "💡 All animations are powered by ViewPropertyX with property delegation:\n" +
                        "• .animated() - Smooth ValueAnimator transitions\n" +
                        "• .distinctUntilChanged() - Prevents redundant updates\n" +
                        "• .onEach() - Side effects and observation\n" +
                        "• Property delegation - Clean, reactive syntax",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun AnimatedTranslationDemo() {
    var translationX by remember { mutableFloatStateOf(0f) }
    var translationY by remember { mutableFloatStateOf(0f) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(
                text = "Animated Translation",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text = "🚀 Animated View"
                        textSize = 18f
                        setPadding(32, 32, 32, 32)
                        setBackgroundColor(0xFF2196F3.toInt())
                        setTextColor(0xFFFFFFFF.toInt())

                        // Set initial position
                        translationX = 0f
                        translationY = 0f
                    }
                },
                update = { view ->
                    view.animatedTranslationX = translationX
                    view.animatedTranslationY = translationY
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Translation X: ${translationX.roundToInt()}px")
            Slider(
                value = translationX,
                onValueChange = { translationX = it },
                valueRange = -200f..200f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Translation Y: ${translationY.roundToInt()}px")
            Slider(
                value = translationY,
                onValueChange = { translationY = it },
                valueRange = -100f..100f,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        translationX = 0f
                        translationY = 0f
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset Position")
                }

                OutlinedButton(
                    onClick = {
                        translationX = (-200..200).random().toFloat()
                        translationY = (-100..100).random().toFloat()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Random Position")
                }
            }
        }
    }
}

@Composable
private fun AnimatedScalingDemo() {
    var scaleX by remember { mutableFloatStateOf(1f) }
    var scaleY by remember { mutableFloatStateOf(1f) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Animated Scaling",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text = "📏 Scale Me"
                        textSize = 20f
                        setPadding(48, 48, 48, 48)
                        setBackgroundColor(0xFF4CAF50.toInt())
                        setTextColor(0xFFFFFFFF.toInt())
                        gravity = android.view.Gravity.CENTER

                        // Set initial scale
                        scaleX = 1f
                        scaleY = 1f
                    }
                },
                update = { view ->
                    view.animatedScaleX = scaleX
                    view.animatedScaleY = scaleY
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Scale X: ${String.format("%.2f", scaleX)}")
            Slider(
                value = scaleX,
                onValueChange = { scaleX = it },
                valueRange = 0.5f..2f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Scale Y: ${String.format("%.2f", scaleY)}")
            Slider(
                value = scaleY,
                onValueChange = { scaleY = it },
                valueRange = 0.5f..2f,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        scaleX = 1f
                        scaleY = 1f
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset Scale")
                }

                OutlinedButton(
                    onClick = {
                        scaleX = (50..200).random() / 100f
                        scaleY = (50..200).random() / 100f
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Random Scale")
                }
            }
        }
    }
}

@Composable
private fun AnimatedAlphaDemo() {
    var alpha by remember { mutableFloatStateOf(1f) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Animated Alpha",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text = "👻 Fade Me"
                        textSize = 20f
                        setPadding(48, 48, 48, 48)
                        setBackgroundColor(0xFF9C27B0.toInt())
                        setTextColor(0xFFFFFFFF.toInt())
                        gravity = android.view.Gravity.CENTER

                        // Set initial alpha
                        alpha = 1f
                    }
                },
                update = { view ->
                    view.animatedAlpha = alpha
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Alpha: ${String.format("%.2f", alpha)}")
            Slider(
                value = alpha,
                onValueChange = { alpha = it },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { alpha = if (alpha > 0.5f) 0f else 1f },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Toggle Visibility")
                }

                OutlinedButton(
                    onClick = { alpha = (0..100).random() / 100f },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Random Alpha")
                }
            }
        }
    }
}

@Composable
private fun CheckBoxPropertyDemo() {
    var isChecked by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Property Binding with CheckBox",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AndroidView(
                factory = { context ->
                    CheckBox(context).apply {
                        text = "Bind me with property delegation"
                        isChecked = false
                    }
                },
                update = { checkBox ->
                    // Create property delegation for checkbox state
                    var checkboxState by propertyOf(
                        get = { checkBox.isChecked },
                        set = {
                            checkBox.isChecked = it
                            println("CheckBox state changed: $it")
                        }
                    ).onEach { checked ->
                        println("Property changed: $checked")
                    }

                    // Update the checkbox state (this will trigger the property setter)
                    checkboxState = isChecked
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Current State: ${if (isChecked) "✅ Checked" else "❌ Unchecked"}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isChecked = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Check")
                }

                OutlinedButton(
                    onClick = { isChecked = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Uncheck")
                }
            }
        }
    }
}
