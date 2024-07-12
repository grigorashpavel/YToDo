plugins {
    id("base-module-convention")
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.pasha.all_tasks"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.androidCore)
    implementation(projects.coreUi)
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.fragment)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.recyclerview.swipedecorator)

    // Dagger 2
    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}