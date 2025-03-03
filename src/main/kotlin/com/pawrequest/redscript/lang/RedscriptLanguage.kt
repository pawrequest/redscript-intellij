package com.pawrequest.redscript.lang

import com.intellij.lang.Language


object RedscriptLanguage : Language("Redscript") {
    val INSTANCE: RedscriptLanguage = RedscriptLanguage
    private fun readResolve(): Any = RedscriptLanguage

}
