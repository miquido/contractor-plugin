package com.miquido.plugin.contractor.task

import com.miquido.plugin.contractor.extension.toCapitalizedCamelCase
import java.nio.file.Path

data class TaskName(
    val prefix: String,
    val main: String,
    val suffix: String
) {
    constructor(fullValue: String): this(
        "", fullValue, ""
    )

    constructor(prefix: String, main: String): this(
        prefix, main, ""
    )

    fun getFullValue() = "$prefix$main$suffix"

    fun with(filePath: Path) =
        this.copy(
            main = "${this.main}${filePath.toCapitalizedCamelCase()}"
        )
}
