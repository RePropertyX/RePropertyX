plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    namespace = "com.github.repropertyx.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    lint {
        checkTestSources = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

dependencies {
    implementation(project(":repropertyx"))
    implementation(project(":repropertyx-android"))

    implementation("androidx.core:core-ktx:1.12.0")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            afterEvaluate {
                from(components["release"])
                artifact(javadocJar)
            }

            pom {
                name.set("repropertyx-compose-android")
                description.set("Compose extensions for repropertyx with MutableState and SharedPreferences bindings")
                url.set("https://github.com/repropertyx/repropertyx")

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("yongjhih")
                        name.set("Yongjhih Chen")
                        email.set("yongjhih@gmail.com")
                    }
                }
            }
        }
    }
}