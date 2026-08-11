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
project(":repropertyx").projectDir = file("repropertyx")
include(":repropertyx-android")
project(":repropertyx-android").projectDir = file("repropertyx-android")
include(":repropertyx-android-app")
include(":repropertyx-compose-android")
include(":repropertyx-datastore-android")
project(":repropertyx-datastore-android").projectDir = file("repropertyx-datastore-android")
