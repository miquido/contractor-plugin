package com.miquido.plugin.contractor.model

import org.gradle.configurationcache.extensions.capitalized

abstract class OpenApiSpecification(
    val fileName: String,
    basePackage: String,
    project: String,
    domain: String,
    version: String,
) {
    abstract val localization: OpenApiLocalization

    private val taskName = "$project${domain.capitalized()}${version.capitalized()}"
    val downloadTaskName = "${taskName}DownloadTask"
    val copyTaskName = "${taskName}CopyTask"
    val generateTaskName = "${taskName}GenerateTask"
    val path = "${project}/${domain}/${version}"
    val basePackage = "${basePackage}.${project}.${domain}.${version}" // override package
}
