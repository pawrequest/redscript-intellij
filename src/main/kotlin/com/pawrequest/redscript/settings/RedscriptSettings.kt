package com.pawrequest.redscript.settings


import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.pawrequest.redscript.util.redLog
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

@State(name = "RedscriptSettings", storages = [Storage("redscript.xml")])
class RedscriptSettings : PersistentStateComponent<RedscriptSettings.State?> {
    private var myState = State()

    class State {
        var gameDir: String = System.getenv("REDCLI_GAME") ?: ""
        var redscriptIDEPath: String = ""
        var redscriptIDEVersion: String = ""
    }

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    var gameDir: String
        get() = myState.gameDir
        set(gameDir) {
            myState.gameDir = gameDir
        }

    var redscriptIDEPath: String
        get() = myState.redscriptIDEPath
        set(redscriptIDEPath) {
            myState.redscriptIDEPath = redscriptIDEPath
        }

    var redscriptIDEVersion: String
        get() = myState.redscriptIDEVersion
        set(redscriptIDEVersion) {
            myState.redscriptIDEVersion = redscriptIDEVersion
        }


    companion object {
        fun getInstance(): RedscriptSettings {
            return ApplicationManager.getApplication().getService(RedscriptSettings::class.java)
        }
    }
}

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
    redLog("getSettingsVersion: '$ret'")
    return ret
}

fun setRedIDEVersionSettings(version: String) {
    RedscriptSettings.getInstance().redscriptIDEVersion = version
}

fun getRedIDEVersionLastInstalled(): String? {
    val ret = PropertiesComponent.getInstance().getValue("redscript.ide.version")
    redLog("Installed version = $ret")
    return ret
}

fun setRedIDEVersionLastInstalled(version: String) {
    redLog("Set Last Installed IDEVersion: $version")
    PropertiesComponent.getInstance().setValue("redscript.ide.version", version)
}


// BINARY
fun getRedIDEBinaryPathSettings(): Path {
    val ret = Paths.get(RedscriptSettings.getInstance().redscriptIDEPath)
    redLog("Get BinaryPath from Settings: $ret")
    return ret
}

fun setRedIDEBinaryPathSettings(path: String) {
    redLog("Set BinaryPath in Settings: $path")
    RedscriptSettings.getInstance().redscriptIDEPath = path
}

fun getBinaryPathDefault(version: String? = null): Path {
    val binaryPath = getCacheDir().resolve(getBinaryName(version))
    redLog("CacheDir Binary Path = ${binaryPath.toAbsolutePath()}")
    return binaryPath
}

fun getBinaryPathSettingsOrDefault(version: String? = null): Path {
    val settingsPath = getRedIDEBinaryPathSettings()
    val cachePath = getBinaryPathDefault(version)
    if (settingsPath.toFile().exists()) {
        redLog("Binary path from settings: ${settingsPath.toAbsolutePath()}")
        return settingsPath
    }
    redLog("Binary path from cache: ${cachePath.toAbsolutePath()}")
    return cachePath
}
fun chooseBinaryPath(version: String? = null): Path {
    val settingsPath = getRedIDEBinaryPathSettings()
    val cachePath = getBinaryPathDefault(version)
    if (settingsPath.toFile().exists() && settingsPath != cachePath) {
        redLog("Binary path from settings non standard: ${settingsPath.toAbsolutePath()}")
        return settingsPath
    }
    redLog("Standard Binary path from cache-dir: ${cachePath.toAbsolutePath()}")
    return cachePath
}