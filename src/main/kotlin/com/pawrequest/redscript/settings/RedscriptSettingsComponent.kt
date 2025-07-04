package com.pawrequest.redscript.settings

import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class RedscriptSettingsComponent(private val project: Project) {

    private val _gameDir = TextFieldWithBrowseButton()
    private val _redscriptIdePath = TextFieldWithBrowseButton()
    private val _redscriptIdeVersionToGet = JTextField()
    private var _isInitialized = false // Prevent redundant initialization
    private val _lastInstalledVersion = JTextField().apply {
        isEditable = false
        preferredSize = Dimension(200, 25)
    }
    private val _updateContentRootsButton = JButton("Add Content Roots from .redscript to Project Workspace")
    val panel = JPanel(GridBagLayout())
    val constraints: GridBagConstraints = GridBagConstraints().apply {
        gridx = 0
        gridy = 0
        anchor = GridBagConstraints.NORTHWEST
        fill = GridBagConstraints.NONE
        insets = JBUI.insets(5)
    }

    init {
        if (!_isInitialized) {
            initializePanel()
            _isInitialized = true
        }
    }


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

    var redscriptIDEVersionToGet: String?
        get() = _redscriptIdeVersionToGet.text
        set(value) {
            _redscriptIdeVersionToGet.text = value ?: ""
        }


    private fun addLastInstalled(gridY: Int) {
        constraints.gridx = 0
        constraints.gridy = gridY
        constraints.weightx = 0.0
        val lastInstalledLabel = JLabel("Last Installed Redscript IDE Version:")
        lastInstalledLabel.labelFor = _lastInstalledVersion
        panel.add(lastInstalledLabel, constraints)

        constraints.gridx = 1
        constraints.weightx = 1.0
        _lastInstalledVersion.text = RedscriptSettings.getRedIDEVersionInstalled() ?: "Unknown"
        panel.add(_lastInstalledVersion, constraints)


    }

    private fun addContentRootsButton(gridY: Int) {
        constraints.gridx = 0
        constraints.gridy = gridY
        constraints.gridwidth = 2
        constraints.weightx = 1.0

        _updateContentRootsButton.addActionListener {
            contentRootsActivity(project)
        }

        panel.add(_updateContentRootsButton, constraints)
    }


    private fun addGameDir(gridY: Int) {
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
        constraints.gridy = gridY
        constraints.gridx = 1
        constraints.weightx = 1.0
        panel.add(_gameDir, constraints)
    }

    private fun addGetVersion(gridY: Int) {
        constraints.gridx = 0
        constraints.gridy = gridY
        constraints.weightx = 0.0
        val ideVersionLabel = JLabel("Get Redscript IDE Version:")
        ideVersionLabel.labelFor = _redscriptIdeVersionToGet
        panel.add(ideVersionLabel, constraints)
        constraints.gridx = 1
        constraints.weightx = 1.0
        _redscriptIdeVersionToGet.preferredSize = Dimension(200, 25)
        _redscriptIdeVersionToGet.background = UIUtil.getTextFieldBackground()
        _redscriptIdeVersionToGet.foreground = UIUtil.getTextFieldForeground()
        panel.add(_redscriptIdeVersionToGet, constraints)
    }

    private fun addRSIDEPath(gridY: Int) {
        constraints.gridx = 0
        constraints.gridy = gridY
        constraints.weightx = 0.0
        val idePathDescriptor = FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()
            .withTitle("Select Redscript IDE Path (Blank For Default With Latest Version)")
        _redscriptIdePath.addBrowseFolderListener(null, idePathDescriptor)
        _redscriptIdePath.preferredSize = Dimension(200, 25)
        _redscriptIdePath.minimumSize = Dimension(200, 25)
        _redscriptIdePath.maximumSize = Dimension(200, 25)
        val idePathLabel = JLabel("Select Redscript IDE Path (Blank for default):")
        idePathLabel.labelFor = _redscriptIdePath
        panel.add(idePathLabel, constraints)
        constraints.gridx = 1
        constraints.weightx = 1.0
        panel.add(_redscriptIdePath, constraints)

    }

    private fun addEmpty() {
        constraints.gridy = GridBagConstraints.RELATIVE
        constraints.weighty = 1.0
        panel.add(JPanel(), constraints)
    }

    private fun initializePanel() {
        addGameDir(0)
        addRSIDEPath(1)
        addGetVersion(2)
        addLastInstalled(3)
        addContentRootsButton(4)
        addEmpty()
    }
}
