package com.miquido.plugin.contractor.task

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.configuration.LocalConfiguration
import com.miquido.plugin.contractor.model.OpenApiSpecification
import org.gradle.api.tasks.Copy

fun copy(localConfiguration: LocalConfiguration?, specification: OpenApiSpecification): Copy.() -> Unit = {
    Constant.run {
        val localContractPath = localConfiguration?.relativePath
            ?: throw IllegalArgumentException("Local path for OpenApi specification is not defined")
        val projectDirectory = project.layout.projectDirectory

        from(projectDirectory.dir("$localContractPath/${specification.path}").file(specification.fileName))
        into(projectDirectory.dir("$specificationDir/${specification.path}"))
    }
}
