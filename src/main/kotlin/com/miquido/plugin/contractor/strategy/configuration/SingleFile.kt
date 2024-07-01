package com.miquido.plugin.contractor.strategy.configuration

import kotlin.io.path.Path
import org.jetbrains.kotlin.konan.file.File


@Deprecated(
    message = "This class is not needed anymore. Use Path instead"
)
data class SingleFile (
    val directoryList: List<String>,
    val fileFullName: String
) {
    fun asPath() =
        Path(this.directoryList.joinToString(File.separator)).resolve(this.fileFullName)
}
