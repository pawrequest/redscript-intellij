package com.pawrequest.redscript.debug

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.ComboBox
import java.awt.GridLayout
import java.io.File
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class RedscriptAttachSettingsEditor : SettingsEditor<RedscriptAttachRunConfiguration>() {
    private val gameDirField = JTextField()
    private val portComboBox = ComboBox<Int>()

    override fun resetEditorFrom(s: RedscriptAttachRunConfiguration) {
        gameDirField.text = s.gameDir ?: ""
        portComboBox.removeAllItems()
        s.gameDir?.let { dir ->
            val ports = findDebugPorts(dir)
            ports.forEach { portComboBox.addItem(it) }
        }
        portComboBox.selectedItem = s.port
    }

    override fun applyEditorTo(s: RedscriptAttachRunConfiguration) {
        s.gameDir = gameDirField.text
        s.port = portComboBox.selectedItem as? Int
    }

    override fun createEditor(): JComponent {
        val panel = JPanel(GridLayout(2, 2))
        panel.add(JLabel("Game Directory:"))
        panel.add(gameDirField)
        panel.add(JLabel("Port:"))
        panel.add(portComboBox)
        return panel
    }

    private fun findDebugPorts(gameDir: String): List<Int> {
        val binDir = File("$gameDir/bin/x64")
        return binDir.listFiles()?.mapNotNull { it.name.matchPort() } ?: emptyList()
    }

    private fun String.matchPort(): Int? {
        val regex = Regex("""dap\.(\d+)\.debug""")
        return regex.find(this)?.groupValues?.get(1)?.toIntOrNull()
    }
}