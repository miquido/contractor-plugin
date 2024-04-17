package com.miquido.plugin.openapi.configuration

import com.miquido.plugin.openapi.model.OpenApiSpecification

open class OpenApiGeneratorConfiguration {
    var contracts: List<OpenApiSpecification> = listOf()
    var local: LocalConfiguration? = null
    var repository: RepositoryConfiguration? = null
    var openApiConfiguration: Map<String, String> = mapOf()
}