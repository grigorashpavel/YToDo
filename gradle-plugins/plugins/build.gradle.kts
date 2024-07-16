plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.serialization)
}


gradlePlugin {
    plugins.register("telegram-plugin") {
        id = "telegram-plugin"
        implementationClass = "TelegramPlugin"
    }
}


dependencies {
    implementation(projects.convention)

    // Network
    implementation(libs.kotlinSerialization.json)
    implementation(libs.kotlinSerialization.json.converter)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.retrofit2.retrofit)


    implementation(libs.coroutines.core)
    implementation(libs.agp)
    implementation(libs.kotlin.gradle.plugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}