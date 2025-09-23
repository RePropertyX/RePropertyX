pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "repropertyx"
include(":repropertyx")
project(":repropertyx").projectDir = file("delegate-ktx")
include(":repropertyx-android")
project(":repropertyx-android").projectDir = file("delegate-ktx-android")
