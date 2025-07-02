package com.pawrequest.redscript.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.pawrequest.redscript.util.redLog
import java.io.File
import java.util.regex.Pattern

fun contentRootsActivity(project: Project) {
    val projectBasePath = project.basePath ?: return
    val configFile = File(projectBasePath, ".redscript")

    if (!configFile.exists()) return

    val sourceRoots = parseSourceRootsFromConfig(configFile.readText())
    if (sourceRoots.isNotEmpty()) {
        redLog("Adding source roots to project: ${sourceRoots.joinToString(", ")}")
        addSourceRootsToProject(project, sourceRoots)
    }
}

fun parseSourceRootsFromConfig(configContent: String): List<String> {
    val sourceRootsPattern = Pattern.compile("source_roots\\s*=\\s*\\[(.*?)\\]", Pattern.DOTALL)
    val matcher = sourceRootsPattern.matcher(configContent)

    if (matcher.find()) {
        val pathsString = matcher.group(1)
        return pathsString.split(",")
            .map { it.trim() }
            .map { it.removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
    }

    return emptyList()
}
//fun addSourceRootsToProject(project: Project, sourceRoots: List<String>) {
//    val moduleManager = ModuleManager.getInstance(project)
//    val modules = moduleManager.modules
//
//    if (modules.isEmpty()) return
//
//    // Use the first module for simplicity - adjust as needed for your use case
//    val module = modules[0]
//    val rootManager = ModuleRootManager.getInstance(module)
//    val model = rootManager.modifiableModel
//
//    try {
//        val fileSystem = LocalFileSystem.getInstance()
//        for (path in sourceRoots) {
//            val vFile = fileSystem.findFileByPath(path)
//            if (vFile != null) {
//                model.addContentEntry(vFile)
//            }
//        }
//        model.commit()
//    } catch (e: Exception) {
//        model.dispose()
//        throw e
//    }
//}


fun addSourceRootsToProject(project: Project, sourceRoots: List<String>) {
    val moduleManager = ModuleManager.getInstance(project)
    val modules = moduleManager.modules

    if (modules.isEmpty()) return

    ApplicationManager.getApplication().invokeLater {
        ApplicationManager.getApplication().runWriteAction {
            try {
                // Use the first module for simplicity
                val module = modules[0]
                val rootManager = ModuleRootManager.getInstance(module)
                val model = rootManager.modifiableModel

                try {
                    val fileSystem = LocalFileSystem.getInstance()
                    for (path in sourceRoots) {
                        val vFile = fileSystem.findFileByPath(path)
                        if (vFile != null) {
                            redLog("Adding content root: $path")
                            model.addContentEntry(vFile)
                        } else {
                            redLog("Could not find file for path: $path")
                        }
                    }
                    model.commit()
                } catch (e: Exception) {
                    model.dispose()
                    redLog("Error adding content roots: ${e.message}")
                    throw e
                }
            } catch (e: Exception) {
                redLog("Error in write action: ${e.message}")
            }
        }
    }
}