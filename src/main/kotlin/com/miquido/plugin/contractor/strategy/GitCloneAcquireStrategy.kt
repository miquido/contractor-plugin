package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec

class GitCloneAcquireStrategy(
    generatedApiBaseDirectoryList: List<String>,
    specificationSourceDirectoryList: List<String>,
    specificationFileName: String,
    private val gitCloneUrl: String,
    private val repositoryName: String
) : ContractSpecificationAcquireStrategy(
    generatedApiBaseDirectoryList,
    specificationSourceDirectoryList,
    specificationFileName
) {

    private val gitCloneTaskName = "${taskName}GiCloneTask"
    private val copyToSpecificationDirTaskName = "${taskName}CopyToSpecificationDirTask"
    private val deleteGitClonedRepoTaskName = "${taskName}DeleteGitClonedRepoTask"

    override val specificationAcquireTasksOrder = listOf(
        gitCloneTaskName,
        copyToSpecificationDirTaskName,
        deleteGitClonedRepoTaskName
    )

    override fun canBeUsed(project: Project): Boolean {
        return true
    }

    override fun registerSpecificationAcquireTasks(project: Project) {
        project.tasks.register(
            gitCloneTaskName,
            Exec::class.java,
            cloneGitRepository()
        )
        project.tasks.register(
            copyToSpecificationDirTaskName,
            Copy::class.java,
            copyToSpecificationDir()
        )
        project.tasks.register(
            deleteGitClonedRepoTaskName,
            Delete::class.java,
            deleteClonedRepository()
        )

    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String) {
        project.tasks.named(gitCloneTaskName) {
            it.dependsOn(project.tasks.named(dependsOn))
        }
        project.tasks.named(copyToSpecificationDirTaskName) {
            it.dependsOn(project.tasks.named(gitCloneTaskName))
        }
        project.tasks.named(deleteGitClonedRepoTaskName) {
            it.dependsOn(project.tasks.named(copyToSpecificationDirTaskName))
        }
    }

    private fun cloneGitRepository(): Exec.() -> Unit =
        {
            Constant.run {
                workingDir = workingDir.resolve(rootDir)
                workingDir.mkdirs()
                executable = "git"
                args = listOf("clone", gitCloneUrl)
            }
        }

    private fun copyToSpecificationDir(): Copy.() -> Unit = {
        Constant.run {
            val projectDirectory = project.layout.projectDirectory
            from(projectDirectory.dir(rootDir).dir(repositoryName).dir(specificationSourceDirectoryPath).file(specificationFileName))
            into(projectDirectory.dir(specificationDir).dir(specificationSourceDirectoryPath))
        }
    }

    private fun deleteClonedRepository(): Delete.() -> Unit = {
        Constant.run {
            delete(project.layout.projectDirectory.dir(rootDir).dir(repositoryName))
        }
    }
}
