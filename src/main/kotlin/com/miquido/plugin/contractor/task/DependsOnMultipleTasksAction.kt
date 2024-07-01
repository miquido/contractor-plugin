package com.miquido.plugin.contractor.task

import com.miquido.plugin.contractor.extension.named
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

class DependsOnMultipleTasksAction(
    private val project: Project,
    private val dependentTaskNames: List<TaskName>
): Action<Task> {
    override fun execute(task: Task) {
        dependentTaskNames.forEach {
            task.dependsOn(project.tasks.named(it))
        }
    }
}
