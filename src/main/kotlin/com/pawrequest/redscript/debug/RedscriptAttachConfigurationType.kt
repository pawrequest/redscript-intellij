package com.pawrequest.redscript.debug

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.util.IconLoader

class RedscriptAttachConfigurationType : ConfigurationType {
    override fun getDisplayName() = "Redscript Attach"
    override fun getConfigurationTypeDescription() = "Attach to Cyberpunk 2077 (Redscript DAP)"
    override fun getId() = "REDSCRIPT_ATTACH"
    override fun getConfigurationFactories(): Array<out ConfigurationFactory?>? = arrayOf(RedscriptAttachConfigurationFactory(this))
    override fun getIcon() = IconLoader.getIcon("/textmate/images/icon_rs_16.png", javaClass)
}