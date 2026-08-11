plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    namespace = "com.github.repropertyx.android"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.interpolator:interpolator:1.0.0")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    //implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Use JUnit4; avoid kotlin-test to prevent capability conflicts during lint

    // Mockito for testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.1")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("org.mockito:mockito-core:5.7.0")
    androidTestImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

    // Android test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            afterEvaluate {
                from(components["release"])
                artifact(javadocJar)
            }
            
            pom {
                name.set("repropertyx-android")
                description.set("Android-specific extensions for repropertyx")
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
