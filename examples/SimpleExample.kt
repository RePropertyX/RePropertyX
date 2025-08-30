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
