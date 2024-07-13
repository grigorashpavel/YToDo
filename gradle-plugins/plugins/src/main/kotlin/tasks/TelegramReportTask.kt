package tasks

import api.NetworkClient
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject


abstract class TelegramReportTask @Inject constructor(
    private val client: NetworkClient
) : DefaultTask() {
    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:Input
    abstract val buildVariant: Property<String>

    @get:Input
    abstract val buildVersion: Property<String>

    @get:InputFile
    abstract val apkSizeFile: RegularFileProperty

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val chatId: Property<String>

    @TaskAction
    fun execute() {
        apkDir.get().asFile.listFiles()
            ?.filter { file -> file.name.endsWith(".apk") }
            ?.forEach { file ->
                val fileSize = apkSizeFile.asFile.get().readText()
                val newName = "todolist-${buildVariant.get()}-${buildVersion.get()}.apk"

                val fileToUpload = File(file.parent, newName)
                file.copyTo(fileToUpload)

                runBlocking {
                    client.sendMessage(
                        "Build Success! Size: $fileSize KB",
                        token.get(),
                        chatId.get()
                    )
                    client.sendFile(fileToUpload, token.get(), chatId.get())

                    fileToUpload.delete()
                }
            }

    }
}