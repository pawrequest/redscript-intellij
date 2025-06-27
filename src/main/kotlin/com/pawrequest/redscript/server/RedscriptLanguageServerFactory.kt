package com.pawrequest.redscript.server

import com.intellij.openapi.project.Project
import com.pawrequest.redscript.util.logInfo
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import java.io.File

fun gameDirValid(gameDir: String): Boolean =
    gameDir.isNotEmpty() && File(gameDir).exists() && File(gameDir).isDirectory && File(
        gameDir, "bin/x64/Cyberpunk2077.exe"
    ).exists()


class RedscriptLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        logInfo("\nCreating Redscript Language Server connection provider")
        return RedscriptLanguageServer()
    }
}

