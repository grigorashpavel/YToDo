plugins {
    id("base-application-convention")
    alias(libs.plugins.devtoolsKsp)
}

android {
    defaultConfig {
        val clientId: String by project
        manifestPlaceholders["YANDEX_CLIENT_ID"] = clientId

        val tokenBearer: String by project
        buildConfigField("String", "TOKEN_BEARER", tokenBearer)
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.coreUi)
    implementation(projects.data)
    implementation(projects.network)
    implementation(projects.androidCore)
    implementation(projects.domain)
    implementation(projects.features.allTasks)
    implementation(projects.features.edit)

    // Navigation
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Yandex OAuth
    implementation(libs.yandex.oauth)

    // WorkManager
    implementation(libs.androidx.work.workRunTime.ktx)

    // Dagger 2
    implementation(libs.dagger2.dagger)
    ksp(libs.dagger2.compiler)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}