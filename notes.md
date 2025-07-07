# DAP

To integrate the redscript-dap (Debug Adapter Protocol server for redscript) into your IntelliJ plugin, follow these
steps:

1. Download or Bundle the DAP Binary

Use a similar approach as with redscript-ide: download the DAP binary from GitHub releases or bundle it with your
plugin.
You can automate the download in your Gradle build or at runtime, using code similar to your GetRedscriptIde.kt.

2. Start the DAP Server Process

Launch the DAP server as an external process from your plugin, passing the required arguments (see the DAP repo README
for CLI usage).
Use ProcessBuilder or similar in Kotlin/Java.

3. Implement a Debugger Integration

Use the IntelliJ Platform Debugger API to create a custom debug process.
Implement a DebugProcess and DebugProcessStarter that communicates with the DAP server using the Debug Adapter
Protocol (JSON-RPC over stdio or TCP).

4. Use an Existing DAP Client Library (Optional)

Consider using a Java/Kotlin DAP client library, or implement the protocol yourself (see vscode-java-debug for
reference).

5. Register the Debugger in plugin.xml

Add the debugger extension points in your plugin.xml to make it available for redscript files.
Summary of Steps in Code/Config:

Add code to download or locate the redscript-dap binary.
Start the DAP server process when debugging is requested.
Implement the IntelliJ debug process to communicate with the DAP server.
Register the debugger in plugin.xml.
References:

IntelliJ Debugger API Docs
redscript-dap Usage
This is a non-trivial integration, but you can reuse much of your existing infrastructure for downloading and managing
external binaries. The main work is in implementing the Debugger integration layer.

# re no 0.1.46

## GPT 4.1 says

The key difference is in the URI format returned by the textDocument/definition response:

v0.1.46 (working):
The URI is properly percent-encoded and uses the file:/// scheme:
file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/PawPunk.Funcs.reds

v0.2.7 (broken):
The URI is missing a slash and uses backslashes with percent-encoding:
file:C:/prdev/mod/PawPunk%5Cr6/scripts/PawPunk%5CPawPunk.Funcs.reds

Why this breaks GoToDefinition:
IntelliJ and most LSP clients expect URIs to follow the file:/// scheme and use forward slashes. The v0.2.7 response
uses an invalid URI format, so the client cannot resolve the file location, breaking GoToDefinition.

Summary:
v0.2.7 returns an incorrectly formatted URI in the definition response. This prevents the client from opening the target
file. v0.1.46 returns a correct URI, so GoToDefinition works.

# logs from v0.2.7 clickToDefinition broken at cursor position 5:19

[Trace - 23:34:45] Sending notification 'initialized'
Params: {}

[Trace - 23:34:50] Sending notification 'textDocument/didOpen'
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds",
"languageId": "Redscript",
"version": 1,
"text": "module PawPunk.Test\nimport PawPunk.Funcs.*\n\nfunc Test(){\n let serial \u003d aFunc();\n}\n\n"
}
}

[Trace - 00:12:02] Sending request 'textDocument/definition - (3)'.
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds"
},
"position": {
"line": 4,
"character": 18
}
}

[Trace - 00:12:02] Received response 'textDocument/definition - (3)' in 0ms.
Result: [
{
"uri": "file:C:/prdev/mod/PawPunk%5C.%5Cr6%5Cscripts%5CPawPunk%5CPawPunk.Funcs.reds",
"range": {
"start": {
"line": 3,
"character": 12
},
"end": {
"line": 3,
"character": 17
}
}
}
]

[Trace - 00:12:02] Sending request 'textDocument/definition - (4)'.
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds"
},
"position": {
"line": 0,
"character": 1
}
}

[Trace - 00:12:02] Received response 'textDocument/definition - (4)' in 0ms.
Result: []

# logs from v0.1.46 working at cursor position 5:19

[Trace - 23:29:20] Sending notification 'initialized'
Params: {}

[Trace - 23:29:25] Received notification 'textDocument/publishDiagnostics'
Params: {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/PawPunk.Funcs.reds",
"diagnostics": []
}

[Trace - 23:29:25] Received notification 'textDocument/publishDiagnostics'
Params: {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/PawPunk.reds",
"diagnostics": []
}

[Trace - 23:29:25] Received notification 'textDocument/publishDiagnostics'
Params: {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds",
"diagnostics": []
}

[Trace - 23:29:25] Received notification 'textDocument/publishDiagnostics'
Params: {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds",
"diagnostics": [
{
"range": {
"start": {
"line": 4,
"character": 4
},
"end": {
"line": 4,
"character": 25
}
},
"severity": 2,
"source": "redscript",
"message": "this variable is never used"
}
]
}

[Trace - 23:29:25] Received notification 'window/logMessage'
Params: {
"type": 3,
"message": "redscript server initialized!"
}

[Trace - 23:29:25] Sending notification 'textDocument/didOpen'
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds",
"languageId": "Redscript",
"version": 1,
"text": "module PawPunk.Test\nimport PawPunk.Funcs.*\n\nfunc Test(){\n let serial \u003d aFunc();\n}\n\n"
}
}

[Trace - 23:29:26] Sending request 'textDocument/hover - (2)'.
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds"
},
"position": {
"line": 4,
"character": 18
}
}

[Trace - 23:29:26] Received response 'textDocument/hover - (2)' in 0ms.
Result: {
"contents": {
"language": "redscript",
"value": "PawPunk.Funcs.aFunc() -\u003e String"
},
"range": {
"start": {
"line": 4,
"character": 17
},
"end": {
"line": 4,
"character": 24
}
}
}

[Trace - 23:29:26] Sending request 'textDocument/definition - (3)'.
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds"
},
"position": {
"line": 4,
"character": 17
}
}

[Trace - 23:29:26] Received response 'textDocument/definition - (3)' in 0ms.
Result: [
{
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/PawPunk.Funcs.reds",
"range": {
"start": {
"line": 4,
"character": 0
},
"end": {
"line": 4,
"character": 0
}
}
}
]



