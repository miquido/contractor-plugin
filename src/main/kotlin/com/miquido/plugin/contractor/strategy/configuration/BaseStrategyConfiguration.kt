package com.miquido.plugin.contractor.strategy.configuration

data class BaseStrategyConfiguration (
    val generatedApiBaseDirectoryList: List<String>,
    val specificationSourceDirectoryList: List<String>,
    val mainSpecificationFileName: String,
    val additionalSpecificationFileNames: List<String>
)
