package com.miquido.plugin.contractor.strategy.configuration

@Deprecated(
    message = "This class is not needed anymore. Use List<Path> instead"
)
data class MultipleFiles (
    val directoryList: List<String>,
    val fileFullNames: List<String>
) {

    fun toSingleFileList() =
        fileFullNames.map {
            SingleFile(
                directoryList,
                it
            )
        }
}

fun List<MultipleFiles>?.toSingleFilesList() =
    this?.flatMap { it.toSingleFileList() } ?: emptyList()
