package com.pawrequest.redscript.server

import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

fun getPlatformBinaryName(): String {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        osName.contains("win") -> "redscript-ide.exe"
        osName.contains("mac") -> "redscript-ide-x86_64-apple-darwin"
        osName.contains("linux") -> "redscript-ide-x86_64-unknown-linux-gnu"
        else -> throw UnsupportedOperationException("Unsupported platform: $osName")
    }
}

fun getCacheDir(): Path {
    return Paths.get(System.getProperty("user.home"), ".redscript-ide")
}

fun redscriptIdeLocalPath(): Path =
    getCacheDir().resolve(getPlatformBinaryName())

fun getRedscriptIdeVersion(): String =
    object {}.javaClass.classLoader.getResourceAsStream("redscript-ide-version.txt")
        ?.bufferedReader()?.readText()?.trim()
        ?: error("redscript-ide-version.txt not found")



fun maybeDownloadRedscriptIDE(): File {
    val binaryFile = redscriptIdeLocalPath().toFile()
    if (!binaryFile.exists()) {
        val version = getRedscriptIdeVersion()
        val binaryName = getPlatformBinaryName()
        val url = "https://github.com/jac3km4/redscript-ide/releases/download/$version/$binaryName"
        println("Downloading Redscript binary '$binaryName' version $version from: $url to ${binaryFile.absolutePath}")
        Files.createDirectories(getCacheDir())
        URI(url).toURL().openStream().use { input ->
            FileOutputStream(binaryFile).use { output ->
                input.copyTo(output)
            }
        }
        binaryFile.setExecutable(true)
    } else {
        println("Redscript IDE binary already exists at: ${binaryFile.absolutePath}")
    }

    return binaryFile
}

