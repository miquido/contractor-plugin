package com.miquido.plugin.contractor.strategy.configuration

import org.gradle.configurationcache.extensions.capitalized

data class BaseStrategyConfiguration (
    val apiGenerationTargetDirectoryList: List<String>,
    val mainSpecificationFilePath: SingleFile,
    val additionalSpecificationFilePaths: List<MultipleFiles> = emptyList()
)

typealias DirectoryList = List<String>
typealias FileFullName = String

data class SingleFile (
    val directoryList: DirectoryList,
    val fileFullName: FileFullName
) {

    fun toCapitalizedCamelCase() =
        directoryList.toCapitalizedCamelCase() + fileFullName.substringBefore(".").capitalized()
}

data class MultipleFiles (
    val directoryList: DirectoryList,
    val fileFullNames: List<FileFullName>
) {

    fun toSingleFileList() =
        fileFullNames.map {
            SingleFile(
                directoryList,
                it
            )
        }
}

fun DirectoryList.toCapitalizedCamelCase() =
    this.joinToString("") {
        it.capitalized()
    }

fun DirectoryList.toDirectoryPath() =
    this.joinToString("/")

fun DirectoryList.toPackagesPath() =
    this.joinToString(".")
