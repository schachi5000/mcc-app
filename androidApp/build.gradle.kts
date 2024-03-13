plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "net.schacher.mcc"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "net.schacher.mcc"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 4
        versionName = "1.0.0-alpha"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            versionNameSuffix = " (debug)"
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("../androidApp/src/androidMain/signing/release.keystore")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
            storePassword = System.getenv("STORE_PASSWORD")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    // Kamel Image causes issue if these resource are not excluded
    packagingOptions.resources.excludes.add("META-INF/versions/9/*")

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    dependencies {
        debugImplementation(libs.androidx.compose.compiler)
        debugImplementation(libs.androidx.compose.tooling)
        implementation(libs.androidx.compose.preview)
    }
}