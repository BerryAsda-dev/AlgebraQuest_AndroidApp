plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation("io.ktor:ktor-client-core:2.3.11")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:2.3.11")
            implementation("androidx.activity:activity-compose:1.9.0")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
            implementation(compose.uiTooling)
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.11")
        }
    }
}

android {
    namespace = "com.example.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}