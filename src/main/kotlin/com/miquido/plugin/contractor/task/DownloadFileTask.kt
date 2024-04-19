package com.miquido.plugin.contractor.task

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.configuration.GitlabConfiguration
import com.miquido.plugin.contractor.configuration.RepositoryConfiguration
import com.miquido.plugin.contractor.model.RemoteOpenApiSpecification
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import java.net.URLEncoder

fun download(configuration: RepositoryConfiguration?, specification: RemoteOpenApiSpecification): Download.() -> Unit =
    {
        Constant.run {
            when (configuration) {
                is GitlabConfiguration -> {
                    header("PRIVATE-TOKEN", configuration.accessToken)
                    src(getGitlabUrl(configuration.projectId, specification))
                    dest(
                        project.layout.projectDirectory.dir("$specificationDir/${specification.path}")
                            .file(specification.fileName).asFile
                    )
                }
            }
        }
    }

private fun getGitlabUrl(projectId: String, spec: RemoteOpenApiSpecification): String {
    Constant.run {
        val basePath = "${spec.baseUrl}/api/v4/projects/${projectId}/repository/files"
        val arguments = URLEncoder.encode("${spec.path}/${spec.fileName}", "utf-8") + "/raw?ref=${spec.branch}"
        return "$basePath/$arguments"
    }
}
