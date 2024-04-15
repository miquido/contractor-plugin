package com.miquido.plugin.openapi.task

import com.miquido.plugin.openapi.Constant
import com.miquido.plugin.openapi.configuration.LocalConfiguration
import com.miquido.plugin.openapi.model.ContractData
import org.gradle.api.tasks.Copy

fun copy(localConfiguration: LocalConfiguration?, specification: ContractData): Copy.() -> Unit = {
    Constant.run {
        val localContractPath = localConfiguration?.path
            ?: throw IllegalArgumentException("Local path for OpenApi specification is not defined")
        val projectDirectory = project.layout.projectDirectory

        from(projectDirectory.dir("$localContractPath/${specification.path}").file(specificationFileName))
        into(projectDirectory.dir("$specificationDir/${specification.path}"))
    }
}