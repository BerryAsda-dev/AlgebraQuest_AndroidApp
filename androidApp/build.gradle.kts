plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.example.androidApp"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.androidApp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material3)
}