import api.NetworkClient
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import tasks.TelegramReportTask
import tasks.ValidateApkSizeTask


class TelegramPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
                ?: throw GradleException("Android plugin required.")

        val ext = project.extensions.create("telegram", TelegramExtension::class)
        val validateExt = project.extensions.create("validation", ValidateExtension::class)

        val client = NetworkClient()
        androidComponents.onVariants { variant ->
            val variantName = variant.name.replaceFirstChar { it.uppercase() }
            val reportTaskName = "telegramReportFor$variantName"
            val validateTaskName = "validateApkSizeFor$variantName"

            val artifacts = variant.artifacts.get(SingleArtifact.APK)

            val validateTask = project.tasks
                .register(validateTaskName, ValidateApkSizeTask::class, client).apply {
                    configure {
                        taskEnabled.set(validateExt.enabled)

                        apkDir.set(artifacts)

                        token.set(ext.token)
                        chatId.set(ext.chatId)

                        maxSizeMB.set(validateExt.maxSizeInMB)
                        taskName.set(validateTaskName)

                        apkSizeFile.set(project.layout.buildDirectory.file("ApkSize.txt"))
                    }
                }


            project.tasks.register(reportTaskName, TelegramReportTask::class, client).configure {
                apkDir.set(artifacts)
                token.set(ext.token)
                chatId.set(ext.chatId)


                buildVersion.set(BuildInfo.versionCode.toString())
                buildVariant.set(variant.name)

                apkSizeFile.set(validateTask.get().apkSizeFile)
            }
        }
    }
}
