package com.pawrequest.redscript.server
import com.pawrequest.redscript.settings.RedscriptSettings
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
        try {
            println("Fetching Redscript-IDE binary")
            val binaryFile: java.io.File = RedscriptIDEGitHubRepo.getRedscriptIDE()
            println("binaryFile: $binaryFile")
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


