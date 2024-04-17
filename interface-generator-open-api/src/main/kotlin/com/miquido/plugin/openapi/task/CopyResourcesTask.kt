package com.miquido.plugin.openapi.task

import com.miquido.plugin.openapi.Constant
import org.gradle.api.tasks.Copy
import java.io.File

fun copyResourcesTask(): Copy.() -> Unit = {
    Constant.run {
        val tempDirectory = File(project.layout.projectDirectory.dir(tempDirectoryName).asFile.absolutePath).apply {
            mkdirs()
        }

        // TODO: copy all resources + extract plugin ID
        project.plugins.getPlugin("interface-generator-open-api")
            .javaClass
            .classLoader
            .getResourceAsStream("configuration/apiInterface.mustache")
            .let { resource ->
                File(tempDirectory.absolutePath + File.separator + "apiInterface.mustache").let {
                    resource?.copyTo(it.outputStream())
                }
            }

        from(project.layout.projectDirectory.dir("./$tempDirectoryName"))
        into(project.layout.projectDirectory.dir("./$configurationDir"))
    }
}