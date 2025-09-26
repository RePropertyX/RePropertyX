# Compose runtime rules
-keep class androidx.compose.runtime.** { *; }
-keep interface androidx.compose.runtime.** { *; }

# Keep RePropertyX Compose classes
-keep class com.github.repropertyx.compose.** { *; }
-keep interface com.github.repropertyx.compose.** { *; }

# Keep property delegate methods
-keepclassmembers class * {
    ** getValue(**, kotlin.reflect.KProperty);
    ** setValue(**, kotlin.reflect.KProperty, **);
}

# Keep MutableState implementations
-keep class * implements androidx.compose.runtime.MutableState { *; }
-keep class * implements androidx.compose.runtime.State { *; }