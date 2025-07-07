package com.pawrequest.redscript.debug

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.fileTypes.FileType
import com.pawrequest.redscript.lang.RedscriptFileType
import com.redhat.devtools.lsp4ij.dap.DebugMode
import com.redhat.devtools.lsp4ij.dap.client.LaunchUtils
import com.redhat.devtools.lsp4ij.dap.configurations.options.FileOptionConfigurable
import com.redhat.devtools.lsp4ij.dap.configurations.options.WorkingDirectoryConfigurable
import com.redhat.devtools.lsp4ij.dap.definitions.DebugAdapterServerDefinition
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptorFactory
import com.redhat.devtools.lsp4ij.dap.descriptors.ServerReadyConfig
import java.nio.file.Path
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptorFactory.getDebugAdapterServerPath


class RedscriptDebugDescriptor(
    options: RunConfigurationOptions,
    env: ExecutionEnvironment,
    def: DebugAdapterServerDefinition?
) : DebugAdapterDescriptor(options, env, def) {

    companion object {
        private val SERVER: Path =
            getDebugAdapterServerPath("my.plugin.id", "bin/debugpy_adapter.py")
    }

    @Throws(ExecutionException::class)
    override fun startServer(): ProcessHandler {
        val cmd = createStartServerCommandLine("python $SERVER \${port}")
        return startServer(cmd)
    }

    override fun getDapParameters(): Map<String, Any> {
        val file = (options as FileOptionConfigurable).file
        val cwd  = (options as WorkingDirectoryConfigurable).workingDirectory
        val json = """
      {
        "type":"python",
        "request":"launch",
        "name":"Launch file",
        "program":"$file",
        "cwd":"$cwd"
      }
    """.trimIndent()
        return LaunchUtils.getDapParameters(json, LaunchUtils.LaunchContext(file, cwd))
    }

    override fun getServerReadyConfig(p0: DebugMode): ServerReadyConfig =
        ServerReadyConfig("Listening on port")

    override fun getFileType(): FileType? {
        return RedscriptFileType
    }
}