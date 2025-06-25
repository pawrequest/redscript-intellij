package com.pawrequest.redscript.settings

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import javax.swing.*

class RedscriptSettingsComponent {
    val panel: JPanel = JPanel()
    private val gameDirField: JTextField
    private val browseButton: JButton
    private val redscriptIDEVersionBox: JComboBox<IDEVersion>

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

        val ideVersionLabel = JLabel("IDE Version:")
        redscriptIDEVersionBox = JComboBox(IDEVersion.entries.toTypedArray())


        // Add components to the panel
        panel.add(label)
        panel.add(gameDirField)
        panel.add(browseButton)
        panel.add(ideVersionLabel)
        panel.add(redscriptIDEVersionBox)
    }

    var gameDir: String?
        get() = gameDirField.text
        set(gameDir) {
            gameDirField.text = gameDir
        }

    var redscriptIDEVersion: IDEVersion
        get() = redscriptIDEVersionBox.selectedItem as IDEVersion
        set(version) {
            redscriptIDEVersionBox.selectedItem = version
        }
}

