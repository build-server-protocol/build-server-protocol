$version: "2"

namespace bsp.jvm

use bsp#Arguments
use bsp#BuildTargetData
use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp#EnvironmentVariables
use bsp#Identifier
use bsp#URI
use traits#dataKind
use traits#jsonRPC
use traits#jsonRequest

@jsonRPC
service JvmBuildServer {
    operations: [
        BuildTargetJvmTestEnvironment,
        BuildTargetJvmRunEnvironment
    ]
}

/// `JvmBuildTarget` is a basic data structure that contains jvm-specific
/// metadata, specifically JDK reference.
@dataKind(kind: "jvm", extends: [BuildTargetData])
structure JvmBuildTarget {
    /// Uri representing absolute path to jdk
    /// For example: file:///usr/lib/jvm/java-8-openjdk-amd64
    javaHome: URI
    /// The java version this target is supposed to use.
    /// For example: 1.8
    javaVersion: String
}

structure JvmEnvironmentItem {
    @required
    target: BuildTargetIdentifier
    @required
    classpath: Classpath
    @required
    jvmOptions: JvmOptions
    @required
    workingDirectory: String
    @required
    environmentVariables: EnvironmentVariables
    mainClasses: JvmMainClasses
}

/// The JVM test environment request is sent from the client to the server in order to
/// gather information required to launch a Java process. This is useful when the
/// client wants to control the Java process execution, for example to enable custom
/// Java agents or launch a custom main class during unit testing or debugging
///
/// The data provided by this endpoint may change between compilations, so it should
/// not be cached in any form. The client should ask for it right before test execution,
/// after all the targets are compiled.
@jsonRequest("buildTarget/jvmTestEnvironment")
operation BuildTargetJvmTestEnvironment {
    input: JvmTestEnvironmentParams
    output: JvmTestEnvironmentResult
}

structure JvmTestEnvironmentParams {
    @required
    targets: BuildTargetIdentifiers
    originId: Identifier
}

structure JvmTestEnvironmentResult {
    @required
    items: JvmEnvironmentItems
}

list JvmEnvironmentItems {
    member: JvmEnvironmentItem
}

/// Similar to `buildTarget/jvmTestEnvironment`, but returns environment
/// that should be used for regular exection of main classes, not for testing
@jsonRequest("buildTarget/jvmRunEnvironment")
operation BuildTargetJvmRunEnvironment {
    input: JvmRunEnvironmentParams
    output: JvmRunEnvironmentResult
}

structure JvmRunEnvironmentParams {
    @required
    targets: BuildTargetIdentifiers
    originId: Identifier
}

structure JvmMainClass {
    @required
    className: String
    @required
    arguments: Arguments
}

structure JvmRunEnvironmentResult {
    @required
    items: JvmEnvironmentItems
}

list JvmMainClasses {
    member: JvmMainClass
}

list Classpath {
    member: String
}

list JvmOptions {
    member: String
}
