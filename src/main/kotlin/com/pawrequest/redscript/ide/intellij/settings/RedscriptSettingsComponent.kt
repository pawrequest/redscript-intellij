package com.pawrequest.redscript.ide.intellij.settings

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import javax.swing.*

class RedscriptSettingsComponent {
    val panel: JPanel = JPanel()
    private val gameDirField: JTextField
    private val browseButton: JButton

    init {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        // Label and text field for game directory
        val label = JLabel("Game Directory:")
        gameDirField = JTextField(50)

        // Browse button
        browseButton = JButton("Browse...")
        browseButton.addActionListener {
            // Open a folder selection dialog
            val descriptor =
                FileChooserDescriptor(false, true, false, false, false, false)
            val selectedDir = FileChooser.chooseFile(descriptor, null, null)
            if (selectedDir != null) {
                // Update the text field with the selected directory path
                gameDirField.text = selectedDir.path
            }
        }

        // Add components to the panel
        panel.add(label)
        panel.add(gameDirField)
        panel.add(browseButton)
    }

    var gameDir: String?
        get() = gameDirField.text
        set(gameDir) {
            gameDirField.text = gameDir
        }
}

