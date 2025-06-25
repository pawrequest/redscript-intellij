package com.pawrequest.redscript.server

import com.intellij.ide.util.PropertiesComponent
import com.pawrequest.redscript.settings.IDEVersion
import com.pawrequest.redscript.settings.RedscriptSettings
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.net.ssl.HttpsURLConnection

data class RedscriptIdeRelease(val tagName: String, val downloadUrl: String)

// get local info

fun setRedscriptIDEBinaryPath(path: String) =
    PropertiesComponent.getInstance().setValue("redscript.ide.binary.path", path)


fun getInstalledIDEVersion(): String = PropertiesComponent.getInstance().getValue("redscript.ide.version") ?: "unknown"


fun setInstalledIDEVersion(version: String) =
    PropertiesComponent.getInstance().setValue("redscript.ide.version", version)


fun getCacheDir(): Path = Paths.get(System.getProperty("user.home"), ".redscript-ide")

fun getReleaseUrl(version: String): URL =
    URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/tags/$version").toURL()

fun getRedIDEVersionFromSettings(): String {
    val v = RedscriptSettings.getInstance().redscriptIDEVersion
    val ret = getHighestMatchingRelease(v) ?: "unknown"
    println("Redscript IDE version from settings: $ret")
    return ret
}


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

// get remote info
fun getAllReleaseTags(): List<String> {
    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases?per_page=100").toURL()
    val conn = apiUrl.openConnection() as HttpsURLConnection
    conn.setRequestProperty("User-Agent", "redscript-ide-intellij")
    conn.connectTimeout = 5000
    conn.readTimeout = 5000

    conn.inputStream.bufferedReader().use { reader ->
        val json = JSONArray(reader.readText())
        println("Fetched ${json.length()} release tags from GitHub API")
        println("First 5 tags: ${json.take(5).joinToString(", ") { (it as JSONObject).getString("tag_name") }}")
//        println("First 5 tags: ${json.take(5).joinToString(", ") { it.getString("tag_name") }}")
        return List(json.length()) { i -> json.getJSONObject(i).getString("tag_name") }
    }
}

fun getHighestMatchingRelease(versionSetting: IDEVersion): String? {
    val prefix = when (versionSetting) {
        IDEVersion.V1 -> "v0.1"
        IDEVersion.V2 -> "v0.2"
    }

    fun parseVersion(tag: String): List<Int> = tag.removePrefix("v").split('.').map { it.toIntOrNull() ?: 0 }

    return getAllReleaseTags().filter { it.startsWith(prefix) }.maxWithOrNull { a, b ->
        val va = parseVersion(a)
        val vb = parseVersion(b)
        va.zip(vb).map { it.first.compareTo(it.second) }.firstOrNull { it != 0 } ?: (va.size - vb.size)
    }
}

fun getReleaseInfo(version: String): RedscriptIdeRelease {
//    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/latest").toURL()
    val apiUrl = getReleaseUrl(version = version)
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

// do it
fun downloadRedscriptIdeFromGithub(): Pair<File, String> {
    val installedVersion = getInstalledIDEVersion()
    val wantedVersion = getRedIDEVersionFromSettings()

    val bPath = getRedscriptIDEBinaryPath()
    val binaryFile = bPath.toFile()

    try {
        if (installedVersion == wantedVersion && binaryFile.exists()) {
            println("Cached Redscript IDE binary version'$installedVersion' is up-to-date: ${binaryFile.absolutePath}")
            return binaryFile to installedVersion
        }

        if (installedVersion != wantedVersion || !binaryFile.exists()) {
            val release = getReleaseInfo(wantedVersion)
            println("Cached Redscript IDE binary version '$installedVersion' is outdated or does not exist, downloading version $wantedVersion...")
            println("Downloading Redscript IDE '${getPlatformBinaryName()}' from: ${release.downloadUrl} to ${binaryFile.absolutePath}")
            Files.createDirectories(getCacheDir())
            URI(release.downloadUrl).toURL().openStream().use { input ->
                FileOutputStream(binaryFile).use { output ->
                    input.copyTo(output)
                }
            }
            binaryFile.setExecutable(true)
            setInstalledIDEVersion(wantedVersion)
            setRedscriptIDEBinaryPath(binaryFile.absolutePath)
            println("Downloaded and set Redscript IDE binary to version '$wantedVersion': ${binaryFile.absolutePath}")
        }
        return binaryFile to wantedVersion


    } catch (e: Exception) {
        e.printStackTrace()

        if (binaryFile.exists()) {
            println("Failed to fetch latest release info, using cached binary: ${binaryFile.absolutePath}")
            return binaryFile to installedVersion
        } else {
            throw RuntimeException("Failed to fetch latest release info and no cached binary found", e)
        }
    }

}

//fun getRedscriptIdeVersion(): String =
//    object {}.javaClass.classLoader.getResourceAsStream("redscript-ide-version.txt")
//        ?.bufferedReader()?.readText()?.trim()
//        ?: error("redscript-ide-version.txt not found")
//

//fun getHighestMatchingRelease(versionSetting: IDEVersion): String? {
//    val prefix = when (versionSetting) {
//        IDEVersion.V1 -> "v0.1"
//        IDEVersion.V2 -> "v0.2"
//    }
//    return getAllReleaseTags()
//        .filter { it.startsWith(prefix) }
//        .maxWithOrNull(compareBy { tag ->
//            tag.removePrefix("v").split('.').map { it.toIntOrNull() ?: 0 }
//        })
//}

//fun getValidTags(): Comparable {
//    return getAllReleaseTags()
//        .filter { it.startsWith(prefix) }
//        .maxWithOrNull(compareBy { tag ->
//            tag.removePrefix("v").split('.').map { it.toIntOrNull() ?: 0 }
//        })
//
//}

//
//fun getHighestMatchingRelease(versionSetting: IDEVersion): String? {
//    val prefix = when (versionSetting) {
//        IDEVersion.V1 -> "v0.1"
//        IDEVersion.V2 -> "v0.2"
//    }
//    return getAllReleaseTags()
//        .filter { it.startsWith(prefix) }
//        .maxWithOrNull(compareBy { tag ->
//            tag.removePrefix("v").split('.').map { it.toIntOrNull() ?: 0 }
//        })
//}


//fun getLatestReleaseInfo(): RedscriptIdeRelease {
////    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/latest").toURL()
//    val version = getRedIDEVersionFromSettings()
//    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/tags/$version").toURL()
//    val conn = apiUrl.openConnection() as HttpsURLConnection
//    conn.setRequestProperty("User-Agent", "redscript-ide-intellij")
//    conn.connectTimeout = 5000
//    conn.readTimeout = 5000
//
//    conn.inputStream.bufferedReader().use { reader ->
//        val json = JSONObject(reader.readText())
//        val tagName = json.getString("tag_name")
//        val assets = json.getJSONArray("assets")
//        val binaryName = getPlatformBinaryName()
//        for (i in 0 until assets.length()) {
//            val asset = assets.getJSONObject(i)
//            if (asset.getString("name") == binaryName) {
//                val downloadUrl = asset.getString("browser_download_url")
//                return RedscriptIdeRelease(tagName, downloadUrl)
//            }
//        }
//        error("No asset named $binaryName found in latest release")
//    }
//}
