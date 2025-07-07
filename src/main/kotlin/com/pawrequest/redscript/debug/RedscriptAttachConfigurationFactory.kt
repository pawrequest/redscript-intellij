package com.pawrequest.redscript.debug

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class RedscriptAttachConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun createTemplateConfiguration(p0: Project): RunConfiguration =
        RedscriptAttachRunConfiguration(p0, this, "Redscript Attach")
}