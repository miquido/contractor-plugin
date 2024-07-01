package com.miquido.plugin.contractor.extension

import com.miquido.plugin.contractor.Constant
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString
import org.gradle.configurationcache.extensions.capitalized

fun Path.normalizedPath() =
    this.normalize().pathString

fun Path.toList(): List<String> =
    this.normalizedPath()
        .removePrefix(".${File.separator}")
        .removePrefix(File.separator)
        .split(File.separator)

fun Path.toCapitalizedCamelCase(): String {
    return this.parent.toList().joinToString("") { it.capitalized() } + this.toFile().nameWithoutExtension.capitalized()
}

fun Path.getAllLinesOfTargetFile(): List<String> =
    Constant.specificationDir.resolve(this).toFile().readLines()
