plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bp22intel.edgesentinel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bp22intel.edgesentinel"
        minSdk = 26
        targetSdk = 35
        versionCode = 68
        versionName = "2.0.68"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room schema export for migrations
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        // NDK: build for ARM and x86 (emulator) targets
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }

        // Native DIAG helper build
        externalNativeBuild {
            cmake {
                cppFlags += ""
                arguments += "-DANDROID_STL=none"
            }
        }
    }

    // CMake configuration for the native DIAG helper library
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("edge-sentinel-release.jks")
            storePassword = "EdgeSentinel2026!"
            keyAlias = "edge-sentinel"
            keyPassword = "EdgeSentinel2026!"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // AndroidX Core
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    // Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.service)

    // Navigation
    implementation(libs.navigation.compose)

    // Hilt - Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room - Local Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // WorkManager - Background Processing
    implementation(libs.workmanager.runtime.ktx)
    implementation(libs.workmanager.hilt)
    ksp(libs.hilt.compiler)

    // DataStore - Preferences
    implementation(libs.datastore.preferences)

    // Splash Screen
    implementation(libs.splashscreen)

    // Glance - App Widgets
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    // OpenStreetMap (osmdroid) - Threat Map view
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
