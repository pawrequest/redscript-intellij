package com.pawrequest.redscript.util

import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.*
import java.util.logging.Formatter

private fun getSource(): String {
    val skipPackages = listOf(
        "java.lang.Thread",
        "java.util.logging.Logger",
        "com.pawrequest.redscript.util"
    )
    val stackTrace = Thread.currentThread().stackTrace
    val element = stackTrace.firstOrNull {
        skipPackages.none { skip -> it.className.startsWith(skip) }
    }
    return element?.let { "(${it.fileName}:${it.lineNumber})" } ?: ""
}

private class RedscriptLogFormatter : Formatter() {
    private val timeFormat = SimpleDateFormat("HH:mm:ss")
    override fun format(record: LogRecord): String {
        val time = timeFormat.format(Date(record.millis))
        val level = record.level.name
        val message = formatMessage(record)
        val source = record.parameters?.firstOrNull() as? String ?: ""
        return "$time $level $message $source\n"
    }
}

private val logger: Logger = Logger.getLogger("RedscriptLogger").apply {
    level = Level.ALL
    handlers.forEach { removeHandler(it) }
    val consoleHandler = ConsoleHandler().apply {
        level = Level.ALL
        formatter = RedscriptLogFormatter()
    }
    addHandler(consoleHandler)

    try {
        val fileStr = System.getProperty("redscript-intellij.log.file") ?: ".redscript-ide/redscript-intellij.log"
        val logDir = File(fileStr).parentFile
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val fileHandler = FileHandler(fileStr, true).apply {
            level = Level.ALL
            formatter = RedscriptLogFormatter()
        }
        addHandler(fileHandler)
    } catch (e: Exception) {
        println("ERROR: Failed to create log file: ${e.message}")
    }
    useParentHandlers = false
}

fun redLog(message: String, level: Level = Level.INFO) {
    logger.log(level, message, arrayOf(getSource()))
}