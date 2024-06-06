package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class LocalConfigurationAcquireStrategy(
    generatedApiBaseDirectoryList: List<String>,
    specificationSourceDirectoryList: List<String>,
    specificationFileName: String,
    private val relativePath: String?
) : ContractSpecificationAcquireStrategy(
    generatedApiBaseDirectoryList,
    specificationSourceDirectoryList,
    specificationFileName
) {

    private val copyFromLocalTaskName = "${taskName}CopyFromLocalTask"

    override val specificationAcquireTasksOrder = listOf(
        copyFromLocalTaskName
    )

    override fun canBeUsed(project: Project): Boolean {
        return relativePath != null && project.layout.projectDirectory.dir(relativePath).asFile.exists()
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        project.tasks.register(
            copyFromLocalTaskName,
            Copy::class.java,
            copy()
        )
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String) {
        project.tasks.named(copyFromLocalTaskName) {
            it.dependsOn(project.tasks.named(dependsOn))
        }
    }

    private fun copy(): Copy.() -> Unit = {
        Constant.run {
            val projectDirectory = project.layout.projectDirectory

            from(projectDirectory.dir("$relativePath/$specificationSourceDirectoryPath").file(specificationFileName))
            into(projectDirectory.dir("$specificationDir/$specificationSourceDirectoryPath"))
        }
    }
}
