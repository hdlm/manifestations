import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp.kotlin)
}

ksp {
    arg("KOIN_DEFAULT_MODULE", "true")
}

android {
    namespace = "com.budoxr.manifestations"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.budoxr.manifestations"
        minSdk = 28
        targetSdk = 36
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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf("room.schemaLocation" to "$projectDir/schemas")
                )
            }
        }

    }


    buildTypes {
        debug {
            buildConfigField("boolean", "CAPTURE_JSON", "true")
            buildConfigField("boolean", "SAVE_DATA_TO_JSON", "true")
            isDebuggable = true
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

    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDirs("src/main/")
        }
        getByName("test").java.srcDirs("src/test/kotlin")
    }


    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("test").java.srcDirs("src/test/java")
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

    applicationVariants.all {
        if (android.buildToolsVersion >= "33.0.0") {
            sourceSets {
                getByName("main").manifest.srcFile("src/mainSdk33/AndroidManifest.xml")
            }
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
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler.ksp)
    implementation(libs.room.ktx)
    implementation(libs.room.common)
    androidTestImplementation(libs.room.test)

    implementation(libs.exoplayer)
    implementation(libs.workmanager.kotlin.coroutines)
    androidTestImplementation(libs.workmanager.test)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

