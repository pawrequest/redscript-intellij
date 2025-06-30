package com.pawrequest.redscript.lang


import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.plugins.textmate.TextMateBackedFileType
import javax.swing.Icon

object RedscriptFileType : LanguageFileType(RedscriptLanguage.INSTANCE), TextMateBackedFileType {
    override fun getName(): String = "Redscript"

    override fun getDescription(): String = "Redscript file"

    override fun getDefaultExtension(): String = "reds"

    override fun getIcon(): Icon = RedscriptIcons.REDSCRIPT_FILE
}




