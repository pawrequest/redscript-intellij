package com.pawrequest.redscript.lang


import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.textmate.TextMateBackedFileType
import org.jetbrains.plugins.textmate.TextMateFileType
import javax.swing.Icon


//object RedscriptFileType : FileType, TextMateBackedFileType {
//    override fun getName() = "redscript"
//    override fun getDescription() = "Redscript file"
//    override fun getDefaultExtension() = "reds"
//    override fun getIcon(): Icon = RedscriptIcons.REDSCRIPT_FILE
//    override fun isBinary() = false
//
//    fun isMyFile(file: PsiFile) = isMyFile(file.virtualFile)
//
//    fun isMyFile(file: VirtualFile) =
//        file.fileType == TextMateFileType.INSTANCE && file.extension == defaultExtension
//}

//object RedscriptFileType : LanguageFileType(RedscriptLanguage.INSTANCE) {
//    val INSTANCE = RedscriptFileType
//
//    override fun getName(): String = "redscript"
//
//    override fun getDescription(): String = "Redscript file"
//
//    override fun getDefaultExtension(): String = "reds"
//
//    override fun getIcon(): Icon = RedscriptIcons.REDSCRIPT_FILE
//
////    companion object {
////        val INSTANCE = RedscriptFileType()
////    }
//}



object RedscriptFileType : FileType, TextMateBackedFileType {
    val INSTANCE = RedscriptFileType
    override fun getName(): String = "Redscript"

    override fun getDescription(): String = "Redscript file"

    override fun getDefaultExtension(): String = "reds"

    override fun getIcon(): Icon = RedscriptIcons.REDSCRIPT_FILE
    override fun isBinary(): Boolean = false
}


