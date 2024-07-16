plugins {
    id("base-module-convention")
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.pasha.android_core"
}

dependencies {
    implementation(projects.domain)


    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.core.ktx)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // WorkManager
    implementation(libs.androidx.work.workRunTime.ktx)

    // Dagger
    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)

    // Navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}