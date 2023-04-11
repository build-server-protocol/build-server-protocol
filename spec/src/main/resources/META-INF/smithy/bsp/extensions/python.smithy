$version: "2"

namespace bsp

structure PythonBuildTarget {
    version: String
    interpreter: String
}

list PythonInterpreterOptions {
    member: String
}

structure PythonOptionsItem {
    @required
    target: BuildTargetIdentifier
    @required
    interpreterOptions: PythonInterpreterOptions
}

structure PythonOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

list PythonOptionsItems {
    member: PythonOptionsItem
}

structure PythonOptionsResult {
    @required
    items: PythonOptionsItems
}
