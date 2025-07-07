package com.pawrequest.redscript.debug



import com.intellij.execution.runners.ExecutionEnvironment
import com.redhat.devtools.lsp4ij.dap.configurations.DAPRunConfigurationOptions
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptorFactory

class RedscriptDebugDescriptorFactory : DebugAdapterDescriptorFactory() {
    override fun createDebugAdapterDescriptor(
        options: DAPRunConfigurationOptions,
        env: ExecutionEnvironment
    ): DebugAdapterDescriptor = RedscriptDebugDescriptor(options, env, serverDefinition)
}





//import com.intellij.execution.runners.ExecutionEnvironment
//import com.redhat.devtools.lsp4ij.dap.configurations.DAPRunConfigurationOptions
//import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
//import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptorFactory


//class MyDebugAdapterDescriptorFactory : DebugAdapterDescriptorFactory() {
//    override fun createDebugAdapterDescriptor(
//        o: DAPRunConfigurationOptions,
//        env: ExecutionEnvironment
//    ): DebugAdapterDescriptor {
//        return MyDebugAdapterDescriptor(o, env, getServerDefinition())
//    }
//}

