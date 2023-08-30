$version: "2"

namespace bsp.java

use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp.jvm#Classpath
use traits#jsonRPC
use traits#jsonRequest

@jsonRPC
service JavaBuildServer {
    operations: [
        BuildTargetJavacOptions
    ]
}

/// The build target javac options request is sent from the client to the server to
/// query for the list of compiler options necessary to compile in a given list of
/// targets.
@jsonRequest("buildTarget/javacOptions")
operation BuildTargetJavacOptions {
    input: JavacOptionsParams
    output: JavacOptionsResult
}

structure JavacOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

structure JavacOptionsResult {
    @required
    items: JavacOptionsItems
}

list JavacOptionsList {
    member: String
}

structure JavacOptionsItem {
    @required
    target: BuildTargetIdentifier
    /// Additional arguments to the compiler.
    /// For example, -deprecation.
    @required
    options: JavacOptionsList
    /// The dependency classpath for this target, must be
    /// identical to what is passed as arguments to
    /// the -classpath flag in the command line interface
    /// of javac.
    @required
    classpath: Classpath
    /// The output directory for classfiles produced by this target
    @required
    classDirectory: String
}

list JavacOptionsItems {
    member: JavacOptionsItem
}


