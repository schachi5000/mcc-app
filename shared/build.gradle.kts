plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.10"
    id("com.squareup.sqldelight") version "1.5.5"
    id("dev.icerock.mobile.multiplatform-resources").version("0.23.0")
}


object Versions {
    const val ktor = "2.3.4"
    const val coin = "3.5.0"
    const val sqlDelight = "1.5.5"
    const val moko = "0.16.1"
    const val mokoResources = "0.23.0"
    const val koin = "3.5.0"
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            export("dev.icerock.moko:resources:${Versions.mokoResources}")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("io.insert-koin:koin-core:${Versions.koin}")
                api("io.insert-koin:koin-compose:1.1.0")
                implementation("co.touchlab:kermit:2.0.2")
                implementation("media.kamel:kamel-image:0.7.3")
                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging:${Versions.ktor}")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("dev.icerock.moko:mvvm-core:${Versions.moko}")
                implementation("dev.icerock.moko:mvvm-compose:${Versions.moko}")
                implementation("dev.icerock.moko:resources-compose:0.23.0")
                api("dev.icerock.moko:resources:${Versions.mokoResources}")
                implementation("com.squareup.sqldelight:runtime:${Versions.sqlDelight}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            }
        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonMain)
                api("androidx.activity:activity-compose:1.8.0")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
                implementation("io.ktor:ktor-client-android:${Versions.ktor}")
                implementation("com.squareup.sqldelight:android-driver:${Versions.sqlDelight}")
                implementation("io.insert-koin:koin-androidx-compose:${Versions.koin}")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:${Versions.ktor}")
                implementation("com.squareup.sqldelight:native-driver:${Versions.sqlDelight}")

            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

sqldelight {
    database("AppDatabase") {
        packageName = "database"
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "net.schacher.mcc.shared"
    multiplatformResourcesClassName = "SharedRes"
    iosBaseLocalizationRegion = "en"
}