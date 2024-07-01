package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.extension.create
import com.miquido.plugin.contractor.extension.dir
import com.miquido.plugin.contractor.extension.file
import com.miquido.plugin.contractor.extension.named
import com.miquido.plugin.contractor.extension.register
import com.miquido.plugin.contractor.file.SpecificationFilesScanner
import com.miquido.plugin.contractor.task.TaskName
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.task.DependsOnSingleTaskAction
import com.miquido.plugin.contractor.task.ExecutableCopyAction
import java.nio.file.Path
import org.gradle.api.Project

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
        val specificationFilesScanner = SpecificationFilesScanner()
        copyFromLocalTaskNames.forEach { (filePath, taskName) ->
            project.tasks.register(taskName) { task ->
                task.doLast {
                    specificationFilesScanner.importFiles(
                        project, taskName, filePath, this::copyFile
                    )
                }
            }
        }
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: TaskName) {
        var dependentTaskName = dependsOn
        specificationAcquireTasksOrder.forEach { taskName ->
            project.tasks.named(
                taskName,
                DependsOnSingleTaskAction(project, dependentTaskName)
            )
            dependentTaskName = taskName
        }
    }

    private fun copyFile(
        project: Project,
        mainTaskName: TaskName,
        filePath: Path
    ) {
        project.tasks.create(
            mainTaskName.with(filePath),
            ExecutableCopyAction::class.java
        ) {
            Constant.run {
                val projectDirectory = project.layout.projectDirectory
                it.from(
                    projectDirectory
                        .dir(configuration.relativePath!!)
                        .file(filePath)
                )
                it.into(
                    projectDirectory
                        .dir(specificationDir)
                        .dir(filePath.parent)
                )
            }
        }.execute()
    }

    data class Configuration constructor(
        val relativePath: String?
    ) : ContractSpecificationAcquireStrategy.Configuration<LocalConfigurationAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): LocalConfigurationAcquireStrategy =
            LocalConfigurationAcquireStrategy(baseConfiguration, this)
    }
}
