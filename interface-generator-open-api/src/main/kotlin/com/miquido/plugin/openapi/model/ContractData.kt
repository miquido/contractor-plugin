package com.miquido.plugin.openapi.model

import org.gradle.configurationcache.extensions.capitalized

data class ContractData(
    val localization: ContractLocalization,
    val project: String,
    val domain: String,
    val version: String,
    val branch: String = "main"
) {
    private val taskName = "$project${domain.capitalized()}${version.capitalized()}"
    val downloadTaskName = "${taskName}DownloadTask"
    val copyTaskName = "${taskName}CopyTask"
    val generateTaskName = "${taskName}GenerateTask"
    val path = "${project}/${domain}/${version}"
    val basePackage = "com.miquido.${project}.${domain}.${version}" // override package
}