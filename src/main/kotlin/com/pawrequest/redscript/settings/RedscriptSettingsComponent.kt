package com.pawrequest.redscript.settings

import com.intellij.util.ui.UIUtil
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class RedscriptSettingsComponent {
    private val _gameDir = TextFieldWithBrowseButton()
    private val _redscriptIdePath = TextFieldWithBrowseButton()
    private val _redscriptIdeVersion = JTextField()
    private var _isInitialized = false // Prevent redundant initialization
    val panel = JPanel(GridBagLayout())

    var gameDir: String?
        get() = _gameDir.text
        set(value) {
            _gameDir.text = value ?: ""
        }

    var redscriptIDEPath: String?
        get() = _redscriptIdePath.text
        set(value) {
            _redscriptIdePath.text = value ?: ""
        }

    var redscriptIDEVersion: String?
        get() = _redscriptIdeVersion.text
        set(value) {
            _redscriptIdeVersion.text = value ?: ""
        }

    init {
        if (!_isInitialized) {
            initializePanel()
            _isInitialized = true
        }
    }

    private fun initializePanel() {
        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            anchor = GridBagConstraints.NORTHWEST
            fill = GridBagConstraints.NONE
            insets = JBUI.insets(5)
        }

        // Game Directory
        val gameDirDescriptor =
            FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle("Select Game Directory")
        _gameDir.addBrowseFolderListener(null, gameDirDescriptor)
        _gameDir.preferredSize = Dimension(200, 25)
        _gameDir.minimumSize = Dimension(200, 25)
        _gameDir.maximumSize = Dimension(200, 25)
        val gameDirLabel = JLabel("Game Directory:")
        gameDirLabel.labelFor = _gameDir
        constraints.weightx = 0.0
        panel.add(gameDirLabel, constraints)
        constraints.gridx = 1
        constraints.weightx = 1.0
        panel.add(_gameDir, constraints)

        // IDE Path
        constraints.gridx = 0
        constraints.gridy = 1
        constraints.weightx = 0.0
        val idePathDescriptor = FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()
            .withTitle("Select Redscript IDE Path (Blank For Default With Latest Version)")
        _redscriptIdePath.addBrowseFolderListener(null, idePathDescriptor)
        _redscriptIdePath.preferredSize = Dimension(200, 25)
        _redscriptIdePath.minimumSize = Dimension(200, 25)
        _redscriptIdePath.maximumSize = Dimension(200, 25)
        val idePathLabel = JLabel("Redscript IDE Path:")
        idePathLabel.labelFor = _redscriptIdePath
        panel.add(idePathLabel, constraints)
        constraints.gridx = 1
        constraints.weightx = 1.0
        panel.add(_redscriptIdePath, constraints)

        // IDE Version
        constraints.gridx = 0
        constraints.gridy = 2
        constraints.weightx = 0.0
        val ideVersionLabel = JLabel("Redscript IDE Version:")
        ideVersionLabel.labelFor = _redscriptIdeVersion
        panel.add(ideVersionLabel, constraints)

        constraints.gridx = 1
        constraints.weightx = 1.0
        _redscriptIdeVersion.preferredSize = Dimension(200, 25)
        _redscriptIdeVersion.background = UIUtil.getTextFieldBackground()
        _redscriptIdeVersion.foreground = UIUtil.getTextFieldForeground()
        panel.add(_redscriptIdeVersion, constraints)

        // Empty panel to push content to the top
        constraints.gridy = GridBagConstraints.RELATIVE
        constraints.weighty = 1.0
        panel.add(JPanel(), constraints)
    }
}


