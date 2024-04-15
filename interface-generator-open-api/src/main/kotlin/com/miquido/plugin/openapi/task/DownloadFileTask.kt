package com.miquido.plugin.openapi.task

import com.miquido.plugin.openapi.Constant
import com.miquido.plugin.openapi.configuration.GitlabConfiguration
import com.miquido.plugin.openapi.configuration.RepositoryConfiguration
import com.miquido.plugin.openapi.model.ContractData
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import java.net.URLEncoder

fun download(configuration: RepositoryConfiguration?, specification: ContractData): Download.() -> Unit = {
    Constant.run {
        when (configuration) {
            is GitlabConfiguration -> {
                header("PRIVATE-TOKEN", configuration.accessToken)
                src(getGitlabUrl(configuration.projectId, specification))
                dest(
                    project.layout.projectDirectory.dir("$specificationDir/${specification.path}")
                        .file(specificationFileName).asFile
                )
            }
        }
    }
}

private fun getGitlabUrl(projectId: String, spec: ContractData): String {
    Constant.run {
        val basePath = "https://gitlab.com/api/v4/projects/${projectId}/repository/files"
        val arguments = URLEncoder.encode("${spec.path}/$specificationFileName", "utf-8") + "/raw?ref=${spec.branch}"
        return "$basePath/$arguments"
    }
}