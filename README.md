# Redscript-IDE-IntelliJ

<!-- Plugin description -->
Work in progress   
IntelliJ plugin for Redscript Modding. entirely dependent on [redscript-ide](https://github.com/jac3km4/redscript-ide)
<!-- Plugin description end -->

# Requires:
IntelliJ platform IDE  >= 2024.3.3 (eg IDEA, Pycharm)  
[LSP4IJ](https://github.com/redhat-developer/lsp4ij) intellij plugin from RedHat for LSP integration

# ToDo

Stage1: mimic/port [vscode_extension](https://github.com/jac3km4/redscript-ide-vscode?tab=readme-ov-file)

- [x] skeleton project - plugin builds and installs on IDEA Community
- [x] proxy Language server via [LSP4IJ](https://github.com/redhat-developer/lsp4ij) and [redscript-ide](https://github.com/jac3km4/redscript-ide)
- [x] GoToDefinition / click-thru
- [x] Game Install dir configurable in UI
- [ ] Textmate Syntax Highlighting via [redscript syntx highlighting](https://github.com/jackhumbert/redscript-syntax-highlighting)
- [ ] Debugging via [redscript DAP](https://github.com/jac3km4/redscript-dap)
- [ ] Integrate  RedHotReload via [RedHotTools](https://github.com/psiberx/cp2077-red-hot-tools)
- [x] Target multiple IDEs - working in Pycharm community, Pycharm Pro, IDEA Community


nb. i dont have a clue what i'm doing. use at your own risk.

Usage:
- Update IDE to 2024.3.3 or later
- Get plugin zip - either:
    - clone and build with intellij platform buildPlugin  
      OR:
    - download prebuilt zip from [releases](https://github.com/pawrequest/rs4ij/releases)

install zip in IntelliJ IDE >= 2024.3.3 via settings -> plugins -> cog icon -> install plugin from disk  

