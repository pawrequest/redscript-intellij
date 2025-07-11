package com.pawrequest.redscript.server

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.pawrequest.redscript.util.*

import com.pawrequest.redscript.settings.*
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.util.logging.Level
import javax.net.ssl.HttpsURLConnection

data class RedscriptIdeRelease(val tagName: String, val downloadUrl: String)


fun githubConnection(url: String): HttpsURLConnection {
    val apiUrl = URI(url).toURL()
    val conn = apiUrl.openConnection() as HttpsURLConnection
    val token = System.getenv("RS4IJ-TOKEN")?.let { SecretStr(it) }
    if (token == null || token.get().isBlank()) {
        redLog("Warning: RS4IJ-TOKEN environment variable is not set or is empty. GitHub API rate limits may apply.")
    } else {
        conn.setRequestProperty("Authorization", "token ${token.get()}")
    }
    conn.setRequestProperty("User-Agent", "redscript-ide-intellij")
    conn.connectTimeout = 5000
    conn.readTimeout = 5000
    return conn
}

fun fetchReleaseInfo(version: String? = null): RedscriptIdeRelease {
    var apiUrlStr: String
    if (version.isNullOrBlank()) {
        apiUrlStr = "https://api.github.com/repos/jac3km4/redscript-ide/releases/latest"
        redLog("Fetching latest release info")
    } else {
        apiUrlStr = "https://api.github.com/repos/jac3km4/redscript-ide/releases/tags/$version"
        redLog("Fetching release info for version: $version")
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
                redLog("Found Github asset ${ret.tagName}")
                return ret

            }
        }
        error("No asset named $binary found in latest release")
    }
}


fun shouldDownload(version: String?): Boolean {
    val installedVersion = getRedIDEVersionLastInstalled() ?: ""
    val settingsBinary = getRedIDEBinaryPathSettings()

    if (!settingsBinary.toFile().exists()) {
        redLog("Redscript IDE binary path in settings does not exist - downloading.")
        return true
    }
    if (version.equals("user-custom")) {
        redLog("Passed version is set to user-custom and settings binary exists, not downloading.")
        return false
    }
    if (settingsBinary != getBinaryPathDefault()) {
        redLog("Redscript IDE binary path in settings is non-default, not downloading.")
        return false
    }

    if (installedVersion.isBlank() || installedVersion.equals("latest", ignoreCase = true)) {
        redLog("Installed version is blank and shouldn't be user custom version, will check Redscript IDE version")
        return true
    }
    if (version.isNullOrBlank()) {
        redLog("Passed version is null or blank, will check latest Redscript IDE")
        return true
    }
    if (version.isNotBlank() && !version.equals(installedVersion, ignoreCase = true)) {
        redLog("Passed version '$version' is different from installed version '$installedVersion', will check Redscript IDE version")
        return true
    }
    if (version.equals(installedVersion, ignoreCase = true)) {
        redLog("Installed version '$installedVersion' matches passed version '$version', no download needed")
        return false
    }
    redLog("No conditions met for skipping download, will check Redscript IDE version")
    return true

}

private val downloadLock = Any()

fun doDownload(release: RedscriptIdeRelease, binaryFile: File) {
    synchronized(downloadLock) {

        redLog("Downloading Redscript IDE: ${release.downloadUrl}")
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

        redLog("Downloaded Redscript IDE binary to ${binaryFile.absolutePath}")
    }
}


