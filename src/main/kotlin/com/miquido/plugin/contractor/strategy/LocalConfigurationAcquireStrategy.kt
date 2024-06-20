package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.strategy.configuration.SingleFile
import com.miquido.plugin.contractor.strategy.configuration.toDirectoryPath
import com.miquido.plugin.contractor.util.DependsOnSingleTaskAction
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class LocalConfigurationAcquireStrategy(
    baseConfiguration: BaseStrategyConfiguration,
    private val configuration: Configuration
) : ContractSpecificationAcquireStrategy(baseConfiguration) {

    private val copyFromLocalTaskNames = createFilesToTaskNamesMap("CopyFromLocalTask")

    override val specificationAcquireTasksOrder = buildList {
        this.addAll(copyFromLocalTaskNames.values)
    }

    override fun canBeUsed(project: Project): Boolean {
        return configuration.relativePath != null && project.layout.projectDirectory.dir(configuration.relativePath).asFile.exists()
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        copyFromLocalTaskNames.forEach { (file, taskName) ->
            project.tasks.register(
                taskName,
                Copy::class.java,
                copy(file)
            )
        }
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String) {
        var dependentTaskName = dependsOn
        specificationAcquireTasksOrder.forEach { taskName ->
            project.tasks.named(
                taskName,
                DependsOnSingleTaskAction(project, dependentTaskName)
            )
            dependentTaskName = taskName
        }
    }

    private fun copy(file: SingleFile): Copy.() -> Unit = {
        Constant.run {
            val projectDirectory = project.layout.projectDirectory
            val specificationSourceDirectoryPath = file.directoryList.toDirectoryPath()
            from(projectDirectory.dir("${configuration.relativePath}/$specificationSourceDirectoryPath").file(file.fileFullName))
            into(projectDirectory.dir("$specificationDir/$specificationSourceDirectoryPath"))
        }
    }

    data class Configuration (
        val relativePath: String?
    ) : ContractSpecificationAcquireStrategy.Configuration<LocalConfigurationAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): LocalConfigurationAcquireStrategy =
            LocalConfigurationAcquireStrategy(baseConfiguration, this)
    }
}
