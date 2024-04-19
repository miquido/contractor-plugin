package com.miquido.plugin.contractor.configuration

import com.miquido.plugin.contractor.model.OpenApiSpecification

open class ContractorConfiguration {
    var contracts: List<OpenApiSpecification> = listOf()
    var local: LocalConfiguration? = null
    var repository: RepositoryConfiguration? = null
    var openApiConfiguration: Map<String, String> = mapOf()
}
