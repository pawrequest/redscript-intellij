package com.pawrequest.redscript.server

import com.pawrequest.redscript.settings.RedscriptSettings
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
        val binaryFile = getRedscriptIDEBinaryPath().toFile()
        val commandLine = com.intellij.execution.configurations.GeneralCommandLine(binaryFile.absolutePath)
        super.setCommandLine(commandLine)

    }
    override fun getInitializationOptions(rootUri: com.intellij.openapi.vfs.VirtualFile?): Any {
        val options: MutableMap<String, Any> = java.util.HashMap()
        options["ui.semanticTokens"] = true
        options["game_dir"] = RedscriptSettings.getInstance().gameDir
        return options
    }
}


