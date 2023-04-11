$version: "2"

namespace bsp

use jsonrpc#data

@data(kind: "cpp", extends: BuildTargetData)
structure CppBuildTarget {
    version: String
    compiler: String
    cCompiler: String
    cppCompiler: String
}

list Copts {
    member: String
}

list Defines {
    member: String
}

list Linkopts {
    member: String
}

structure CppOptionsItem {
    @required
    target: BuildTargetIdentifier
    @required
    copts: Copts
    @required
    defines: Defines
    @required
    linkopts: Linkopts
    linkshared: Boolean
}

structure CppOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

list CppOptionsItems {
    member: CppOptionsItem
}

structure CppOptionsResult {
    @required
    items: CppOptionsItems
}
