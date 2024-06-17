package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.Constant
import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

abstract class ContractSpecificationAcquireStrategy(
    generatedApiBaseDirectoryList: List<String>,
    specificationSourceDirectoryList: List<String>,
    val specificationFileName: String,
) {

    protected val taskName = specificationSourceDirectoryList.joinToString("") { it.capitalized() }
    private val generateTaskName = "${taskName}GenerateTask"
    protected val specificationSourceDirectoryPath = specificationSourceDirectoryList.joinToString("/")
    protected val specificationSourceDirectoryPackages = specificationSourceDirectoryList.joinToString(".")
    protected val generatedApiBaseDirectoryPath = generatedApiBaseDirectoryList.joinToString("/")
    protected val generatedApiBaseDirectoryPackages = generatedApiBaseDirectoryList.joinToString(".")
    protected val generatedApiDirectoryPackages = "${generatedApiBaseDirectoryPackages}.${specificationSourceDirectoryPackages}"

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
        project.tasks.named(generateTaskName) {
            it.dependsOn(lastContractTaskName)
        }
    }

    open fun getTasksNames(project: Project) = specificationAcquireTasksOrder + generateTaskName

    private fun generateInterfaceTask(
        configuration: ContractorConfiguration
    ): GenerateTask.() -> Unit = {
        Constant.run {
            group = JavaPlugin.CLASSES_TASK_NAME
            generatorName.set(configuration.generatorName)
            inputSpec.set("${project.projectDir}/$specificationDir/$specificationSourceDirectoryPath/${specificationFileName}")
            outputDir.set(
                project.layout.projectDirectory
                    .dir("$interfaceDir/$specificationSourceDirectoryPath")
                    .toString()
            )
            apiPackage.set("${generatedApiDirectoryPackages}.api")
            modelPackage.set("${generatedApiDirectoryPackages}.dto")
            configOptions.set(
                defaultConfigOptions + configuration.configOptions + mapOf("basePackage" to generatedApiDirectoryPackages)
            )
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
                    project.layout.projectDirectory.dir("$interfaceDir/$specificationSourceDirectoryPath/src/main/kotlin")
                )
            project.extensions.getByType(JavaPluginExtension::class.java)
                .sourceSets.getByName("main")
                .java.srcDir(
                    project.layout.projectDirectory.dir("$interfaceDir/$specificationSourceDirectoryPath/src/main/java")
                )
            templateDir.set("${project.projectDir}/$configurationDir")
        }
    }
}
