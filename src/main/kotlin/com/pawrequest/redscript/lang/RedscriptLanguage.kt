package com.pawrequest.redscript.lang

import com.intellij.lang.Language


object RedscriptLanguage : Language("Redscript") {
    val INSTANCE: RedscriptLanguage = RedscriptLanguage
    private fun readResolve(): Any = RedscriptLanguage

}


//@Suppress("unused", "UnstableApiUsage")
//object RedscriptLanguage : Language("redscript") {
//    private fun readResolve(): Any = RedscriptLanguage
//    override fun getDisplayName(): @NlsSafe String = "Redscript"
//}
