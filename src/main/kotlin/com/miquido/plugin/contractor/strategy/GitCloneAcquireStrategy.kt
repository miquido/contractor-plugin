package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.strategy.configuration.SingleFile
import com.miquido.plugin.contractor.strategy.configuration.toDirectoryPath
import com.miquido.plugin.contractor.util.DependsOnSingleTaskAction
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec

class GitCloneAcquireStrategy(
    baseConfiguration: BaseStrategyConfiguration,
    private val configuration: Configuration
) : ContractSpecificationAcquireStrategy(baseConfiguration) {

    private val gitCloneTaskName = "${taskNamePrefix}GitCloneTask"
    private val copyToSpecificationDirTaskNames = createFilesToTaskNamesMap("CopyToSpecificationDirTask")
    private val deleteGitClonedRepoTaskName = "${taskNamePrefix}DeleteGitClonedRepoTask"

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
        copyToSpecificationDirTaskNames.forEach { (file, taskName) ->
            project.tasks.register(
                taskName,
                Copy::class.java,
                copyToSpecificationDir(file)
            )
        }
        project.tasks.register(
            deleteGitClonedRepoTaskName,
            Delete::class.java,
            deleteClonedRepository()
        )

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

    private fun cloneGitRepository(): Exec.() -> Unit =
        {
            Constant.run {
                workingDir = workingDir.resolve(rootDir)
                workingDir.mkdirs()
                executable = "git"
                args = listOf("clone", configuration.gitCloneUrl, "-b", configuration.branchName)
            }
        }

    private fun copyToSpecificationDir(file: SingleFile): Copy.() -> Unit = {
        Constant.run {
            val projectDirectory = project.layout.projectDirectory
            val specificationSourceDirectoryPath = file.directoryList.toDirectoryPath()
            from(projectDirectory.dir(rootDir).dir(configuration.repositoryName).dir(specificationSourceDirectoryPath).file(file.fileFullName))
            into(projectDirectory.dir(specificationDir).dir(specificationSourceDirectoryPath))
        }
    }

    private fun deleteClonedRepository(): Delete.() -> Unit = {
        Constant.run {
            delete(project.layout.projectDirectory.dir(rootDir).dir(configuration.repositoryName))
        }
    }

    data class Configuration (
        val gitCloneUrl: String,
        val repositoryName: String,
        val branchName: String
    ): ContractSpecificationAcquireStrategy.Configuration<GitCloneAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): GitCloneAcquireStrategy =
            GitCloneAcquireStrategy(baseConfiguration, this)
    }
}
