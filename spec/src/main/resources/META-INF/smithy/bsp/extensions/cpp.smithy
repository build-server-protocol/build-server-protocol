$version: "2"

namespace bsp.cpp

use bsp#BuildTargetData
use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp#URI
use jsonrpc#dataKind
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

@jsonRPC
service CppBuildServer {
    operations: [
        BuildTargetCppOptions
    ]
}

/// `CppBuildTarget` is a basic data structure that contains c++-specific
/// metadata, specifically compiler reference.
@dataKind(kind: "cpp", extends: [BuildTargetData])
structure CppBuildTarget {
    /// The c++ version this target is supposed to use.
    /// For example: C++11
    version: String
    /// The type of compiler this target is supposed to use.
    /// For example: gcc
    compiler: String
    /// Uri representating path to the c compiler.
    /// For example: file:///usr/bin/gcc
    cCompiler: URI
    /// Uri representating path to the c++ compiler.
    /// For example: file:///usr/bin/g++
    cppCompiler: URI
}

/// The build target cpp options request is sent from the client to the server to
/// query for the list of compiler options necessary to compile in a given list of
/// targets.
@jsonRequest("buildTarget/cppOptions")
operation BuildTargetCppOptions {
    input: CppOptionsParams
    output: CppOptionsResult
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
    /// The target identifier for which the options are requested.
    @required
    target: BuildTargetIdentifier
    /// Attributes added in the given order to COPTS
    /// before compiling the target.
    /// For example: -Iexternal/gtest/include
    @required
    copts: Copts
    /// Attributes prepended with -D
    /// and added to the compile command line
    /// For example: BOOST_FALLTHROUGH
    @required
    defines: Defines
    /// Attributes added to the linker command
    /// For example: -pthread
    @required
    linkopts: Linkopts
    /// Create a shared library.
    /// The presence of this flag means that linking occurs with the -shared flag
    linkshared: Boolean
}

structure CppOptionsParams {
    /// The targets for which the options are requested.
    @required
    targets: BuildTargetIdentifiers
}

list CppOptionsItems {
    member: CppOptionsItem
}

structure CppOptionsResult {
    /// The list of options for each target.
    @required
    items: CppOptionsItems
}
