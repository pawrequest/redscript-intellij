package com.pawrequest.redscript.lang

import com.intellij.openapi.application.PathManager
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider.PluginBundle
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


//class RedscriptTextMateBundleProvider : TextMateBundleProvider {
//    override fun getBundles(): List<PluginBundle> {
//        print("GETTING TEXTMATEDIR")
//        print("GETTING TEXTMATEDIR")
//        print("GETTING TEXTMATEDIR")
//        print("GETTING TEXTMATEDIR")
//        print("GETTING TEXTMATEDIR")
//        print("GETTING TEXTMATEDIR")
//        val textmateDir = PluginPathManager.getPluginResource(javaClass, "textmate") ?: return emptyList()
//        print("textmateDir: $textmateDir \n")
//        val path = textmateDir.toPath()
//        print("path: $path \n")
//        return listOf(PluginBundle("Redscript", path))
//    }
//}


//class RedscriptTextMateBundleProvider : TextMateBundleProvider {
//    override fun getBundles(): List<PluginBundle> {
//        print("GETTING TEXTMATEDIR\n")
//        print("GETTING TEXTMATEDIR\n")
//        print("GETTING TEXTMATEDIR\n")
//        print("GETTING TEXTMATEDIR\n")
//        print("GETTING TEXTMATEDIR\n")
//        val tmPathStr = getPluginResource(javaClass, "textmate")
//        print("tmPathStr: $tmPathStr \n")
//        val tmPath = tmPathStr?.toPath() ?: throw IllegalStateException("Failed to locate TextMate bundle directory")
////        val packageJson = tmPath.resolve("package.json").exists()
////        if (!packageJson) {
////            throw IllegalStateException("Failed to locate package.json in TextMate bundle directory")
////        }
//        print("GETTING TEXTMATEDIR\n")
//        return listOf(PluginBundle("redscript", tmPath))
//    }
//}

//class RedscriptTextMateBundleProvider : TextMateBundleProvider {
//    override fun getBundles(): List<PluginBundle> {
//        return listOf(PluginBundle("Redscript", Path.of("textmate/bundles/redscript/redscript.tmLanguage.json")))
//    }
//}

//class RedscriptTextMateBundleProvider : TextMateBundleProvider {
//    override fun getBundles(): List<PluginBundle> {
//        val bundleResource = javaClass.classLoader.getResource("textmate/bundles/redscript")
//        val bundleFile = File(bundleResource?.toURI() ?: throw IllegalStateException("Failed to locate TextMate bundle directory"))
//
//        return listOf(PluginBundle("redscript", bundleFile.toPath()))
//    }
//}

//class RedscriptTextMateBundleProvider : TextMateBundleProvider {
//    private val log = Logger.getInstance(javaClass)
//
//
//    override fun getBundles(): List<TextMateBundleProvider.PluginBundle> {
//        print("GETTING TEXTMATEDIR")
//        val bundleDir = PluginPathManager.getPluginResource(
//            javaClass,
//            "textmate"
//        )
//        print("bundleDir: $bundleDir \n")
//
//        log.info("Loading TextMate bundle from: ${bundleDir?.absolutePath ?: "NULL"}")
//
//        return if (bundleDir != null && bundleDir.exists()) {
//            listOf(TextMateBundleProvider.PluginBundle("Redscript", bundleDir.toPath()))
//        } else {
////            log.error("Failed to locate TextMate bundle directory")
//            emptyList()
//        }
//    }
//}



// Bruno - no logs?
class RedscriptTextMateBundleProvider : TextMateBundleProvider {
    override fun getBundles(): List<PluginBundle> {
        try {
            val redscriptBundleTmpDir: Path =
                Files.createTempDirectory(Path.of(PathManager.getTempPath()), "textmate")
            for (fileToCopy in listOf(
                "package.json",
                "language-configuration.json",
                "syntaxes/redscript.tmLanguage.json"
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

// Speculative vscode importer
//class RedscriptTextMateBundleProvider : TextMateBundleProvider {
//    override fun getBundles(): List<PluginBundle> {
//        val res = getPluginResource(javaClass, "textmate/bundles/vscode/redscript-syntax-highlighting-0.0.3.vsix")
//            ?: throw IllegalStateException("Failed to locate TextMate bundle directory")
//
//        // Define a resource loader for the VS Code extension
//        val resourceLoader: (String) -> InputStream? = { resourcePath ->
//            javaClass.classLoader.getResourceAsStream("textmate/bundles/redscript/$resourcePath")
//        }
//
//        // Use the VSCBundleReader to read the bundle
//        val bundleReader = readVSCBundle(resourceLoader)
//            ?: throw IllegalStateException("Failed to read Redscript TextMate bundle")
//
//        // Return the bundle as a PluginBundle
//        return bundleReader.readGrammars()
////        return listOf(PluginBundle("Redscript", bundleReader))
//    }
//}
