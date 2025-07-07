package com.pawrequest.redscript.debug

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment

// RedscriptAttachRunProfileState.kt
class RedscriptAttachRunProfileState(
    environment: ExecutionEnvironment,
    private val config: RedscriptAttachRunConfiguration
) : CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        val port = config.port ?: throw ExecutionException("No port selected")
        // Start a DAP client attached to the port
        // (You may need to use a plugin or library for DAP support)
        // For demonstration, just connect to the port:
        val commandLine = GeneralCommandLine("your-dap-client", "--port", port.toString())
        return KillableProcessHandler(commandLine)
    }
}