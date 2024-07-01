package com.miquido.plugin.contractor.file

import com.miquido.plugin.contractor.extension.getAllLinesOfTargetFile
import com.miquido.plugin.contractor.extension.normalizedPath
import com.miquido.plugin.contractor.task.TaskName
import java.nio.file.Path
import org.gradle.api.Project

class SpecificationFilesScanner {

    companion object {
        private val SPECIFICATION_FILE_REFERENCE_PATTERN = "\\\$ref: '(.*)#".toPattern()
        private val SPECIFICATION_FILE_REFERENCE_PREDICATE = SPECIFICATION_FILE_REFERENCE_PATTERN.asPredicate()
    }

    private val listOfAllImportedFiles = mutableSetOf<String>()

    fun importFiles(
        project: Project,
        mainTaskName: TaskName,
        file: Path,
        importFileFunction: (Project, TaskName, Path) -> Unit
    ) {
        val normalizedFilePath = file.normalizedPath()
        if(!listOfAllImportedFiles.contains(normalizedFilePath)) {
            importFileFunction(project, mainTaskName, file)
            listOfAllImportedFiles.add(normalizedFilePath)
            getReferencedSpecificationFilesList(file).forEach { nextFile ->
                this.importFiles(project, mainTaskName, nextFile, importFileFunction)
            }
        }
    }

    private fun getReferencedSpecificationFilesList(parentFilePath: Path): List<Path> {
        return parentFilePath.getAllLinesOfTargetFile()
            .filter { line -> SPECIFICATION_FILE_REFERENCE_PREDICATE.test(line) }
            .map { line ->
                val matcher = SPECIFICATION_FILE_REFERENCE_PATTERN.matcher(line)
                matcher.find()
                matcher.group(1)
            }
            .filter { it.isNotBlank() }
            .map { relativeFilePath ->
                parentFilePath.parent.resolve(relativeFilePath).normalize()
            }
    }
}
