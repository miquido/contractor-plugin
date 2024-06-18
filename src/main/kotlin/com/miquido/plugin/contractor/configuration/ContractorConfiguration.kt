package com.miquido.plugin.contractor.configuration

import com.miquido.plugin.contractor.strategy.ContractSpecificationAcquireStrategy

open class ContractorConfiguration {
    var contracts: List<ContractSpecificationAcquireStrategy> = listOf()
    var generatorName: String = "kotlin-spring"
    var skipValidateSpec: Boolean = false
    var configOptions: Map<String, String> = mapOf()
    var importMappings: Map<String, String> = mapOf()
    var typeMappings: Map<String, String> = mapOf()
}
