package com.pawrequest.redscript.debug

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project


class RedscriptAttachRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<Any>(project, factory, name) {
    var port: Int? = null

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return RedscriptAttachSettingsEditor()
    }

    override fun checkConfiguration() {
    }

    override fun getState(p0: com.intellij.execution.Executor, environment: ExecutionEnvironment): RunProfileState? {
        return RedscriptAttachRunProfileState(environment, this)
    }
}