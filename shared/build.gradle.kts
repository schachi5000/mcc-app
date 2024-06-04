import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.org.jline.utils.InputStreamReader
import java.io.FileInputStream
import java.util.Properties

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization").version(libs.versions.kotlin)
    id("com.android.library")
    id("org.jetbrains.compose")
    alias(libs.plugins.sqldelight)
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
        }
    }

    val composeVersion = extra["compose.version"] as String

    //Generating BuildConfig for multiplatform
    val buildConfigGenerator by tasks.registering(Sync::class) {
        println("Generating BuildConfig for multiplatform")
        val packageName = "pro.schacher.mcc"
        from(
            resources.text.fromString(
                """
        |package $packageName
        |
        |object BuildConfig {
        |  const val OAUTH_URL = "${getLocalProperty("oauth.url")}"
        |  const val PROXY_URL = "${getLocalProperty("proxy.url")}"
        |}
        |
      """.trimMargin()
            )
        )
        {
            rename { "BuildConfig.kt" } // set the file name
            into(packageName) // change the directory to match the package
        }
        into(layout.buildDirectory.dir("generated/kotlin/"))
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(
                // convert the task to a file-provider
                buildConfigGenerator.map { it.destinationDir })
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(libs.koin.compose)
                implementation(libs.koin.core)
                implementation(libs.kermit)
                implementation(libs.kamel)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.loggin)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.kotlinx.serilization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight)
                implementation(libs.moko.mvvm.core)
                implementation(libs.moko.mvvm.compose)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                api("io.github.kevinnzou:compose-webview-multiplatform:1.9.2")
            }
        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonMain)
                api(libs.androidx.activity)
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
                api("org.jetbrains.compose.ui:ui-tooling-preview:${composeVersion}")
                implementation(libs.koin.compose.androidx)
                implementation(libs.ktor.client.android)
                implementation(libs.sqldelight.android)
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
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqldelight.native)
            }
        }
    }

    task("testClasses")
}

fun getLocalProperty(key: String, file: String = "local.properties"): Any {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        InputStreamReader(
            FileInputStream(localProperties),
            Charsets.UTF_8.toString()
        ).use { properties.load(it) }
    } else {
        return ""
    }

    return properties.getProperty(key)
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "net.schacher.mcc"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
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

tasks.named("build").dependsOn("generateSqlDelightInterface")