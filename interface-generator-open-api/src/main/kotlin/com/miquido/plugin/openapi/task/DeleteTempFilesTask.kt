package com.miquido.plugin.openapi.task

import com.miquido.plugin.openapi.Constant
import org.gradle.api.tasks.Delete

fun deleteTempFilesTask(): Delete.() -> Unit = {
    Constant.run {
        delete(project.layout.projectDirectory.dir(tempDirectoryName))
        delete(project.layout.projectDirectory.dir(specificationDir))
        delete(project.layout.projectDirectory.dir(configurationDir))
    }
}