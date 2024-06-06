package com.miquido.plugin.contractor.configuration

import com.miquido.plugin.contractor.strategy.ContractSpecificationAcquireStrategy

open class ContractorConfiguration {
    var contracts: List<ContractSpecificationAcquireStrategy> = listOf()
    var openApiConfiguration: Map<String, String> = mapOf()
}
