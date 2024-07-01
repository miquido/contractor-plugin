package com.miquido.plugin.contractor.task

import com.miquido.plugin.contractor.extension.named
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

class DependsOnSingleTaskAction(
    private val project: Project,
    private val dependentTaskName: TaskName
): Action<Task> {
    override fun execute(task: Task) {
        task.dependsOn(project.tasks.named(dependentTaskName))
    }
}
