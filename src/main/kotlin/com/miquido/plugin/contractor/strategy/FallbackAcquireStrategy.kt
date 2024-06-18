package com.miquido.plugin.contractor.strategy

import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import org.gradle.api.Project

class FallbackAcquireStrategy(
    baseConfiguration: BaseStrategyConfiguration,
    configuration: Configuration
) : ContractSpecificationAcquireStrategy(baseConfiguration) {

    private val fallbackStrategies: List<ContractSpecificationAcquireStrategy> = configuration.strategyConfigurations.map{
        it.createStrategy(baseConfiguration)
    }

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

    data class Configuration (
        val strategyConfigurations: List<ContractSpecificationAcquireStrategy.Configuration<*>>
    ): ContractSpecificationAcquireStrategy.Configuration<FallbackAcquireStrategy> {
        override fun createStrategy(baseConfiguration: BaseStrategyConfiguration): FallbackAcquireStrategy =
            FallbackAcquireStrategy(baseConfiguration, this)
    }
}
