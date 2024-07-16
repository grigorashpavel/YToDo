import org.gradle.api.provider.Property

interface TelegramExtension {
    val token: Property<String>
    val chatId: Property<String>
}

interface ValidateExtension {
    val maxSizeInMB: Property<Int>
    val enabled: Property<Boolean>
}