package com.miquido.plugin.contractor.extension

import java.nio.file.Path
import kotlin.io.path.name
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile

fun Directory.dir(path: Path): Directory =
    this.dir(path.normalizedPath())

fun Directory.file(path: Path): RegularFile =
    this.dir(path.parent.normalizedPath()).file(path.name)
