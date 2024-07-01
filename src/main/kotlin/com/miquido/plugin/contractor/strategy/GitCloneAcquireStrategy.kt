package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.extension.create
import com.miquido.plugin.contractor.extension.dir
import com.miquido.plugin.contractor.extension.file
import com.miquido.plugin.contractor.extension.named
import com.miquido.plugin.contractor.extension.register
import com.miquido.plugin.contractor.extension.resolve
import com.miquido.plugin.contractor.file.SpecificationFilesScanner
import com.miquido.plugin.contractor.task.TaskName
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.task.DependsOnSingleTaskAction
import com.miquido.plugin.contractor.task.ExecutableCopyAction
import java.nio.file.Path
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec

class GitCloneAcquireStrategy(
    baseConfiguration: BaseStrategyConfiguration,
    private val configuration: Configuration
) : ContractSpecificationAcquireStrategy(baseConfiguration) {

    private val gitCloneTaskName = TaskName(taskNamePrefix, "GitCloneTask")
    private val copyToSpecificationDirTaskNames = createFilesToTaskNamesMap("CopyToSpecificationDirTask")
    private val deleteGitClonedRepoTaskName = TaskName(taskNamePrefix, "DeleteGitClonedRepoTask")

    override val specificationAcquireTasksOrder = buildList {
        this.add(gitCloneTaskName)
        this.addAll(copyToSpecificationDirTaskNames.values)
        this.add(deleteGitClonedRepoTaskName)
    }

    override fun canBeUsed(project: Project): Boolean {
        return true
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        project.tasks.register(
            gitCloneTaskName,
            Exec::class.java,
            cloneGitRepository()
        )
        val specificationFilesScanner = SpecificationFilesScanner()
        copyToSpecificationDirTaskNames.forEach { (filePath, taskName) ->
            project.tasks.register(taskName) { task ->
                task.doLast {
                    specificationFilesScanner.importFiles(
                        project, taskName, filePath, this::copyFile
                    )
                }
            }
        }
        project.tasks.register(
            deleteGitClonedRepoTaskName,
            Delete::class.java,
            deleteClonedRepository()
        )
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

    private fun cloneGitRepository(): Exec.() -> Unit =
        {
            Constant.run {
                workingDir = workingDir.resolve(rootDir)
                workingDir.mkdirs()
                executable = "git"
                args = listOf("clone", configuration.gitCloneUrl, "-b", configuration.branchName)
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
                        .dir(rootDir)
                        .dir(configuration.repositoryName)
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

    private fun deleteClonedRepository(): Delete.() -> Unit = {
        Constant.run {
            delete(project.layout.projectDirectory.dir(rootDir).dir(configuration.repositoryName))
        }
    }

    data class Configuration @JvmOverloads constructor(
        val gitCloneUrl: String,
        val repositoryName: String,
        val branchName: String = "main"
    ): ContractSpecificationAcquireStrategy.Configuration<GitCloneAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): GitCloneAcquireStrategy =
            GitCloneAcquireStrategy(baseConfiguration, this)
    }
}
