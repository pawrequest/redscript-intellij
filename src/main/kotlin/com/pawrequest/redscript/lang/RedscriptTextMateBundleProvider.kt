package com.pawrequest.redscript.lang

import com.intellij.openapi.application.PathManager
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider.PluginBundle
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*



// Bruno's approach
class RedscriptTextMateBundleProvider : TextMateBundleProvider {
    override fun getBundles(): List<PluginBundle> {
        try {
            val redscriptBundleTmpDir: Path =
                Files.createTempDirectory(Path.of(PathManager.getTempPath()), "textmate")
            for (fileToCopy in listOf(
                "package.json",
                "language-configuration.json",
                "syntaxes/redscript.tmLanguage.json",
                "images/icon_rs_16.png",
            )) {
                val resource: URL? =
                    RedscriptTextMateBundleProvider::class.java.classLoader.getResource("textmate/$fileToCopy")
                Objects.requireNonNull(resource)?.openStream().use { resourceStream ->
                    val target: Path = redscriptBundleTmpDir.resolve(fileToCopy)
                    Files.createDirectories(target.parent)
                    if (resourceStream != null) {
                        Files.copy(resourceStream, target)
                    }
                    else {
                        throw IOException("Failed to copy resource $fileToCopy")
                    }
                }
            }
            val redscriptBundle = PluginBundle("Redscript", Objects.requireNonNull(redscriptBundleTmpDir))
            return listOf(redscriptBundle)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
