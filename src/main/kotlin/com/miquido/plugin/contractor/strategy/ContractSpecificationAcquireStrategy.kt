package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import com.miquido.plugin.contractor.extension.dir
import com.miquido.plugin.contractor.extension.file
import com.miquido.plugin.contractor.extension.named
import com.miquido.plugin.contractor.extension.register
import com.miquido.plugin.contractor.extension.toCapitalizedCamelCase
import com.miquido.plugin.contractor.task.TaskName
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.task.DependsOnSingleTaskAction
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

abstract class ContractSpecificationAcquireStrategy(
    private val baseConfiguration: BaseStrategyConfiguration
) {

    protected val taskNamePrefix = baseConfiguration.mainSpecificationFilePath.toCapitalizedCamelCase()

    private val generateTaskName = TaskName(taskNamePrefix, "GenerateTask")

    private val allSpecificationFiles = buildList {
        addAll(baseConfiguration.additionalSpecificationFilePaths)
        add(baseConfiguration.mainSpecificationFilePath)
    }

    protected abstract val specificationAcquireTasksOrder: List<TaskName>
    abstract fun canBeUsed(project: Project): Boolean
    protected abstract fun registerSpecificationAcquireTasks(project: Project)
    protected abstract fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: TaskName)

    open fun registerTasks(project: Project, configuration: ContractorConfiguration) {
        registerSpecificationAcquireTasks(project)
        project.tasks.register(
            generateTaskName,
            GenerateTask::class.java,
            generateInterfaceTask(configuration)
        )
    }

    open fun prepareTasksOrder(project: Project, dependsOn: TaskName) {
        prepareSpecificationAcquireTasksOrder(project, dependsOn)
        val lastContractTaskName = specificationAcquireTasksOrder.last()
        project.tasks.named(
            generateTaskName,
            DependsOnSingleTaskAction(project, lastContractTaskName)
        )
    }

    open fun getTasksNames(project: Project) = specificationAcquireTasksOrder + generateTaskName

    protected fun createFilesToTaskNamesMap(taskNameSuffix: String) = allSpecificationFiles.associateWith{ filePath ->
        TaskName(
            taskNamePrefix,
            filePath.toCapitalizedCamelCase(),
            taskNameSuffix
        )
    }

    private fun generateInterfaceTask(
        configuration: ContractorConfiguration
    ): GenerateTask.() -> Unit = {
        Constant.run {
            group = JavaPlugin.CLASSES_TASK_NAME
            generatorName.set(configuration.generatorName)
            inputSpec.set(
                project.layout.projectDirectory
                    .dir(specificationDir)
                    .file(baseConfiguration.mainSpecificationFilePath)
                    .toString()
            )
            outputDir.set(
                project.layout.projectDirectory
                    .dir(rootDir)
                    .dir(baseConfiguration.mainSpecificationFilePath.parent)
                    .toString()
            )
            apiPackage.set("${baseConfiguration.apiGenerationTargetPackagePath}.api")
            modelPackage.set("${baseConfiguration.apiGenerationTargetPackagePath}.dto")
            configOptions.set(
                defaultConfigOptions + configuration.configOptions + mapOf("basePackage" to baseConfiguration.apiGenerationTargetPackagePath)
            )
            skipValidateSpec.set(configuration.skipValidateSpec)
            importMappings.set(
                configuration.importMappings
            )
            typeMappings.set(
                configuration.typeMappings

            )
            project.extensions
                .getByType(KotlinJvmProjectExtension::class.java)
                .sourceSets.getByName("main")
                .kotlin.srcDir(
                    project.layout.projectDirectory
                        .dir(rootDir)
                        .dir(baseConfiguration.mainSpecificationFilePath.parent)
                        .dir("src")
                        .dir("main")
                        .dir("kotlin")
                )
            project.extensions.getByType(JavaPluginExtension::class.java)
                .sourceSets.getByName("main")
                .java.srcDir(
                    project.layout.projectDirectory
                        .dir(rootDir)
                        .dir(baseConfiguration.mainSpecificationFilePath.parent)
                        .dir("src")
                        .dir("main")
                        .dir("java")
                )
        }
    }

    interface Configuration<T: ContractSpecificationAcquireStrategy> {
        fun createStrategy(baseConfiguration: BaseStrategyConfiguration): T
    }
}
