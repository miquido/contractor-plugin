package com.miquido.plugin.openapi.configuration

import com.miquido.plugin.openapi.model.ContractData

open class OpenApiGeneratorConfiguration {
    var contracts: List<ContractData> = listOf()
    var local: LocalConfiguration? = null
    var repository: RepositoryConfiguration? = null
}