package tasks

import api.NetworkClient
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class ValidateApkSizeTask @Inject constructor(
    private val client: NetworkClient
) : DefaultTask() {
    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val chatId: Property<String>

    @get:Input
    abstract val maxSizeMB: Property<Int>

    @get:Input
    abstract val taskName: Property<String>

    @get:OutputFile
    abstract val apkSizeFile: RegularFileProperty

    @get:Input
    abstract val taskEnabled: Property<Boolean>

    @TaskAction
    fun execute() {
        val maxSizeKB = maxSizeMB.get() * 1024
        apkDir.get().asFile.listFiles()
            ?.filter { it.name.endsWith(".apk") }
            ?.forEach { file ->
                val fileKb = file.length() / 1024
                if (taskEnabled.get() && fileKb > maxSizeKB) {
                    runBlocking {
                        client.sendMessage(
                            "Task (${taskName.get()}): File ${file.name} large then max size ($maxSizeKB KB)",
                            token.get(),
                            chatId.get()
                        )
                    }

                    throw GradleException("Task (${taskName.get()}): File ${file.name} large then max size ($maxSizeKB KB)")
                }

                apkSizeFile.get().asFile.writeText(fileKb.toString())
            }
    }
}