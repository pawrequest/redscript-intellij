# Redscript-IntelliJ  

<!-- Plugin description -->
Work in progress   
IntelliJ plugin for Redscript Modding. entirely dependent on:
  - [redscript-ide](https://github.com/jac3km4/redscript-ide)
  - [redscript-syntax-highlighting](https://github.com/jackhumbert/redscript-syntax-highlighting)
  - [lsp4ij](https://github.com/redhat-developer/lsp4ij)
<!-- Plugin description end -->

# Requires
IntelliJ platform IDE  >= 2025.1.1 (e.g. IDEA, Pycharm)  
[LSP4IJ](https://github.com/redhat-developer/lsp4ij) intellij plugin from RedHat for LSP integration (auto installed with this plugin)

# ToDo

Stage1: mimic/port [vscode_extension](https://github.com/jac3km4/redscript-ide-vscode?tab=readme-ov-file)

- [x] skeleton project - plugin builds and installs on IDEA Community
- [x] Target multiple IDEs - working in Pycharm community, Pycharm Pro, IDEA Community
~~- [x] Publish to Jetbrains Marketplace~~ marketplace dependency resolution currently broken!
- [x] Language server via [LSP4IJ](https://github.com/redhat-developer/lsp4ij)
  and [redscript-ide](https://github.com/jac3km4/redscript-ide)
- [x] GoToDefinition / click-thru
- [x] Diagnostic underlining and pop-up code-lens
- [x] Code-Completion
- [x] Code-formatting
- [x] Game Install dir configurable in UI
- [x] Textmate Syntax Highlighting
  via [redscript syntax highlighting](https://github.com/jackhumbert/redscript-syntax-highlighting)
  (partial support atm, runtime hooks like `addmethod()` do not get highlighted properly.
  see [issue #10](https://github.com/pawrequest/redscript-intellij/issues/10))
- [ ] Debugging via [redscript DAP](https://github.com/jac3km4/redscript-dap)
- [ ] RedHotReload via [RedHotTools](https://github.com/psiberx/cp2077-red-hot-tools)


nb. i dont have a clue what i'm doing. use at your own risk.

# Installation
Update IDE to 2025.1.1 or later

`JetBrains Marketplace is experiencing a major bug affecting all plugins with dependencies. Use manual install until they
fix it.`  
~~- ## JetBrains Marketplace~~ **broken!**  
  ~~- Search 'redscript' in IDE plugins marketplace
  https://plugins.jetbrains.com/plugin/26709-redscript/versions/stable~~

- ## GitHub
    - Get plugin zip - either:
        - clone and build with intellij platform buildPlugin for very latest, OR:
        - download prebuilt zip from [releases](https://github.com/pawrequest/redscript-intellij/releases)
    - Install redscript.x.x.x.zip in IntelliJ IDE >= 2024.3.3 via settings -> plugins -> cog icon -> install plugin from
      disk

# Usage
- Set CyberPunk Install directory in Settings -> Tools -> Redscript (wait a moment for lsp server to restart)
- Any definitions you want to reference must exist in the editor. i.e. if you want to GoToDefinition on a function, that
  function must be in the editor.  
  use `settings -> project -> project structure` to add workspace folders in pycharm. in IDEA use ctrl+shift+alt+s to
  open project settings, then `modules -> sources` to add folders.
- Save file (ctrl+S) to check for errors.

# Troubleshooting
## LSP
- Verify Language server exists in `View -> Tool Windows -> Language Servers -> LSP Consoles -> RedscriptLanguageServer`
- Open a .reds file
- Verify RedscriptLanguageServer is in 'started' state
- Check lsp logs in tool window, should contain 'redscript server initialized!'
  ('redscript cache file not found at , the extension is configured incorrectly' means you didn't or incorrectly set the game install dir in settings)

if something is still wrong - please [create an issue](https://github.com/pawrequest/redscript-intellij/issues)

## Known Issues
- syntax highlighting is patchy on some files, maybe related to `wrap | replace | ...` runtime hooks, and `case keyword`
  statements. see [issue #10](https://github.com/pawrequest/redscript-intellij/issues/10)
- lsp GoToDefinition is patchy, specifically it will not find base-game definitions. if you find a case where it doesn't
  work in intelliJ but does in vsCode please open an issue

## FYI
- Redscript v1 [is in beta](https://github.com/jac3km4/redscript/releases), no updates will be made to this plugin against the old compiler.
- Most functionality will break if there are any errors on the page - save (ctrl+s) to highlight the errors and resolve them to resume using GoToDefinition, or ReFormat, etc. 