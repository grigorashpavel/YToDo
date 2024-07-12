plugins {
    id("base-module-convention")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.pasha.network"

    defaultConfig {
        val baseUrl: String by project
        buildConfigField("String", "BASE_URL", baseUrl)

        val urlPattern: String by project
        buildConfigField("String", "URL_PATTERN", urlPattern)

        val certificate: String by project
        buildConfigField("String", "CERTIFICATE", certificate)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    // Network
    implementation(libs.kotlinSerialization.json)
    implementation(libs.kotlinSerialization.json.converter)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.retrofit2.retrofit)

    // Dagger 2
    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}