package com.pawrequest.redscript.util

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

private val logger: Logger = Logger.getLogger("RedscriptLogger").apply {
    level = Level.ALL
    handlers.forEach { removeHandler(it) }
    val consoleHandler = ConsoleHandler().apply {
        level = Level.ALL
        formatter = SimpleFormatter()
    }
    addHandler(consoleHandler)
    useParentHandlers = false
}

private fun getSource(): String {
    val stackTrace = Thread.currentThread().stackTrace[3]
    return "${stackTrace.methodName} | (${stackTrace.fileName}:${stackTrace.lineNumber})"
}

fun logInfo(message: String) {
    println("$message | ${getSource()}")
}

fun logWarning(message: String) {
    logger.warning("$message ${getSource()}")
}

fun logError(message: String) {
    logger.severe("$message ${getSource()}")
}