package com.miquido.plugin.openapi.task

import com.miquido.plugin.openapi.Constant
import com.miquido.plugin.openapi.model.OpenApiSpecification
import org.gradle.api.plugins.JavaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

fun generateInterfaceTask(s: OpenApiSpecification): GenerateTask.() -> Unit = {
    Constant.run {
        group = JavaPlugin.CLASSES_TASK_NAME
        generatorName.set("kotlin-spring")
        inputSpec.set("${project.projectDir}/${specificationDir}/${s.path}/${s.fileName}")
        outputDir.set(project.layout.projectDirectory.dir("${interfaceDir}/${s.path}").toString())
        apiPackage.set("${s.basePackage}.api")
        modelPackage.set("${s.basePackage}.dto")
        configOptions.set(
            openApiProperties + mapOf("basePackage" to s.basePackage)
        )
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).sourceSets.getByName("main")
            .kotlin.srcDir(project.layout.projectDirectory.dir("$interfaceDir/${s.path}/src/main/kotlin"))
        templateDir.set("${project.projectDir}/$configurationDir")
    }
}