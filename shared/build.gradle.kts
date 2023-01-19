plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("co.touchlab.faktory.kmmbridge") version "0.3.4"
    `maven-publish`
    kotlin("native.cocoapods")
}

version = "0.1"
kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
//        it.binaries.framework {
//            baseName = "shared"
//        }
        cocoapods {
            summary = "KMMBridgeSampleKotlin"
            homepage = "https://touchlab.dev"
            ios.deploymentTarget = "13"
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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
    }
}

android {
    namespace = "com.hkaito.edu.kmm_base"
    compileSdk = 32
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}

addGithubPackagesRepository()
kmmbridge {
    mavenPublishArtifacts()
    githubReleaseVersions()
    spm()
    cocoapods("git@github.com:haiphamthanh/PublicPodspecs.git")
    versionPrefix.set("0.3")
}