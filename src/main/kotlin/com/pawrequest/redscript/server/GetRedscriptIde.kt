package com.pawrequest.redscript.server

import com.pawrequest.redscript.util.*

import com.intellij.openapi.project.Project
import com.intellij.ide.util.PropertiesComponent
import com.pawrequest.redscript.settings.*
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

fun getCacheDir(): Path {
    val ret = Paths.get(System.getProperty("user.home"), ".redscript-ide")
    return ret
}

fun getDefaultBinaryName(): String {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    val ret = when {
        osName.contains("win") -> "redscript-ide.exe"
        osName.contains("mac") -> "redscript-ide-x86_64-apple-darwin"
        osName.contains("linux") -> "redscript-ide-x86_64-unknown-linux-gnu"
        else -> throw UnsupportedOperationException("Unsupported platform: $osName")
    }
    return ret
}

fun getBinaryName(version: String? = null): String {
    val baseName = getDefaultBinaryName()
    if (version.isNullOrEmpty() || version.equals("latest", ignoreCase = true)) {
        return baseName
    }
    return if (baseName.endsWith(".exe")) {
        baseName.removeSuffix(".exe") + "-$version.exe"
    } else {
        "$baseName-$version"
    }
}


//IDE VERSION
fun getRedIDEVersionSettings(): String {
    val ret = RedscriptSettings.getInstance().redscriptIDEVersion
    logInfo("getSettingsVersion: '$ret'")
    return ret
}

fun setRedIDEVersionSettings(version: String) {
    RedscriptSettings.getInstance().redscriptIDEVersion = version
}

fun getRedIDEVersionLastInstalled(): String? {
    val ret = PropertiesComponent.getInstance().getValue("redscript.ide.version")
    logInfo("Installed version = $ret")
    return ret
}

fun setRedIDEVersionLastInstalled(version: String) {
    logInfo("Set Last Installed IDEVersion: $version")
    PropertiesComponent.getInstance().setValue("redscript.ide.version", version)
}


// BINARY
fun getRedIDEBinaryPathSettings(): Path {
    val ret = Paths.get(RedscriptSettings.getInstance().redscriptIDEPath)
    logInfo("Get BinaryPath from Settings: $ret")
    return ret
}

fun setRedIDEBinaryPathSettings(path: String) {
    logInfo("Set BinaryPath in Settings: $path")
    RedscriptSettings.getInstance().redscriptIDEPath = path
}

fun getBinaryPathCacheDir(version: String? = null): Path {
    val binaryPath = getCacheDir().resolve(getBinaryName(version))
    logInfo("CacheDir Binary Path = ${binaryPath.toAbsolutePath()}")
    return binaryPath
}

//fun getInstalledBinaryPath(): Path{
//    val version = getRedIDEVersionInstalled()
//    return getBinaryPath(version)
//
//}
//fun getBinaryFile(version: String? = null): File {
//    val binaryPath = getBinaryPath(version)
//    logInfo("getBinaryFile() called, returning: ${binaryPath.toAbsolutePath()}")
//    return binaryPath.toFile()
//}


fun fetchReleaseInfo(version: String? = null): RedscriptIdeRelease {
    var apiUrlStr: String
    if (version.isNullOrBlank()) {
        apiUrlStr = "https://api.github.com/repos/jac3km4/redscript-ide/releases/latest"
        logInfo("Fetching latest release info")
    } else {
        apiUrlStr = "https://api.github.com/repos/jac3km4/redscript-ide/releases/tags/$version"
        logInfo("Fetching release info for version: $version")
    }
    val binary = getDefaultBinaryName()
    val conn = githubConnection(apiUrlStr)
    conn.inputStream.bufferedReader().use { reader ->
        val json = JSONObject(reader.readText())
        val tagName = json.getString("tag_name")
        val assets = json.getJSONArray("assets")
        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            if (asset.getString("name") == binary) {
                val downloadUrl = asset.getString("browser_download_url")
                val ret = RedscriptIdeRelease(tagName, downloadUrl)
                logInfo("Found Github asset ${ret.tagName}")
                return ret

            }
        }
        error("No asset named $binary found in latest release")
    }
}


