package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import java.net.URLEncoder
import org.gradle.api.Project
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

class GitlabAccessTokenAcquireStrategy(
    generatedApiBaseDirectoryList: List<String>,
    specificationSourceDirectoryList: List<String>,
    specificationFileName: String,
    private val projectId: String,
    private val accessToken: String?,
    private val baseUrl: String = "https://gitlab.com",
    private val branch: String = "main"
) : ContractSpecificationAcquireStrategy(
    generatedApiBaseDirectoryList,
    specificationSourceDirectoryList,
    specificationFileName
) {

    private val downloadFromGitlabTaskName = "${taskName}DownloadFromGitlabTask"

    override val specificationAcquireTasksOrder = listOf(
        downloadFromGitlabTaskName
    )

    override fun canBeUsed(project: Project): Boolean {
        return accessToken != null
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        project.tasks.register(
            downloadFromGitlabTaskName,
            Download::class.java,
            download()
        )
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String) {
        project.tasks.named(downloadFromGitlabTaskName) {
            it.dependsOn(project.tasks.named(dependsOn))
        }
    }

    private fun download(): Download.() -> Unit =
        {
            Constant.run {
                header("PRIVATE-TOKEN", accessToken)
                src(getGitlabUrl())
                dest(
                    project.layout.projectDirectory
                        .dir("$specificationDir/${specificationSourceDirectoryPath}")
                        .file(specificationFileName)
                        .asFile
                )
            }
        }

    private fun getGitlabUrl(): String {
        Constant.run {
            val basePath = "${baseUrl}/api/v4/projects/${projectId}/repository/files"
            val arguments = URLEncoder.encode(
                "$specificationSourceDirectoryPath/${specificationFileName}", "utf-8"
            ) + "/raw?ref=${branch}"
            return "$basePath/$arguments"
        }
    }
}
