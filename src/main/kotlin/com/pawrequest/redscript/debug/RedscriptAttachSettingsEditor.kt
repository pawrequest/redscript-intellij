package com.pawrequest.redscript.debug

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.ComboBox
import com.pawrequest.redscript.settings.RedscriptSettings
import java.awt.GridLayout
import java.io.File
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class RedscriptAttachSettingsEditor : SettingsEditor<RedscriptAttachRunConfiguration>() {
    private val portComboBox = ComboBox<Int>()

    override fun resetEditorFrom(s: RedscriptAttachRunConfiguration) {
        portComboBox.removeAllItems()
        findDebugPorts().forEach { portComboBox.addItem(it) }
        portComboBox.selectedItem = s.port
    }

    override fun applyEditorTo(s: RedscriptAttachRunConfiguration) {
        s.port = portComboBox.selectedItem as? Int
    }

    override fun createEditor(): JComponent {
        val panel = JPanel(GridLayout(2, 2))
        panel.add(JLabel("Port:"))
        panel.add(portComboBox)
        return panel
    }

    private fun findDebugPorts(): List<Int> {
        val gameDir = RedscriptSettings.getGameDir()
        val binDir = File("$gameDir/bin/x64")
        return binDir.listFiles()?.mapNotNull { it.name.matchPort() } ?: emptyList()
    }

    private fun String.matchPort(): Int? {
        val regex = Regex("""dap\.(\d+)\.debug""")
        return regex.find(this)?.groupValues?.get(1)?.toIntOrNull()
    }
}