import android.content.Context
import com.github.yongjhih.delegatex.*
import com.github.yongjhih.delegatex.android.*

/**
 * Android example demonstrating delegate-ktx with SharedPreferences
 */
class AndroidSettingsExample(context: Context) {
    
    private val prefs = context.getDefaultSharedPreferences()
    
    // Basic SharedPreferences with null fallback
    var username: String by prefs.stringPreference("username")
        .or { "guest_$it" }
    
    // Int preference with validation
    var age: Int by prefs.intPreference("age", 0)
        .validate { require(it >= 0 && it <= 150) }
    
    // Boolean preference with logging
    var isEnabled: Boolean by prefs.booleanPreference("is_enabled", false)
        .log { old, new -> println("Feature enabled: $old → $new") }
    
    // Long preference for timestamps
    var lastLoginTime: Long by prefs.longPreference("last_login", 0L)
        .log { old, new -> println("Last login: ${java.util.Date(old)} → ${java.util.Date(new)}") }
    
    // Float preference for ratings
    var userRating: Float by prefs.floatPreference("rating", 0.0f)
        .validate { require(it >= 0.0f && it <= 5.0f) }
    
    // Nullable string preference
    var optionalNote: String? by prefs.nullableStringPreference("note")
    
    // Complex validation with multiple rules
    var email: String by prefs.stringPreference("email", "")
        .validate { 
            require(it.contains("@")) { "Email must contain @ symbol" }
            require(it.contains(".")) { "Email must contain domain" }
            require(it.length >= 5) { "Email too short" }
        }
        .log { old, new -> println("Email updated: $old → $new") }
    
    // Automatic key generation (uses property name as key)
    var autoKeyProperty: String? by prefs.preference()
    
    // Once-only setting for user ID
    var userId: String by prefs.stringPreference("user_id", "")
        .once()
    
    // Cached preference for expensive operations
    private val cache = mutableMapOf<String, Any>()
    var expensiveValue: String by prefs.stringPreference("expensive", "")
        .cacheIn(cache)
        .log { old, new -> println("Expensive value cached: $old → $new") }
}

/**
 * Example usage in an Android Activity or Fragment
 */
fun exampleUsage(context: Context) {
    val settings = AndroidSettingsExample(context)
    
    // Set values
    settings.username = "john_doe"
    settings.age = 25
    settings.isEnabled = true
    settings.lastLoginTime = System.currentTimeMillis()
    settings.userRating = 4.5f
    settings.email = "john@example.com"
    settings.userId = "user123"
    
    // Read values
    println("Username: ${settings.username}")
    println("Age: ${settings.age}")
    println("Enabled: ${settings.isEnabled}")
    println("Last login: ${java.util.Date(settings.lastLoginTime)}")
    println("Rating: ${settings.userRating}")
    println("Email: ${settings.email}")
    println("User ID: ${settings.userId}")
    
    // Test validation
    try {
        settings.age = -5 // This will throw an exception
    } catch (e: IllegalArgumentException) {
        println("Age validation failed: ${e.message}")
    }
    
    try {
        settings.email = "invalid" // This will throw an exception
    } catch (e: IllegalArgumentException) {
        println("Email validation failed: ${e.message}")
    }
    
    // Test once-only behavior
    settings.userId = "user456" // This will be ignored
    println("User ID (should still be user123): ${settings.userId}")
}