fun maybeDownloadRedscriptIdeProject(
    project: Project,
    getVersion: String? = null,
    fallbackToLatest: Boolean = true
): File {
    redLog("Maybe download Redscript Ide version '$getVersion'")
    val lastInstalled = getRedIDEVersionLastInstalled()
    var toGet: String? = getVersion
//    val cacheDirBinary = getBinaryPathCacheDir(toGet).toFile()
    val cacheOrSettingsBinary = getBinaryPathSettingsOrDefault(toGet).toFile()
//    val cacheDirBinary = getBinaryPathCacheDir(toGet).toFile()
    if (getVersion.isNullOrBlank()) {
        toGet = getRedIDEVersionSettings()
    }

    Files.createDirectories(getCacheDir())
    if (!shouldDownload(toGet) && cacheOrSettingsBinary.exists()) {
        redLog("Redscript IDE is already up-to-date, no download needed.")
        return cacheOrSettingsBinary
    }
//    We will check for new release
    stopRedscriptLanguageServer()
    Thread.sleep(200)
    val maxRetries = 5
    for (retryCount in 0 until maxRetries) {
        try {
            val release = fetchReleaseInfo(toGet)
            if (release.tagName == lastInstalled && cacheOrSettingsBinary.exists()) {
                val msg = "Cached Redscript IDE binary version '${release.tagName}' is up-to-date @ ${cacheOrSettingsBinary.absolutePath}"
                redLog(msg)
                notifyRedscriptProjectMaybe(project, msg)
                return cacheOrSettingsBinary
            }
            doDownload(release, cacheOrSettingsBinary)

            if (!toGet.isNullOrBlank()) {
                setRedIDEVersionSettings(release.tagName)
            }
            setRedIDEVersionLastInstalled(release.tagName)
            setRedIDEBinaryPathSettings(cacheOrSettingsBinary.absolutePath)
            cacheOrSettingsBinary.setExecutable(true)
            val msg =
                "Redscript IDE binary version '${release.tagName}' downloaded successfully to ${cacheOrSettingsBinary.absolutePath}"
            notifyRedscriptProjectMaybe(project, msg)
//            startRedscriptLanguageServer()
            return cacheOrSettingsBinary

        } catch (e: Exception) {
            e.printStackTrace()

            if (e is FileNotFoundException) {
                redLog("File not found during download: ${e.message}")
                if (fallbackToLatest) {
                    notifyRedscriptProjectMaybe(project, "File not found, falling back to latest version.${e.message}.", NotificationType.WARNING)
                    toGet = null
                    setRedIDEBinaryPathSettings(null.toString())
                }
            } else {
                redLog("Error during download: ${e.message}")
            }

            if (retryCount >= maxRetries - 1) {
                redLog("Max retries reached. Aborting download.")
                if (cacheOrSettingsBinary.exists()) {
                    redLog("Failed to fetch release, using cached binary: ${cacheOrSettingsBinary.absolutePath}")
                    return cacheOrSettingsBinary
                } else {
                    redLog("Failed Download and no cached binary available, throwing exception.", Level.SEVERE)
                    throw e
                }
            }
        }
    }
    throw IllegalStateException("Failed to download Redscript IDE binary after $maxRetries attempts.")
}


//
//fun maybeDownloadRedscriptIdeProject(project: Project, getVersion: String? = null) : File {
//    redLog("Maybe download Redscript Ide version '$getVersion'")
//
//    val binaryFile = getBinaryPathCacheDir(getVersion).toFile()
//    if (!shouldDownload(getVersion) && binaryFile.exists()) {
//        redLog("Redscript IDE is already up-to-date, no download needed.")
//        return binaryFile
//    }
//    val maxRetries = 5
//    for (retryCount in 0 until maxRetries) {
//        try {
//            val release = fetchReleaseInfo(getVersion)
//            if (release.tagName == getRedIDEVersionLastInstalled() && binaryFile.exists()) {
//                redLog("Cached Redscript IDE binary version '${release.tagName}' is up-to-date @ ${binaryFile.absolutePath}")
//                return binaryFile
//            }
//            Files.createDirectories(getCacheDir())
//            stopRedscriptLanguageServer()
//            Thread.sleep(1000)
//            doDownload(release, binaryFile)
//            if (!getVersion.isNullOrBlank()) {
//                setRedIDEVersionSettings(release.tagName)
//            }
//            setRedIDEVersionLastInstalled(release.tagName)
//            setRedIDEBinaryPathSettings(binaryFile.absolutePath)
//            binaryFile.setExecutable(true)
//            val msg = "Redscript IDE binary version '${release.tagName}' downloaded successfully to ${binaryFile.absolutePath}"
////            notifyRedscript(project, msg)
//            notifyRedscriptApp(msg)
//            return binaryFile
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (retryCount >= maxRetries - 1) {
//                redLog("Max retries reached. Aborting download.")
//                if (binaryFile.exists()) {
//                    redLog("Failed to fetch release, using cached binary: ${binaryFile.absolutePath}")
//                    return binaryFile
//                } else {
//                    throw e
//                }
//            }
//        }
//    }
//    throw IllegalStateException("Failed to download Redscript IDE binary after $maxRetries attempts.")
//}


//fun shouldDownload(version: String?): Boolean {
//    val installedVersion = getRedIDEVersionLastInstalled() ?: ""
//    val settingsBinary = getRedIDEBinaryPathSettings()
//
//    if (version.equals("user-custom")) {
//        redLog("Redscript IDE version is set to user-custom, skipping download.")
//        return false
//    }
//    if (settingsBinary.toFile().exists() && (settingsBinary != getBinaryPathCacheDir())) {
//        redLog("Redscript IDE binary path in settings is non-default, not downloading.")
//        return false
//    }
//
//    if (installedVersion.isBlank() || installedVersion.equals("latest", ignoreCase = true)) {
//        redLog("Installed version is blank, will check Redscript IDE version")
//        return true
//    }
//    if (version.isNullOrBlank()) {
//        redLog("Passed version is null or blank, will check latest Redscript IDE")
//        return true
//    }
//    if (version.isNotBlank() && !version.equals(installedVersion, ignoreCase = true)) {
//        redLog("Passed version '$version' is different from installed version '$installedVersion', will check Redscript IDE version")
//        return true
//    }
//    redLog("Installed version '$installedVersion' matches passed version '$version', no download needed")
//    return false
//}
