plugins {
    id("base-module-convention")
}

android {
    namespace = "com.pasha.core_ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}