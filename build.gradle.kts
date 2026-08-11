plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.20" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.dokka") version "1.9.10"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0" apply false
}

allprojects {
    group = "com.github.RePropertyX"
    version = "1.1.0"
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.ExperimentalStdlibApi"
            )
        }
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.register<Copy>("copyDocs") {
    dependsOn("dokkaHtmlMultiModule")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(layout.buildDirectory.dir("dokka/htmlMultiModule"))
    into(file("docs/api"))
}

