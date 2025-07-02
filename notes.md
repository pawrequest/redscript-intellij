# GPT 4.1 says

# logs from v0.2.7 clickToDefinition broken
[Trace - 23:34:45] Sending request 'initialize - (1)'.
Params: {
"processId": 18292,
"rootPath": "C:/prdev/mod/PawPunk",
"rootUri": "file:///C:/prdev/mod/PawPunk",
"initializationOptions": {
"game_dir": "D:\\GAMES\\Cyberpunk 2077"
},
"capabilities": {
"workspace": {
"applyEdit": true,
"workspaceEdit": {
"documentChanges": true,
"resourceOperations": [
"create",
"delete",
"rename"
]
},
"didChangeConfiguration": {
"dynamicRegistration": true
},
"didChangeWatchedFiles": {
"dynamicRegistration": true
},
"symbol": {},
"executeCommand": {
"dynamicRegistration": true
},
"workspaceFolders": true,
"configuration": true,
"semanticTokens": {
"refreshSupport": true
},
"codeLens": {
"refreshSupport": true
},
"fileOperations": {
"didRename": true,
"willRename": true,
"dynamicRegistration": true
},
"inlayHint": {
"refreshSupport": true
}
},
"textDocument": {
"synchronization": {
"willSave": true,
"willSaveWaitUntil": true,
"didSave": true
},
"completion": {
"completionItem": {
"snippetSupport": true,
"documentationFormat": [
"markdown",
"plaintext"
],
"deprecatedSupport": true,
"resolveSupport": {
"properties": [
"documentation",
"detail",
"additionalTextEdits"
]
},
"insertTextModeSupport": {
"valueSet": [
1,
2
]
},
"labelDetailsSupport": true
},
"completionList": {
"itemDefaults": [
"editRange"
]
},
"dynamicRegistration": true
},
"hover": {
"contentFormat": [
"markdown",
"plaintext"
],
"dynamicRegistration": true
},
"signatureHelp": {
"signatureInformation": {
"parameterInformation": {
"labelOffsetSupport": true
}
},
"dynamicRegistration": true
},
"references": {
"dynamicRegistration": true
},
"documentHighlight": {
"dynamicRegistration": true
},
"documentSymbol": {
"symbolKind": {
"valueSet": [
18,
17,
5,
14,
9,
10,
22,
24,
8,
1,
12,
11,
20,
6,
2,
3,
21,
16,
19,
25,
4,
7,
15,
23,
26,
13
]
},
"hierarchicalDocumentSymbolSupport": true,
"dynamicRegistration": true
},
"formatting": {
"dynamicRegistration": true
},
"rangeFormatting": {
"dynamicRegistration": true
},
"declaration": {
"linkSupport": true,
"dynamicRegistration": true
},
"definition": {
"linkSupport": true,
"dynamicRegistration": true
},
"typeDefinition": {
"linkSupport": true,
"dynamicRegistration": true
},
"implementation": {
"linkSupport": true,
"dynamicRegistration": true
},
"codeAction": {
"codeActionLiteralSupport": {
"codeActionKind": {
"valueSet": [
"quickfix",
"refactor",
"refactor.extract",
"refactor.inline",
"refactor.rewrite",
"source",
"source.organizeImports"
]
}
},
"dataSupport": true,
"resolveSupport": {
"properties": [
"edit"
]
},
"dynamicRegistration": true
},
"codeLens": {
"dynamicRegistration": true
},
"documentLink": {
"dynamicRegistration": true
},
"colorProvider": {
"dynamicRegistration": true
},
"rename": {
"prepareSupport": true,
"dynamicRegistration": true
},
"publishDiagnostics": {
"relatedInformation": true,
"tagSupport": {
"valueSet": [
1,
2
]
},
"codeDescriptionSupport": true,
"dataSupport": true
},
"foldingRange": {
"dynamicRegistration": true
},
"typeHierarchy": {
"dynamicRegistration": true
},
"callHierarchy": {
"dynamicRegistration": true
},
"semanticTokens": {
"requests": {
"range": false,
"full": true
},
"tokenTypes": [
"namespace",
"type",
"class",
"enum",
"interface",
"struct",
"typeParameter",
"parameter",
"variable",
"property",
"enumMember",
"event",
"function",
"method",
"macro",
"keyword",
"modifier",
"comment",
"string",
"number",
"regexp",
"operator",
"decorator",
"label"
],
"tokenModifiers": [
"declaration",
"definition",
"readonly",
"static"
],
"formats": [
"relative"
],
"multilineTokenSupport": true,
"serverCancelSupport": true,
"dynamicRegistration": true
},
"inlayHint": {
"dynamicRegistration": true
},
"diagnostic": {
"relatedDocumentSupport": true,
"dynamicRegistration": true
}
},
"window": {
"workDoneProgress": true,
"showMessage": {},
"showDocument": {
"support": true
}
},
"general": {
"staleRequestSupport": {
"cancel": true,
"retryOnContentModified": []
},
"positionEncodings": [
"utf-16"
]
}
},
"clientInfo": {
"name": "IntelliJ IDEA 2025.1",
"version": "IntelliJ IDEA (build IC-251.23774.435)"
},
"trace": "off",
"workspaceFolders": [
{
"uri": "file:///C:/prdev/mod/PawPunk",
"name": "PawPunk"
}
]
}


