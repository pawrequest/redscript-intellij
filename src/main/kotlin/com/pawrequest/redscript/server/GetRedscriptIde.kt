package com.pawrequest.redscript.server

import com.intellij.ide.util.PropertiesComponent
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.net.ssl.HttpsURLConnection

data class RedscriptIdeRelease(val tagName: String, val downloadUrl: String)

fun getRedscriptIdeVersion(): String =
    object {}.javaClass.classLoader.getResourceAsStream("redscript-ide-version.txt")
        ?.bufferedReader()?.readText()?.trim()
        ?: error("redscript-ide-version.txt not found")



fun getPlatformBinaryName(): String {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        osName.contains("win") -> "redscript-ide.exe"
        osName.contains("mac") -> "redscript-ide-x86_64-apple-darwin"
        osName.contains("linux") -> "redscript-ide-x86_64-unknown-linux-gnu"
        else -> throw UnsupportedOperationException("Unsupported platform: $osName")
    }
}

fun getRedscriptIDEBinaryPath(): Path {
    val stored = PropertiesComponent.getInstance().getValue("redscript.ide.binary.path")
    return if (!stored.isNullOrBlank()) {
        Paths.get(stored)
    } else {
        getCacheDir().resolve(getPlatformBinaryName())
    }
}
fun setRedscriptIDEBinaryPath(path: String) =
    PropertiesComponent.getInstance().setValue("redscript.ide.binary.path", path)


fun getRedscriptIDEVersion(): String =
    PropertiesComponent.getInstance().getValue("redscript.ide.version") ?: "unknown"


fun setRedscriptIDEVersion(version: String) =
    PropertiesComponent.getInstance().setValue("redscript.ide.version", version)


fun getCacheDir(): Path =
    Paths.get(System.getProperty("user.home"), ".redscript-ide")


fun getLatestReleaseInfo(): RedscriptIdeRelease {
//    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/latest").toURL()
    val version = getRedscriptIdeVersion()
    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/tags/$version").toURL()
    val conn = apiUrl.openConnection() as HttpsURLConnection
    conn.setRequestProperty("User-Agent", "redscript-ide-intellij")
    conn.connectTimeout = 5000
    conn.readTimeout = 5000

    conn.inputStream.bufferedReader().use { reader ->
        val json = JSONObject(reader.readText())
        val tagName = json.getString("tag_name")
        val assets = json.getJSONArray("assets")
        val binaryName = getPlatformBinaryName()
        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            if (asset.getString("name") == binaryName) {
                val downloadUrl = asset.getString("browser_download_url")
                return RedscriptIdeRelease(tagName, downloadUrl)
            }
        }
        error("No asset named $binaryName found in latest release")
    }
}

fun downloadRedscriptIdeFromGithub(): Pair<File, String> {
    var version = getRedscriptIDEVersion()
    val bPath = getRedscriptIDEBinaryPath()
    val binaryFile = bPath.toFile()

    try {
        val release = getLatestReleaseInfo()
        if (version == release.tagName && binaryFile.exists()) {
            println("Cached Redscript IDE binary version'$version' is up-to-date: ${binaryFile.absolutePath}")
            return binaryFile to version
        }

        if (version != release.tagName || !binaryFile.exists()) {
            println("Cached Redscript IDE binary version '$version' is outdated or does not exist, downloading latest version...")
            println("Downloading Redscript IDE '${getPlatformBinaryName()}' version ${release.tagName} from: ${release.downloadUrl} to ${binaryFile.absolutePath}")
            Files.createDirectories(getCacheDir())
            URI(release.downloadUrl).toURL().openStream().use { input ->
                FileOutputStream(binaryFile).use { output ->
                    input.copyTo(output)
                }
            }
            binaryFile.setExecutable(true)
            setRedscriptIDEVersion(release.tagName)
            setRedscriptIDEBinaryPath(binaryFile.absolutePath)
            version = release.tagName
            println("Downloaded and set Redscript IDE binary to version '$version': ${binaryFile.absolutePath}")
        }
        return binaryFile to version


    } catch (e: Exception) {
        e.printStackTrace()

        if (binaryFile.exists()) {
            println("Failed to fetch latest release info, using cached binary: ${binaryFile.absolutePath}")
            return binaryFile to version
        } else {
            throw RuntimeException("Failed to fetch latest release info and no cached binary found", e)
        }
    }

}
