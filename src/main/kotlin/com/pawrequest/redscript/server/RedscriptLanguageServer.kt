package com.pawrequest.redscript.server

import com.pawrequest.redscript.settings.RedscriptSettings
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

private fun platformBinaryName(): String {
    println("getting os.name: ")
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

    return if (osName.contains("win")) {
        "redscript-ide.exe"
    } else if (osName.contains("mac")) {
        "redscript-ide-x86_64-apple-darwin"
    } else if (osName.contains("linux")) {
        "redscript-ide-x86_64-unknown-linux-gnu"
    } else {
        throw UnsupportedOperationException("Unsupported platform: $osName")
    }
}

class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
        try {
            val binaryName: String = platformBinaryName()
            val binaryPath: Path =
                Paths.get(System.getProperty("user.home") + "/.redscript-ide" + File.separator + binaryName)
            val binaryFile: File = binaryPath.toFile()
            println("Redscript binaryFile: ${binaryFile.name}")
            val commandLine = com.intellij.execution.configurations.GeneralCommandLine(binaryFile.absolutePath)
            super.setCommandLine(commandLine)
        } catch (e: java.io.IOException) {
            throw java.lang.RuntimeException(e)
        } catch (e: java.net.URISyntaxException) {
            throw java.lang.RuntimeException(e)
        }
    }


    override fun getInitializationOptions(rootUri: com.intellij.openapi.vfs.VirtualFile?): Any {
        val options: MutableMap<String, Any> = java.util.HashMap()
        options["ui.semanticTokens"] = true
        options["game_dir"] = RedscriptSettings.getInstance().gameDir
        return options
    }
}


