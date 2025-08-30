import com.github.yongjhih.delegatex.*

/**
 * Basic example demonstrating delegate-ktx usage
 */
fun main() {
    println("=== delegate-ktx Basic Example ===\n")
    
    // Create a settings class with composable delegates
    val settings = UserSettings()
    
    // Test null fallback
    settings.username = null
    println("Username (with fallback): ${settings.username}")
    
    // Test type mapping
    settings.age = "25"
    println("Age (mapped from string): ${settings.age}")
    
    // Test validation
    try {
        settings.email = "invalid-email"
    } catch (e: IllegalArgumentException) {
        println("Email validation failed: ${e.message}")
    }
    
    settings.email = "valid@email.com"
    println("Email (valid): ${settings.email}")
    
    // Test logging
    settings.loginCount = 5
    settings.loginCount = 10
    
    // Test once-only setting
    settings.userId = "user123"
    settings.userId = "user456" // This will be ignored
    println("User ID (once-only): ${settings.userId}")
    
    println("\n=== Example Complete ===")
}

class UserSettings {
    // Null fallback - provides a default value when null
    var username: String by stringDelegate(null)
        .or { "guest_$it" }
    
    // Type mapping - converts between string and int
    var age: Int by stringDelegate("0")
        .map(to = { it.toInt() }, from = { it.toString() })
    
    // Validation - ensures email contains @ symbol
    var email: String by stringDelegate("")
        .validate { require(it.contains("@")) }
    
    // Logging - tracks changes to the property
    var loginCount: Int by intDelegate(0)
        .log { old, new -> println("Login count changed: $old → $new") }
    
    // Once-only - can only be set once
    var userId: String by stringDelegate("")
        .once()
    
    // Exception handling - provides fallback on errors
    var safeValue: String by stringDelegate("")
        .catch { _, _ -> "default" }
}
