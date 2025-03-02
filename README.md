# Redscript-IntelliJ

<!-- Plugin description -->
Work in progress   
IntelliJ plugin for Redscript Modding. entirely dependent on:
  - [redscript-ide](https://github.com/jac3km4/redscript-ide)
  - [redscript-syntax-highlighting](https://github.com/jackhumbert/redscript-syntax-highlighting)
  - [lsp4ij](https://github.com/redhat-developer/lsp4ij)
<!-- Plugin description end -->

# Requires
IntelliJ platform IDE  >= 2024.3.3 (eg IDEA, Pycharm)  
[LSP4IJ](https://github.com/redhat-developer/lsp4ij) intellij plugin from RedHat for LSP integration (auto installed with this plugin)

# ToDo

Stage1: mimic/port [vscode_extension](https://github.com/jac3km4/redscript-ide-vscode?tab=readme-ov-file)

- [x] skeleton project - plugin builds and installs on IDEA Community
- [x] Target multiple IDEs - working in Pycharm community, Pycharm Pro, IDEA Community
- [x] proxy Language server via [LSP4IJ](https://github.com/redhat-developer/lsp4ij) and [redscript-ide](https://github.com/jac3km4/redscript-ide)
- [x] GoToDefinition / click-thru
- [x] Game Install dir configurable in UI
- [x] Textmate Syntax Highlighting via [redscript syntax highlighting](https://github.com/jackhumbert/redscript-syntax-highlighting)
- [ ] Debugging via [redscript DAP](https://github.com/jac3km4/redscript-dap)
- [ ] Integrate  RedHotReload via [RedHotTools](https://github.com/psiberx/cp2077-red-hot-tools)


nb. i dont have a clue what i'm doing. use at your own risk.

# Installation
Update IDE to 2024.3.3 or later

- ## JetBrains Marketplace
  (tbc)

- ## GitHub
  - Get plugin zip - either:
    - clone and build with intellij platform buildPlugin for very latest,  OR:  
    - download prebuilt zip from [releases](https://github.com/pawrequest/redscript-intellij/releases)   
  - Install redscript.x.x.x.zip in IntelliJ IDE >= 2024.3.3 via settings -> plugins -> cog icon -> install plugin from disk  

# Usage
- Set CyberPunk Install directory in Settings -> Tools -> Redscript (wait a moment for lsp server to restart)
- Any definitions you want to reference must exist in the editor. i.e. if you want to GoToDefinition on a function, that function must be in the editor.  
  use `settings -> project -> project structure` to add workspace folders in pycharm. in IDEA use ctrl+shift+alt+s to open project settings, then `modules -> sources` to add folders.

# Troubleshooting
## LSP
- Verify Language server exists in `View -> Tool Windows -> Language Servers -> LSP Consoles -> RedscriptLanguageServer`
- Open a .reds file
- Verify RedscriptLanguageServer is in 'started' state
- Check lsp logs in tool window, should contain 'redscript server initialized!'
  ('redscript cache file not found at , the extension is configured incorrectly' means you didn't or incorrectly set the game install dir in settings)

if something is still wrong - please [create an issue](https://github.com/pawrequest/redscript-intellij/issues)

## Known Issues
- syntax highlighting is patchy on some files, notably Codeware.Global which is > 40k lines, maybe related to `wrap | replace | ...` runtime properties, see [issue #10](https://github.com/pawrequest/redscript-intellij/issues/10)
- lsp GoToDefinition is patchy, if you find a case where it doesn't work in intelliJ but does in vsCode please open an issue
