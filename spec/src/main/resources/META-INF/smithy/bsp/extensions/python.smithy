$version: "2"

namespace bsp.python

use bsp#BuildTargetData
use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp#URI
use jsonrpc#dataKind
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

@jsonRPC
service PythonBuildServer {
    operations: [
        BuildTargetPythonOptions
    ]
}

/// `PythonBuildTarget` is a basic data structure that contains Python-specific
/// metadata, specifically the interpreter reference and the Python version.
@dataKind(kind: "python", extends: [BuildTargetData])
structure PythonBuildTarget {
    version: String
    interpreter: URI
}

/// The Python Options Request is sent from the client to the server to
/// query for the list of the interpreter flags used to run a given list of
/// targets.
@jsonRequest("buildTarget/pythonOptions")
operation BuildTargetPythonOptions {
    input: PythonOptionsParams
    output: PythonOptionsResult
}

structure PythonOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

structure PythonOptionsResult {
    @required
    items: PythonOptionsItems
}

structure PythonOptionsItem {
    @required
    target: BuildTargetIdentifier
    /// Attributes added to the interpreter command
    /// For example: -E
    @required
    interpreterOptions: PythonInterpreterOptions
}

list PythonInterpreterOptions {
    member: String
}

list PythonOptionsItems {
    member: PythonOptionsItem
}
