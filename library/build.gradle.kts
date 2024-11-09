import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
                }
            }
        }
    }

    val xcf = XCFramework()
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64(),
        watchosArm64(),
        watchosDeviceArm64(),
        watchosSimulatorArm64(),
        watchosX64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.healthConnectClient)
        }
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

android {
    namespace = "com.khealth"
    compileSdk = libs.versions.targetSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.java.get())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}
