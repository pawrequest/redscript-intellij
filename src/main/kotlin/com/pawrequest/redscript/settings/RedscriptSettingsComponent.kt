package com.pawrequest.redscript.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton

import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JPanel

class RedscriptSettingsComponent {
    private val pathField = TextFieldWithBrowseButton()
    val panel = JPanel(FlowLayout(FlowLayout.LEFT))

    var gameDir: String?
        get() = pathField.text
        set(value) {
            pathField.text = value ?: ""
        }


    init {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle("Select Game Directory")
        pathField.addBrowseFolderListener(null, descriptor)
        pathField.preferredSize = java.awt.Dimension(300, pathField.preferredSize.height)
        panel.add(Box.createHorizontalStrut(8))
        panel.add(pathField)


    }
}