//package com.pawrequest.redscript.settings
//
//import com.intellij.util.ui.UIUtil
//import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
//import com.intellij.openapi.ui.TextFieldWithBrowseButton
//import com.intellij.util.ui.JBUI
//import java.awt.Dimension
//import java.awt.GridBagConstraints
//import java.awt.GridBagLayout
//import java.awt.TextField
//import javax.swing.JLabel
//import javax.swing.JPanel
//
//class RedscriptSettingsComponent {
//    private val _gameDir = TextFieldWithBrowseButton()
//    private val _redscriptIdePath = TextFieldWithBrowseButton()
//    private val _redscriptIdeVersion = TextField()
//    val panel = JPanel(GridBagLayout())
//
//    var gameDir: String?
//        get() = _gameDir.text
//        set(value) {
//            _gameDir.text = value ?: ""
//        }
//
//    var redscriptIDEPath: String?
//        get() = _redscriptIdePath.text
//        set(value) {
//            _redscriptIdePath.text = value ?: ""
//        }
//
//    var redscriptIDEVersion: String?
//        get() = _redscriptIdeVersion.text
//        set(value) {
//            _redscriptIdeVersion.text = value ?: ""
//        }
//
//    init {
//        val constraints = GridBagConstraints().apply {
//            gridx = 0
//            gridy = 0
//            anchor = GridBagConstraints.NORTHWEST // Align components to the top-left
////            fill = GridBagConstraints.HORIZONTAL
//            fill = GridBagConstraints.NONE // Prevent horizontal stretching
//
//            insets = JBUI.insets(5)
//        }
////GAMEDIR
//        val gameDirDescriptor =
//            FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle("Select Game Directory")
//        _gameDir.addBrowseFolderListener(null, gameDirDescriptor)
//        _gameDir.preferredSize = Dimension(200, 25)
//        _gameDir.minimumSize = Dimension(200, 25)
//        _gameDir.maximumSize = Dimension(200, 25)
//        val gameDirLabel = JLabel("Game Directory:")
//        gameDirLabel.labelFor = _gameDir
//        constraints.weightx = 0.0
//        panel.add(gameDirLabel, constraints)
////        panel.add(gameDirLabel)
//        constraints.gridx = 1
//        constraints.weightx = 1.0
//        panel.add(_gameDir, constraints)
////        panel.add(_gameDir)#
//// IDEPATH
//        constraints.gridx = 0
//        constraints.gridy = 1
//        constraints.weightx = 0.0
//        val idePathDescriptor = FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()
//            .withTitle("Select Redscript IDE Path (Blank For Default With Latest Version)")
//        _redscriptIdePath.addBrowseFolderListener(null, idePathDescriptor)
//        _redscriptIdePath.preferredSize = Dimension(200, 25)
//        _redscriptIdePath.minimumSize = Dimension(200, 25)
//        _redscriptIdePath.maximumSize = Dimension(200, 25)
//        val idePathLabel = JLabel("Redscript IDE Path:")
//        idePathLabel.labelFor = _redscriptIdePath
//        panel.add(idePathLabel, constraints)
//        constraints.gridx = 1
//        constraints.weightx = 1.0
//        panel.add(_redscriptIdePath, constraints)
//
//// IDE VERSION
//        constraints.gridx = 0
//        constraints.gridy = 2
//        constraints.weightx = 0.0
//        val ideVersionLabel = JLabel("Redscript IDE Version:")
//        ideVersionLabel.labelFor = _redscriptIdeVersion
//        panel.add(ideVersionLabel, constraints)
//
//        constraints.gridx = 1
//        constraints.weightx = 1.0
//        _redscriptIdeVersion.preferredSize = Dimension(200, 25)
//        _redscriptIdeVersion.background = UIUtil.getTextFieldBackground()
//        _redscriptIdeVersion.foreground = UIUtil.getTextFieldForeground()
//        panel.add(_redscriptIdeVersion, constraints)
//
////        _redscriptIdeVersion.addTextListener("Redscript IDE Version: ")
////        val ideVersionLabel = JLabel("Redscript IDE Version:")
////        ideVersionLabel.labelFor = _redscriptIdeVersion
////        panel.add(ideVersionLabel, constraints)
//
//        // Ensure the panel aligns its components at the top
//        constraints.gridy = GridBagConstraints.RELATIVE
//        constraints.weighty = 1.0
//        panel.add(JPanel(), constraints) // Add an empty panel to push content to the top
//
//
//    }
//}
