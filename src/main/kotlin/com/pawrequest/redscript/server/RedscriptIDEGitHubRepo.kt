package com.pawrequest.redscript.server

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import com.pawrequest.github.GitHubUser
import com.pawrequest.github.GitHubRepo
import com.pawrequest.github.downloadOrCache


class RedscriptIDEGitHubRepo : GitHubRepo("redscript-ide", GitHubUser("jac3km4")) {

    companion object {
        fun getRedscriptIDE(
            version: String = "v0.1.46",
            cacheDir: Path = Paths.get(System.getProperty("user.home") + "/.redscript-ide")
        ): File {
            val repo = RedscriptIDEGitHubRepo()
            val downloadUri = repo.assetDownloadUri(version, platformBinaryName())
            return downloadOrCache(cacheDir, downloadUri.toURL())
        }

        private fun platformBinaryName(): String {
            print("getting os.name: ")
            val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

            return if (osName.contains("win")) {
                "redscript-ide.exe"
            } else if (osName.contains("mac")) {
                "redscript-ide-x86_64-apple-darwin"
            } else if (osName.contains("linux")) {
                "redscript-ide-x86_64-unknown-linux-gnu"
            } else {
                throw UnsupportedOperationException("Unsupported platform: $osName")
            }
        }
    }
}