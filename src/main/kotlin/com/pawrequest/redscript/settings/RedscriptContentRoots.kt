package com.pawrequest.redscript.settings

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.pawrequest.redscript.util.redLog
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Paths.get
import java.util.regex.Pattern

fun contentRootsActivity(project: Project) {
    val projectBasePath = project.basePath ?: return
    val configFile = File(projectBasePath, ".redscript")

    if (!configFile.exists()) return

    val sourceDirStrings = parseSourceRootDirsFromConfig(configFile.readText())
    val sourcePaths = sourceDirStrings.map { resolveAbsolutePath(it, projectBasePath) }
    val sourceFiles = sourcePaths.map { it.toFile() }
    val filteredFiles1 = sourceFiles.filter{it.exists() && it.isDirectory}

    for (dir in sourceFiles - filteredFiles1) {
        notifyRedscriptProject(project, "Source directory not valid: ${dir.absolutePath}", NotificationType.WARNING)
    }
    if (filteredFiles1.isNotEmpty()) {
        addSourceRootDirsToProject(project, filteredFiles1)
        val successMessage = "Added source roots from .redscript file to Workspace:\n${filteredFiles1.joinToString("\n")}"
        redLog(successMessage)
        notifyRedscriptProject(project, successMessage)
    }
}

fun parseSourceRootDirsFromConfig(configContent: String): List<String> {
    val sourceRootsPattern = Pattern.compile("source_roots\\s*=\\s*\\[(.*?)\\]", Pattern.DOTALL)
    val matcher = sourceRootsPattern.matcher(configContent)

    if (matcher.find()) {
        val pathsString = matcher.group(1)
        val res = pathsString.split(",")
            .map { it.trim() }
            .map { it.removeSurrounding("\"") }.filter { it.isNotEmpty() }
        redLog("Parsed source roots from config: ${res.joinToString(", ")}")
        return res
    }
    return emptyList()
}

fun filterSourceDirs(sourceDirs: List<Path>): List<String> {
    return sourceDirs.map { it.toFile() }.filter { it.exists() && it.isDirectory }.map { it.absolutePath }
}

fun filterSourceDirs1(projectBasePath: String, sourceDirs: List<String>): List<String> {
    val res = mutableListOf<String>()
    for (dir in sourceDirs) {
//        val path = Paths.get(dir)
        val path = if (Paths.get(dir).isAbsolute) Paths.get(dir) else Paths.get(projectBasePath, dir)
        val file = path.toFile()
        if (file.exists() && file.isDirectory) {
            redLog("Valid source directory: $dir")
            res.add(file.absolutePath)
        } else {
            redLog("Invalid source directory: $dir")
        }
    }
    return res
}


fun resolveAbsolutePath(path: String, projectBasePath: String): Path {
    return if (get(path).isAbsolute) get(path) else get(projectBasePath, path)

}

fun addSourceRootDirsToProject(project: Project, sourceRoots: List<File>) {
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
                    for (directory in sourceRoots) {
                        val vFile = fileSystem.findFileByPath(directory.toString())
                        if (vFile != null) {
                            redLog("Adding content root: $directory")
                            model.addContentEntry(vFile)
                        } else {
                            redLog("Could not find file for path: $directory")
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


//fun addSourceRootPathsToProject(project: Project, sourceRoots: List<Path>) {
//    val moduleManager = ModuleManager.getInstance(project)
//    val modules = moduleManager.modules
//    if (modules.isEmpty()) return
//
//    ApplicationManager.getApplication().invokeLater {
//        ApplicationManager.getApplication().runWriteAction {
//            try {
//                // Use the first module for simplicity
//                val module = modules[0]
//                val rootManager = ModuleRootManager.getInstance(module)
//                val model = rootManager.modifiableModel
//
//                try {
//                    val fileSystem = LocalFileSystem.getInstance()
//                    for (path in sourceRoots) {
//                        val vFile = fileSystem.findFileByPath(path.toString())
//                        if (vFile != null) {
//                            redLog("Adding content root: $path")
//                            model.addContentEntry(vFile)
//                        } else {
//                            redLog("Could not find file for path: $path")
//                        }
//                    }
//                    model.commit()
//                } catch (e: Exception) {
//                    model.dispose()
//                    redLog("Error adding content roots: ${e.message}")
//                    throw e
//                }
//            } catch (e: Exception) {
//                redLog("Error in write action: ${e.message}")
//            }
//        }
//    }
//}


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


//fun addSourceRootsToProject(project: Project, sourceRoots: List<String>) {
//    val moduleManager = ModuleManager.getInstance(project)
//    val modules = moduleManager.modules
//
//    if (modules.isEmpty()) return
//
//    ApplicationManager.getApplication().invokeLater {
//        ApplicationManager.getApplication().runWriteAction {
//            try {
//                // Use the first module for simplicity
//                val module = modules[0]
//                val rootManager = ModuleRootManager.getInstance(module)
//                val model = rootManager.modifiableModel
//
//                try {
//                    val fileSystem = LocalFileSystem.getInstance()
//                    for (path in sourceRoots) {
//                        val vFile = fileSystem.findFileByPath(path)
//                        if (vFile != null) {
//                            redLog("Adding content root: $path")
//                            model.addContentEntry(vFile)
//                        } else {
//                            redLog("Could not find file for path: $path")
//                        }
//                    }
//                    model.commit()
//                } catch (e: Exception) {
//                    model.dispose()
//                    redLog("Error adding content roots: ${e.message}")
//                    throw e
//                }
//            } catch (e: Exception) {
//                redLog("Error in write action: ${e.message}")
//            }
//        }
//    }
//}


//fun checkFilterSourceRoots(sourceRoots: List<String>): List<Path> {
//    val checkedPaths = sourceRoots.map { it.trim() }.filter { it.isNotEmpty() && File(it).isDirectory }.map { Path(it) }
//    if (checkedPaths.isEmpty()) {
//        redLog("No valid source roots found in the provided list.")
//    } else {
//        redLog("Valid source roots: ${checkedPaths.joinToString(", ")}")
//    }
//    return checkedPaths
//}
//
//fun parseSourceRootsFromConfig(configContent: String): List<String> {
//    val sourceRootsPattern = Pattern.compile("source_roots\\s*=\\s*\\[(.*?)\\]", Pattern.DOTALL)
//    val matcher = sourceRootsPattern.matcher(configContent)
//
//    if (matcher.find()) {
//        val pathsString = matcher.group(1)
//        return pathsString.split(",")
//            .map { it.trim() }
//            .map { it.removeSurrounding("\"") }
//            .filter { it.isNotEmpty() }
//    }
//
//    return emptyList()
//}