//https://github.com/joreilly/PeopleInSpace/blob/main/common/build.gradle.kts
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("io.github.luca992.multiplatform-swiftpackage") version "2.0.5-arm64"
//    id("org.jetbrains.kotlin.native.cocoapods")
}

// CocoaPods requires the podspec to have a version.
//version = "1.0"

// Core plugin
kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    val sdkName: String? = System.getenv("SDK_NAME")
    val isWatchOSDevice = sdkName.orEmpty().startsWith("watchos")
    if (isWatchOSDevice) {
        watchosArm64("watch")
    } else {
        watchosX64("watch")
    }

    //macosX64("macOS")
    macosArm64("macOS")
    android()
    jvm()

    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Dependencies.Ktor) {
                    implementation(clientCore)
                    implementation(clientJson)
                    implementation(clientLogging)
                    implementation(contentNegotiation)
                    implementation(json)
                }

                with(Dependencies.Kotlinx) {
                    implementation(coroutinesCore)
                    implementation(serializationCore)
                }

                with(Dependencies.SqlDelight) {
                    implementation(runtime)
                    implementation(coroutineExtensions)
                }

                with(Dependencies.Koin) {
                    api(core)
                    api(test)
                }

                with(Dependencies.Log) {
                    api(kermit)
                }
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(Dependencies.Koin.test)
                implementation(Dependencies.Kotlinx.coroutinesTest)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.clientAndroid)
                implementation(Dependencies.SqlDelight.androidDriver)
            }
        }
//        val androidTest by getting

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Dependencies.Ktor.clientDarwin)
                implementation(Dependencies.SqlDelight.nativeDriver)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        val watchMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.clientDarwin)
                implementation(Dependencies.SqlDelight.nativeDriver)
            }
        }
        val macOSMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.clientDarwin)
                implementation(Dependencies.SqlDelight.nativeDriverMacos)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.clientJava)
                implementation(Dependencies.SqlDelight.sqliteDriver)
                implementation(Dependencies.Log.slf4j)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.clientJs)
            }
        }
    }
}

// Android
android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "com.hkaito.edu.your_essay_common"
}

// Data base
sqldelight {
    database("YourEssayDatabase") {
        packageName = "com.hkaito.edu.youressay.data.local"
        sourceFolders = listOf("sqldelight")
    }
}

// Public source settings
multiplatformSwiftPackage {
    packageName("YourEssayKit")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("14") }
    }
    outputDirectory(File(rootDir, "/"))
}

// Common settings
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}