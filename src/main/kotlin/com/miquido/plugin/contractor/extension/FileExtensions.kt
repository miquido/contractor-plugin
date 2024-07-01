package com.miquido.plugin.contractor.extension

import java.io.File
import java.nio.file.Path

fun File.resolve(path: Path): File =
    this.resolve(path.normalizedPath())
