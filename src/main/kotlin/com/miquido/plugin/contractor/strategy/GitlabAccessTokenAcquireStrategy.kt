package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.extension.create
import com.miquido.plugin.contractor.extension.dir
import com.miquido.plugin.contractor.extension.file
import com.miquido.plugin.contractor.extension.named
import com.miquido.plugin.contractor.extension.normalizedPath
import com.miquido.plugin.contractor.extension.register
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.task.DependsOnSingleTaskAction
import com.miquido.plugin.contractor.file.SpecificationFilesScanner
import com.miquido.plugin.contractor.task.TaskName
import java.net.URLEncoder
import java.nio.file.Path
import org.gradle.api.Project
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

class GitlabAccessTokenAcquireStrategy(
    baseConfiguration: BaseStrategyConfiguration,
    private val configuration: Configuration
) : ContractSpecificationAcquireStrategy(baseConfiguration) {

    companion object {
        private const val TASK_NAME_SUFFIX = "DownloadFromGitlabTask"
    }

    private val downloadFromGitlabTaskNames = createFilesToTaskNamesMap(TASK_NAME_SUFFIX)

    override val specificationAcquireTasksOrder = buildList {
        downloadFromGitlabTaskNames.values.let { this.addAll(it) }
    }

    override fun canBeUsed(project: Project): Boolean {
        return configuration.accessToken != null
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        val specificationFilesScanner = SpecificationFilesScanner()
        downloadFromGitlabTaskNames.forEach { (filePath, taskName) ->
            project.tasks.register(taskName) { task ->
                task.doLast {
                    specificationFilesScanner.importFiles(
                        project, taskName, filePath, this::downloadFile
                    )
                }
            }
        }
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: TaskName) {
        var dependentTaskName = dependsOn
        specificationAcquireTasksOrder.forEach { taskName ->
            project.tasks.named(
                taskName,
                DependsOnSingleTaskAction(project, dependentTaskName)
            )
            dependentTaskName = taskName
        }
    }

    private fun downloadFile(
        project: Project,
        mainTaskName: TaskName,
        filePath: Path
    ) {
        project.tasks.create(
            mainTaskName.with(filePath),
            Download::class.java
        ) { downloadConfig ->
            Constant.run {
               downloadConfig.header("PRIVATE-TOKEN", configuration.accessToken)
               downloadConfig.src(filePath.asGitlabUrl())
               downloadConfig.dest(
                    project.layout.projectDirectory
                        .dir(specificationDir)
                        .file(filePath)
                        .asFile
                )
            }
        }.download()
    }

    private fun Path.asGitlabUrl(): String {
        Constant.run {
            val basePath = "${configuration.baseUrl}/api/v4/projects/${configuration.projectId}/repository/files"
            val arguments = URLEncoder.encode(this@asGitlabUrl.normalizedPath(), "utf-8") + "/raw?ref=${configuration.branch}"
            return "$basePath/$arguments"
        }
    }

    data class Configuration @JvmOverloads constructor(
        val projectId: String,
        val accessToken: String?,
        val baseUrl: String = "https://gitlab.com",
        val branch: String = "main"
    ) : ContractSpecificationAcquireStrategy.Configuration<GitlabAccessTokenAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): GitlabAccessTokenAcquireStrategy =
            GitlabAccessTokenAcquireStrategy(baseConfiguration, this)
    }
}
