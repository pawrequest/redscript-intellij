package com.pawrequest.redscript.server

import com.intellij.openapi.project.Project
import com.pawrequest.redscript.util.redLog
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider


class RedscriptLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        redLog("\nCreating Redscript Language Server connection provider")
        return RedscriptLanguageServer()
    }
}

