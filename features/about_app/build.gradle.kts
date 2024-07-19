plugins {
    id("base-module-convention")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.pasha.about_app"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.coreUi)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.fragment)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.divkit.div)
    implementation(libs.divkit.core)
    implementation(libs.divkit.json)
    implementation(libs.divkit.picasso)
    implementation(libs.divkit.video)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}