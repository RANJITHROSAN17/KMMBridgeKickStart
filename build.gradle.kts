plugins {
    kotlin("multiplatform") version "1.7.10"
    id("com.android.library")
    kotlin("plugin.serialization") version "1.7.20"
    id("maven-publish")
    id("co.touchlab.faktory.kmmbridge") version "0.2.2"
    kotlin("native.cocoapods") version "1.7.20"
    id("com.squareup.sqldelight") version libs.versions.sqlDelight.get()
}

group = "co.touchlab"
version = "1.0"

repositories {
    google()
    mavenCentral()
}

kotlin {
    android {
        publishAllLibraryVariants()
    }
    ios()
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                implementation(libs.bundles.ktor.common)
                implementation(libs.touchlab.stately)
                implementation(libs.multiplatformSettings.common)
                implementation(libs.kotlinx.dateTime)
                api(libs.touchlab.kermit)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.shared.commonTest)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqlDelight.android)
                implementation(libs.ktor.client.okHttp)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.bundles.shared.androidTest)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sqlDelight.native)
                implementation(libs.ktor.client.ios)
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }

    cocoapods {
        summary = "Brownfield sample"
        homepage = "https://www.touchlab.co"
        ios.deploymentTarget = "13.5"
        framework {
            isStatic = false
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kmmbridge {
    githubReleaseArtifacts()
    githubReleaseVersions()
    cocoapods("git@github.com:touchlab/PodSpecs.git")
    versionPrefix.set("1.0")
    timestampVersions()
}

sqldelight {
    database("BrownfieldSdkDb") {
        packageName = "co.touchlab.brownfieldsdk.db"
    }
}
