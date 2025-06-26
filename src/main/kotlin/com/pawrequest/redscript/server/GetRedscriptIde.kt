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

// get local info
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


fun getInstalledIDEVersion(): String = PropertiesComponent.getInstance().getValue("redscript.ide.version") ?: "unknown"


fun setinstalledIDEVersion(version: String) =
    PropertiesComponent.getInstance().setValue("redscript.ide.version", version)


fun getCacheDir(): Path = Paths.get(System.getProperty("user.home"), ".redscript-ide")

fun getLatestReleaseInfo(): RedscriptIdeRelease {
//    "https://api.github.com/repos/jac3km4/redscript-ide/releases/tags/$version"
    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases/latest").toURL()
    val conn = apiUrl.openConnection() as HttpsURLConnection
    setGithubHeaders(conn)
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
    val binaryFile = getRedscriptIDEBinaryPath().toFile()

    try {
        val release = getLatestReleaseInfo()
        if (installedVersion == release.tagName && binaryFile.exists()) {
            println("Cached Redscript IDE binary version'$installedVersion' is up-to-date: ${binaryFile.absolutePath}")
            return binaryFile to installedVersion
        }
        println("Cached Redscript IDE binary version '$installedVersion' is outdated or does not exist, downloading version $release.tag_name from ${release.downloadUrl} to ${binaryFile.absolutePath}...")
        Files.createDirectories(getCacheDir())
        withLanguageServerRestart { doDownload(release, binaryFile) }
        binaryFile.setExecutable(true)
        setinstalledIDEVersion(release.tagName)
        setRedscriptIDEBinaryPath(binaryFile.absolutePath)
        println("Downloaded and set Redscript IDE binary to version '$release.tag_name': ${binaryFile.absolutePath}")
        return binaryFile to release.tagName


    } catch (e: Exception) {
        e.printStackTrace()

        if (binaryFile.exists()) {
            println("Failed to fetch latest release, using cached binary: ${binaryFile.absolutePath}")
            return binaryFile to installedVersion
        } else {
            throw RuntimeException("Failed to fetch latest release info and no cached binary found", e)
        }
    }

}

fun doDownload(release: RedscriptIdeRelease, binaryFile: File) {
    println("Downloading Redscript IDE '${getPlatformBinaryName()}' from: ${release.downloadUrl} to ${binaryFile.absolutePath}")
    val url = URI(release.downloadUrl).toURL()
    val conn = url.openConnection() as HttpsURLConnection
    setGithubHeaders(conn)

    conn.inputStream.use { input ->
        FileOutputStream(binaryFile).use { output ->
            input.copyTo(output)
        }
    }
}

class SecretStr(private val value: String) {
    override fun toString(): String = "***"
    fun get(): String = value
}

fun setGithubHeaders(conn: HttpsURLConnection) {
    val token = System.getenv("RS4IJ-TOKEN")?.let { SecretStr(it) }
    if (token == null || token.get().isBlank()) {
        println("Warning: RS4IJ-TOKEN environment variable is not set or is empty. GitHub API rate limits may apply.")
    } else {
        println("Using GitHub token for authentication $token")
        conn.setRequestProperty("Authorization", "token ${token.get()}")
    }
    conn.setRequestProperty("User-Agent", "redscript-ide-intellij")
    conn.connectTimeout = 5000
    conn.readTimeout = 5000
}

//fun doDownload(release: RedscriptIdeRelease, binaryFile: File): Unit =
//    println("Downloading Redscript IDE '${getPlatformBinaryName()}' from: ${release.downloadUrl} to ${binaryFile.absolutePath}").also {
//        URI(release.downloadUrl).toURL().openStream().use { input ->
//            FileOutputStream(binaryFile).use { output ->
//                input.copyTo(output)
//            }
//        }
//    }
//


// get remote info
//fun getAllReleaseTags(): List<String> {
//    val apiUrl = URI("https://api.github.com/repos/jac3km4/redscript-ide/releases?per_page=100").toURL()
//    val conn = apiUrl.openConnection() as HttpsURLConnection
//    setGithubHeaders(conn)
//
//
//    conn.inputStream.bufferedReader().use { reader ->
//        val json = JSONArray(reader.readText())
//        println("Fetched ${json.length()} release tags from GitHub API")
//        println("First 5 tags: ${json.take(5).joinToString(", ") { (it as JSONObject).getString("tag_name") }}")
////        println("First 5 tags: ${json.take(5).joinToString(", ") { it.getString("tag_name") }}")
//        return List(json.length()) { i -> json.getJSONObject(i).getString("tag_name") }
//    }
//}
