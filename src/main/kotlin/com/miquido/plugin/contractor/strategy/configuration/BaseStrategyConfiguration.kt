package com.miquido.plugin.contractor.strategy.configuration

import java.nio.file.Path
import kotlin.io.path.Path

data class BaseStrategyConfiguration @JvmOverloads constructor(
    val apiGenerationTargetPackagePath: String,
    val mainSpecificationFilePath: Path,
    val additionalSpecificationFilePaths: List<Path> = emptyList()
) {

    @JvmOverloads constructor(
        apiGenerationTargetPackagePath: String,
        mainSpecificationFilePath: String,
        additionalSpecificationFilePaths: Map<String, List<String>> = emptyMap()
    ): this(
        apiGenerationTargetPackagePath,
        Path(mainSpecificationFilePath),
        additionalSpecificationFilePaths.flatMap { (pathString, fileNames) ->
            val path = Path(pathString)
            fileNames.map { fileName -> path.resolve(fileName) }

        }
    )

    @Deprecated("Use other constructor.")
    @JvmOverloads constructor(
        apiGenerationTargetDirectoryList: List<String>,
        mainSpecificationFilePath: SingleFile,
        additionalSpecificationFilePaths: List<MultipleFiles> = emptyList()
    ): this(
        apiGenerationTargetDirectoryList.joinToString("."),
        mainSpecificationFilePath.asPath(),
        additionalSpecificationFilePaths.toSingleFilesList().map { it.asPath() }
    )
}
