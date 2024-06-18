package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.util.DependsOnSingleTaskAction
import java.net.URLEncoder
import org.gradle.api.Project
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

class GitlabAccessTokenAcquireStrategy(
    baseConfiguration: BaseStrategyConfiguration,
    private val configuration: Configuration
) : ContractSpecificationAcquireStrategy(baseConfiguration) {

    private val downloadFromGitlabTaskNames = createFilesNamesToTaskNamesMap("DownloadFromGitlabTask")

    override val specificationAcquireTasksOrder = buildList {
        this.addAll(downloadFromGitlabTaskNames.values)
    }

    override fun canBeUsed(project: Project): Boolean {
        return configuration.accessToken != null
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        downloadFromGitlabTaskNames.forEach { (fileName, taskName) ->
            project.tasks.register(
                taskName,
                Download::class.java,
                download(fileName)
            )
        }
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String) {
        var dependentTaskName = dependsOn
        specificationAcquireTasksOrder.forEach { taskName ->
            project.tasks.named(
                taskName,
                DependsOnSingleTaskAction(project, dependentTaskName)
            )
            dependentTaskName = taskName
        }
    }

    private fun download(fileName: String): Download.() -> Unit =
        {
            Constant.run {
                header("PRIVATE-TOKEN", configuration.accessToken)
                src(getGitlabUrl(fileName))
                dest(
                    project.layout.projectDirectory
                        .dir("$specificationDir/${specificationSourceDirectoryPath}")
                        .file(fileName)
                        .asFile
                )
            }
        }

    private fun getGitlabUrl(fileName: String): String {
        Constant.run {
            val basePath = "${configuration.baseUrl}/api/v4/projects/${configuration.projectId}/repository/files"
            val arguments = URLEncoder.encode(
                "$specificationSourceDirectoryPath/$fileName", "utf-8"
            ) + "/raw?ref=${configuration.branch}"
            return "$basePath/$arguments"
        }
    }

    data class Configuration (
        val projectId: String,
        val accessToken: String?,
        val baseUrl: String = "https://gitlab.com",
        val branch: String = "main"
    ) : ContractSpecificationAcquireStrategy.Configuration<GitlabAccessTokenAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): GitlabAccessTokenAcquireStrategy =
            GitlabAccessTokenAcquireStrategy(baseConfiguration, this)
    }
}
