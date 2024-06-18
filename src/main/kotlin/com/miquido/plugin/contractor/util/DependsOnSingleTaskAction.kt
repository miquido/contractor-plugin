package com.miquido.plugin.contractor.util

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

class DependsOnSingleTaskAction(
    private val project: Project,
    private val dependentTaskName: String
): Action<Task> {
    override fun execute(task: Task) {
        task.dependsOn(project.tasks.named(dependentTaskName))
    }
}