fun shouldDownload(version: String?): Boolean {
    val installedVersion = getRedIDEVersionLastInstalled() ?: ""
    val settingsBinary = getRedIDEBinaryPathSettings()
    if (version.equals("user-custom")) {
        logInfo("Redscript IDE version is set to user-custom, skipping download.")
        return false
    }
    if (settingsBinary.toFile().exists() && (settingsBinary != getBinaryPathCacheDir())) {
        logInfo("Redscript IDE binary path in settings is non-default, not downloading.")
        return false
    }

    if (installedVersion.isBlank() || installedVersion.equals("latest", ignoreCase = true)) {
        logInfo("Installed version is blank, will check Redscript IDE version")
        return true
    }
    if (version.isNullOrBlank()) {
        logInfo("Passed version is null or blank, will check latest Redscript IDE")
        return true
    }
    if (version.isNotBlank() && !version.equals(installedVersion, ignoreCase = true)) {
        logInfo("Passed version '$version' is different from installed version '$installedVersion', will check Redscript IDE version")
        return true
    }
    logInfo("Installed version '$installedVersion' matches passed version '$version', no download needed")
    return false
}


fun maybeDownloadRedscriptIde(project: Project, getVersion: String? = null) {
    logInfo("Maybe download Redscript Ide version '$getVersion'")

    val binaryFile = getBinaryPathCacheDir(getVersion).toFile()
    if (!shouldDownload(getVersion) && binaryFile.exists()) {
        logInfo("Redscript IDE is already up-to-date, no download needed.")
        return
    }
    val maxRetries = 5
    for (retryCount in 0 until maxRetries) {
        try {
            val release = fetchReleaseInfo(getVersion)
            if (release.tagName == getRedIDEVersionLastInstalled() && binaryFile.exists()) {
                logInfo("Cached Redscript IDE binary version '${release.tagName}' is up-to-date @ ${binaryFile.absolutePath}")
                return
            }
            Files.createDirectories(getCacheDir())
            stopRedscriptLanguageServer()
            Thread.sleep(1000)
            doDownload(release, binaryFile)
            if (!getVersion.isNullOrBlank()) {
                setRedIDEVersionSettings(release.tagName)
            }
            setRedIDEVersionLastInstalled(release.tagName)
            setRedIDEBinaryPathSettings(binaryFile.absolutePath)
            binaryFile.setExecutable(true)
            notifyNewBinaryDownloaded(project, release.downloadUrl)
            return
        } catch (e: Exception) {
            e.printStackTrace()
            if (retryCount >= maxRetries - 1) {
                logInfo("Max retries reached. Aborting download.")
                if (binaryFile.exists()) {
                    logInfo("Failed to fetch release, using cached binary: ${binaryFile.absolutePath}")
                    return
                } else {
                    throw e
                }
            }
        }
    }
//    throw IllegalStateException("Unexpected state in download logic.")
}

private val downloadLock = Any()

fun doDownload(release: RedscriptIdeRelease, binaryFile: File) {
    synchronized(downloadLock) {

        logInfo("Downloading Redscript IDE: ${release.downloadUrl}")
        if (binaryFile.exists() && !binaryFile.canWrite()) {
            throw IllegalStateException("File ${binaryFile.absolutePath} is locked by another process.")
        }

        val url = release.downloadUrl
        val conn = githubConnection(url)

        conn.inputStream.use { input ->
            FileOutputStream(binaryFile).use { output ->
                input.copyTo(output)
            }
        }

        logInfo("Downloaded Redscript IDE binary to ${binaryFile.absolutePath}")
    }
}


fun githubConnection(url: String): HttpsURLConnection {
    val apiUrl = URI(url).toURL()
    val conn = apiUrl.openConnection() as HttpsURLConnection
    val token = System.getenv("RS4IJ-TOKEN")?.let { SecretStr(it) }
    if (token == null || token.get().isBlank()) {
        logInfo("Warning: RS4IJ-TOKEN environment variable is not set or is empty. GitHub API rate limits may apply.")
    } else {
        conn.setRequestProperty("Authorization", "token ${token.get()}")
    }
    conn.setRequestProperty("User-Agent", "redscript-ide-intellij")
    conn.connectTimeout = 5000
    conn.readTimeout = 5000
    return conn
}
