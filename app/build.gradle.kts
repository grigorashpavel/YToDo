plugins {
    id("base-application-convention")
    id("telegram-plugin")
    alias(libs.plugins.devtoolsKsp)
}

telegram {
    token.set(providers.environmentVariable("TELEGRAM_TOKEN"))
    chatId.set(providers.environmentVariable("TELEGRAM_CHAT_ID"))
}

validation {
    maxSizeInMB = 30
    enabled = true
}

android {
    defaultConfig {
        val clientId = providers.environmentVariable("CLIENT_ID")
        manifestPlaceholders["YANDEX_CLIENT_ID"] = clientId.get()

        val tokenBearer = providers.environmentVariable("TOKEN_BEARER")
        buildConfigField("String", "TOKEN_BEARER", tokenBearer.get())
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
    implementation(projects.features.preferences)

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