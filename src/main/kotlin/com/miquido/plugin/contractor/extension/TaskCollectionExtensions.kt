package com.miquido.plugin.contractor.extension

import com.miquido.plugin.contractor.task.TaskName
import org.gradle.api.Task
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskProvider

fun <T: Task> TaskCollection<T>.named(taskName: TaskName): TaskProvider<T> =
    this.named(taskName.getFullValue())
