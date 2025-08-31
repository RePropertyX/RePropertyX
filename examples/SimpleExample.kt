/*
 * Copyright 2024 yongjhih
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

import com.github.yongjhih.delegatex.*

/**
 * Simple example demonstrating delegate-ktx usage
 */
fun main() {
    println("=== delegate-ktx Simple Example ===\n")
    
    // Create a simple settings class
    val settings = SimpleSettings()
    
    // Test basic functionality
    settings.username = "john_doe"
    println("Username: ${settings.username}")
    
    settings.age = 25
    println("Age: ${settings.age}")
    
    settings.isEnabled = true
    println("Enabled: ${settings.isEnabled}")
    
    println("\n=== Example Complete ===")
}

class SimpleSettings {
    // Basic delegates without operators
    var username: String by stringDelegate("")
    var age: Int by intDelegate(0)
    var isEnabled: Boolean by booleanDelegate(false)
}
