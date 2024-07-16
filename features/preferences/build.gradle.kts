plugins {
    id("base-module-convention")
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.pasha.preferences"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.coreUi)
    implementation(projects.androidCore)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.fragment)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Dagger 2
    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}