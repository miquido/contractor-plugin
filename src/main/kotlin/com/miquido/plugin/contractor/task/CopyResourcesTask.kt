package com.miquido.plugin.contractor.task

import com.miquido.contractor_plugin.BuildConfig
import com.miquido.plugin.contractor.Constant
import java.io.File
import org.gradle.api.tasks.Copy

fun copyResourcesTask(): Copy.() -> Unit = {
    Constant.run {
        val tempDirectory = File(project.layout.projectDirectory.dir(tempDirectoryName).asFile.absolutePath).apply {
            mkdirs()
        }

        // TODO: copy all resources
        project.plugins.getPlugin(BuildConfig.APP_NAME)
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
