import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android<BaseAppModuleExtension> {
    namespace = "com.pasha.ytodo"
    compileSdk = BuildInfo.compileSdk

    defaultConfig {
        applicationId = "com.pasha.ytodo"
        minSdk = BuildInfo.minSdk
        targetSdk = BuildInfo.targetSdk
        versionCode = BuildInfo.versionCode
        versionName = BuildInfo.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Base Activity Impl
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.activity)
}