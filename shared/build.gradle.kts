import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose.compiler)
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
        |  const val SERVICE_URL = "${getLocalProperty("service.url")}"
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
            resources.srcDirs("src/commonMain/resources")
            kotlin.srcDir(buildConfigGenerator.map { it.destinationDir })
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(libs.koin.compose)
                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.kotlinx.navigation.compose)
                implementation(libs.kermit)
                implementation(libs.kamel)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.loggin)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.encoding)
                implementation(libs.ktor.serialization.json)
                implementation(libs.kotlinx.serilization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.viewmodel.compose)
                implementation(libs.sqldelight)
                implementation(libs.sqldelight.coroutines)
                implementation(compose.components.resources)
            }
        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonMain)
                api(libs.androidx.activity)
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
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

fun getLocalProperty(key: String, file: String = "local.properties") = Properties()
    .takeIf { rootProject.file(file).exists() }
    ?.also { it.load(file(rootProject.file(file).path).inputStream()) }
    ?.getProperty(key) ?: ""

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
    databases {
        create("AppDatabase") {
            packageName.set("database")
        }
    }
}

tasks.named("build").dependsOn("generateSqlDelightInterface")