package com.miquido.plugin.contractor.extension

import com.miquido.plugin.contractor.task.TaskName
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

fun <T: Task> TaskContainer.register(taskName: TaskName, type: Class<T>, configurationAction: Action<T>): TaskProvider<T> =
    this.register(taskName.getFullValue(), type, configurationAction)

fun TaskContainer.register(taskName: TaskName, configurationAction: Action<Task>): TaskProvider<Task> =
    this.register(taskName.getFullValue(), configurationAction)

fun TaskContainer.named(taskName: TaskName, configurationAction: Action<Task>): TaskProvider<Task> =
    this.named(taskName.getFullValue(), configurationAction)

fun <T: Task> TaskContainer.create(taskName: TaskName, type: Class<T>, configurationAction: Action<T>): T =
    this.create(taskName.getFullValue(), type, configurationAction)
