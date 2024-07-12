plugins {
    id("base-module-convention")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.pasha.data"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.network)
    implementation(projects.androidCore)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.roomCompiler)
    ksp(libs.androidx.room.roomCompiler)

    implementation(libs.androidx.core.ktx)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.retrofit2.retrofit)

    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}