import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File
import org.gradle.api.GradleException

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp.kotlin)
}

android {
    namespace = "com.budoxr.manifestations"
    compileSdk = AndroidSdk.TARGET

    defaultConfig {
        applicationId = "com.budoxr.manifestations"
        minSdk = AndroidSdk.MIN
        targetSdk = AndroidSdk.TARGET
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        testOptions {
            unitTests {
                isReturnDefaultValues = true
                isIncludeAndroidResources = true
            }
            animationsDisabled = true
        }

        manifestPlaceholders["enableOnBackInvokedCallback"] = "true"
    }


    buildTypes {
        debug {
            isDebuggable = true
            buildConfigField("boolean", "CAPTURE_JSON", "true")
            buildConfigField("boolean", "SAVE_DATA_TO_JSON", "true")
        }
        release {
            isDebuggable = false
            buildConfigField("boolean", "CAPTURE_JSON", "false")
            buildConfigField("boolean", "SAVE_DATA_TO_JSON", "false")

            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    val isApi33 = compileSdk?.let { it.toInt() >= 33 }
    sourceSets {
        if (isApi33 == true) {
            getByName("main") {
                manifest.srcFile("src/mainSdk33/AndroidManifest.xml")
            }
        } else {
            getByName("main") {
                manifest.srcFile("src/main/AndroidManifest.xml")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.ksp)
    implementation(libs.bundles.koin)
    testImplementation(libs.koin.test.junit)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler.ksp)
    api(libs.room.ktx)
    androidTestImplementation(libs.room.test)

    implementation(libs.timber.log)
    implementation(libs.exoplayer)
    implementation(libs.workmanager)
    androidTestImplementation(libs.workmanager.test)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.mockk.test)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.mockk.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit.runner)
    androidTestImplementation(libs.androidx.junit.rules)
    testImplementation(libs.roboelectric.test)
    testImplementation(kotlin("test"))
}


