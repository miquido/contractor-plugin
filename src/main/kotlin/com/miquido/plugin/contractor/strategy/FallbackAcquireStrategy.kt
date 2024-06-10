package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import org.gradle.api.Project

class FallbackAcquireStrategy(
    private val fallbackStrategies: List<ContractSpecificationAcquireStrategy>
) : ContractSpecificationAcquireStrategy(emptyList(), emptyList(), "") {

    override fun canBeUsed(project: Project): Boolean {
        return fallbackStrategies.map { it.canBeUsed(project) }.any { it }
    }

    override fun registerTasks(project: Project, configuration: ContractorConfiguration) {
        fallbackStrategies.first { it.canBeUsed(project) }.registerTasks(project, configuration)
    }

    override fun prepareTasksOrder(project: Project, dependsOn: String) {
        fallbackStrategies.first { it.canBeUsed(project) }.prepareTasksOrder(project, dependsOn)
    }

    override fun getTasksNames(project: Project) =
        fallbackStrategies.first { it.canBeUsed(project) }.getTasksNames(project)

    override val specificationAcquireTasksOrder = emptyList<String>()

    override fun registerSpecificationAcquireTasks(project: Project) {
        // There is no need for implementation
    }

    override fun prepareSpecificationAcquireTasksOrder(project: Project, dependsOn: String) {
        // There is no need for implementation
    }
}
