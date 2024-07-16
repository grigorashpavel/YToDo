plugins {
    id("base-module-convention")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.pasha.edit"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

composeCompiler {
    enableStrongSkippingMode = true

    reportsDestination = layout.buildDirectory.dir("compose_compiler")
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

    // Compose
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiPreview)
    implementation(libs.androidx.compose.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.lifecycle.compose.viewmodel)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.compose.animation)

    implementation(libs.androidx.compose.viewbinding)

    // Dagger 2
    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}