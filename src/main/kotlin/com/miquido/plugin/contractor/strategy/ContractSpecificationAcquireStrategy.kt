package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.strategy.configuration.toDirectoryPath
import com.miquido.plugin.contractor.strategy.configuration.toPackagesPath
import com.miquido.plugin.contractor.util.DependsOnSingleTaskAction
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

abstract class ContractSpecificationAcquireStrategy(
    private val baseConfiguration: BaseStrategyConfiguration
) {

    protected val taskNamePrefix = baseConfiguration.mainSpecificationFilePath.toCapitalizedCamelCase()

    private val generateTaskName = "${taskNamePrefix}GenerateTask"

    private val allSpecificationFiles = baseConfiguration.additionalSpecificationFilePaths.flatMap { it.toSingleFileList() } +
        baseConfiguration.mainSpecificationFilePath

    protected abstract val specificationAcquireTasksOrder: List<String>
    abstract fun canBeUsed(project: Project): Boolean
    protected abstract fun registerSpecificationAcquireTasks(project: Project)
    protected abstract fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String)

    open fun registerTasks(project: Project, configuration: ContractorConfiguration) {
        registerSpecificationAcquireTasks(project)
        project.tasks.register(
            generateTaskName,
            GenerateTask::class.java,
            generateInterfaceTask(configuration)
        )
    }

    open fun prepareTasksOrder(project: Project, dependsOn: String) {
        prepareSpecificationAcquireTasksOrder(project, dependsOn)
        val lastContractTaskName = specificationAcquireTasksOrder.last()
        project.tasks.named(
            generateTaskName,
            DependsOnSingleTaskAction(project, lastContractTaskName)
        )
    }

    open fun getTasksNames(project: Project) = specificationAcquireTasksOrder + generateTaskName

    protected fun createFilesToTaskNamesMap(taskNameSuffix: String) = allSpecificationFiles.associateWith{ singleFile ->
        "${taskNamePrefix}${singleFile.toCapitalizedCamelCase()}${taskNameSuffix}"
    }

    private fun generateInterfaceTask(
        configuration: ContractorConfiguration
    ): GenerateTask.() -> Unit = {
        val mainSpecificationSourceDirectoryPath = baseConfiguration.mainSpecificationFilePath.directoryList.toDirectoryPath()
        val apiGenerationTargetDirectoryPackages = baseConfiguration.apiGenerationTargetDirectoryList.toPackagesPath()
        Constant.run {
            group = JavaPlugin.CLASSES_TASK_NAME
            generatorName.set(configuration.generatorName)
            inputSpec.set("${project.projectDir}/$specificationDir/$mainSpecificationSourceDirectoryPath/${baseConfiguration.mainSpecificationFilePath.fileFullName}")
            outputDir.set(
                project.layout.projectDirectory
                    .dir("$interfaceDir/$mainSpecificationSourceDirectoryPath")
                    .toString()
            )
            apiPackage.set("${apiGenerationTargetDirectoryPackages}.api")
            modelPackage.set("${apiGenerationTargetDirectoryPackages}.dto")
            configOptions.set(
                defaultConfigOptions + configuration.configOptions + mapOf("basePackage" to apiGenerationTargetDirectoryPackages)
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
                    project.layout.projectDirectory.dir("$interfaceDir/$mainSpecificationSourceDirectoryPath/src/main/kotlin")
                )
            project.extensions.getByType(JavaPluginExtension::class.java)
                .sourceSets.getByName("main")
                .java.srcDir(
                    project.layout.projectDirectory.dir("$interfaceDir/$mainSpecificationSourceDirectoryPath/src/main/java")
                )
            templateDir.set("${project.projectDir}/$configurationDir")
        }
    }

    interface Configuration<T: ContractSpecificationAcquireStrategy> {
        fun createStrategy(baseConfiguration: BaseStrategyConfiguration): T
    }
}
