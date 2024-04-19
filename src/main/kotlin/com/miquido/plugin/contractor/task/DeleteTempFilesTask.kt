package com.miquido.plugin.contractor.task

import com.miquido.plugin.contractor.Constant
import org.gradle.api.tasks.Delete

fun deleteTempFilesTask(): Delete.() -> Unit = {
    Constant.run {
        delete(project.layout.projectDirectory.dir(tempDirectoryName))
        delete(project.layout.projectDirectory.dir(specificationDir))
        delete(project.layout.projectDirectory.dir(configurationDir))
    }
}