[Trace - 23:34:45] Received response 'initialize - (1)' in 0ms.
Result: {
"capabilities": {
"textDocumentSync": 2,
"hoverProvider": true,
"completionProvider": {
"triggerCharacters": [
"."
],
"completionItem": {
"labelDetailsSupport": true
}
},
"definitionProvider": true,
"workspaceSymbolProvider": true,
"documentFormattingProvider": true,
"workspace": {
"workspaceFolders": {
"supported": true,
"changeNotifications": true
}
}
},
"serverInfo": {
"name": "redscript-ide",
"version": "0.2.7"
}
}


[Trace - 23:34:45] Sending notification 'initialized'
Params: {}


[Trace - 23:34:50] Sending notification 'textDocument/didOpen'
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds",
"languageId": "Redscript",
"version": 1,
"text": "module PawPunk.Test\nimport PawPunk.Funcs.*\n\nfunc Test(){\n    let serial \u003d aFunc();\n}\n\n"
}
}


[Trace - 23:34:51] Sending request 'textDocument/definition - (2)'.
Params: {
"textDocument": {
"uri": "file:///C:/prdev/mod/PawPunk/r6/scripts/PawPunk/test.reds"
},
"position": {
"line": 4,
"character": 17
}
}


[Trace - 23:34:51] Received response 'textDocument/definition - (2)' in 0ms.
Result: [
{
"uri": "file:C:/prdev/mod/PawPunk%5Cr6/scripts/PawPunk%5CPawPunk.Funcs.reds",
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




# logs from v0.1.46 working at cursor position 5:19


[Trace - 23:29:20] Sending request 'initialize - (1)'.
Params: {
"processId": 18292,
"rootPath": "C:/prdev/mod/PawPunk",
"rootUri": "file:///C:/prdev/mod/PawPunk",
"initializationOptions": {
"game_dir": "D:\\GAMES\\Cyberpunk 2077"
},
"capabilities": {
"workspace": {
"applyEdit": true,
"workspaceEdit": {
"documentChanges": true,
"resourceOperations": [
"create",
"delete",
"rename"
]
},
"didChangeConfiguration": {
"dynamicRegistration": true
},
"didChangeWatchedFiles": {
"dynamicRegistration": true
},
"symbol": {},
"executeCommand": {
"dynamicRegistration": true
},
"workspaceFolders": true,
"configuration": true,
"semanticTokens": {
"refreshSupport": true
},
"codeLens": {
"refreshSupport": true
},
"fileOperations": {
"didRename": true,
"willRename": true,
"dynamicRegistration": true
},
"inlayHint": {
"refreshSupport": true
}
},
"textDocument": {
"synchronization": {
"willSave": true,
"willSaveWaitUntil": true,
"didSave": true
},
"completion": {
"completionItem": {
"snippetSupport": true,
"documentationFormat": [
"markdown",
"plaintext"
],
"deprecatedSupport": true,
"resolveSupport": {
"properties": [
"documentation",
"detail",
"additionalTextEdits"
]
},
"insertTextModeSupport": {
"valueSet": [
1,
2
]
},
"labelDetailsSupport": true
},
"completionList": {
"itemDefaults": [
"editRange"
]
},
"dynamicRegistration": true
},
"hover": {
"contentFormat": [
"markdown",
"plaintext"
],
"dynamicRegistration": true
},
"signatureHelp": {
"signatureInformation": {
"parameterInformation": {
"labelOffsetSupport": true
}
},
"dynamicRegistration": true
},
"references": {
"dynamicRegistration": true
},
"documentHighlight": {
"dynamicRegistration": true
},
"documentSymbol": {
"symbolKind": {
"valueSet": [
18,
17,
5,
14,
9,
10,
22,
24,
8,
1,
12,
11,
20,
6,
2,
3,
21,
16,
19,
25,
4,
7,
15,
23,
26,
13
]
},
"hierarchicalDocumentSymbolSupport": true,
"dynamicRegistration": true
},
"formatting": {
"dynamicRegistration": true
},
"rangeFormatting": {
"dynamicRegistration": true
},
"declaration": {
"linkSupport": true,
"dynamicRegistration": true
},
"definition": {
"linkSupport": true,
"dynamicRegistration": true
},
"typeDefinition": {
"linkSupport": true,
"dynamicRegistration": true
},
"implementation": {
"linkSupport": true,
"dynamicRegistration": true
},
"codeAction": {
"codeActionLiteralSupport": {
"codeActionKind": {
"valueSet": [
"quickfix",
"refactor",
"refactor.extract",
"refactor.inline",
"refactor.rewrite",
"source",
"source.organizeImports"
]
}
},
"dataSupport": true,
"resolveSupport": {
"properties": [
"edit"
]
},
"dynamicRegistration": true
},
"codeLens": {
"dynamicRegistration": true
},
"documentLink": {
"dynamicRegistration": true
},
"colorProvider": {
"dynamicRegistration": true
},
"rename": {
"prepareSupport": true,
"dynamicRegistration": true
},
"publishDiagnostics": {
"relatedInformation": true,
"tagSupport": {
"valueSet": [
1,
2
]
},
"codeDescriptionSupport": true,
"dataSupport": true
},
"foldingRange": {
"dynamicRegistration": true
},
"typeHierarchy": {
"dynamicRegistration": true
},
"callHierarchy": {
"dynamicRegistration": true
},
"semanticTokens": {
"requests": {
"range": false,
"full": true
},
"tokenTypes": [
"namespace",
"type",
"class",
"enum",
"interface",
"struct",
"typeParameter",
"parameter",
"variable",
"property",
"enumMember",
"event",
"function",
"method",
"macro",
"keyword",
"modifier",
"comment",
"string",
"number",
"regexp",
"operator",
"decorator",
"label"
],
"tokenModifiers": [
"declaration",
"definition",
"readonly",
"static"
],
"formats": [
"relative"
],
"multilineTokenSupport": true,
"serverCancelSupport": true,
"dynamicRegistration": true
},
"inlayHint": {
"dynamicRegistration": true
},
"diagnostic": {
"relatedDocumentSupport": true,
"dynamicRegistration": true
}
},
"window": {
"workDoneProgress": true,
"showMessage": {},
"showDocument": {
"support": true
}
},
"general": {
"staleRequestSupport": {
"cancel": true,
"retryOnContentModified": []
},
"positionEncodings": [
"utf-16"
]
}
},
"clientInfo": {
"name": "IntelliJ IDEA 2025.1",
"version": "IntelliJ IDEA (build IC-251.23774.435)"
},
"trace": "off",
"workspaceFolders": [
{
"uri": "file:///C:/prdev/mod/PawPunk",
"name": "PawPunk"
}
]
}


[Trace - 23:29:20] Received response 'initialize - (1)' in 0ms.
Result: {
"capabilities": {
"textDocumentSync": 2,
"hoverProvider": true,
"completionProvider": {
"resolveProvider": true,
"triggerCharacters": [
"."
]
},
"definitionProvider": true,
"workspaceSymbolProvider": true,
"documentFormattingProvider": true,
"workspace": {
"workspaceFolders": {
"supported": true,
"changeNotifications": true
}
}
}
}


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
"text": "module PawPunk.Test\nimport PawPunk.Funcs.*\n\nfunc Test(){\n    let serial \u003d aFunc();\n}\n\n"
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



