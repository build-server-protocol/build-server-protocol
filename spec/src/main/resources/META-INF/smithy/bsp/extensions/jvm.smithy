$version: "2"

namespace bsp

structure JvmBuildTarget {
    javaHome: String
    javaVersion: String
}

//   @NonNull
//  private BuildTargetIdentifier target;
//
//  @NonNull
//  private List<String> classpath;
//
//  @NonNull
//  private List<String> jvmOptions;
//
//  @NonNull
//  private String workingDirectory;
//
//  @NonNull
//  private Map<String, String> environmentVariables;
//
//  private List<JvmMainClass> mainClasses;

structure JvmMainClass {
    @required
    className: String
    @required
    arguments: Arguments
}

list JvmMainClasses {
    member: JvmMainClass
}

list JvmOptions {
    member: String
}

map EnvironmentVariables {
    key: String,
    value: String
}

list JvmEnvironmentItems {
    member: JvmEnvironmentItem
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

structure JvmRunEnvironmentParams {
    @required
    targets: BuildTargetIdentifiers
    originId: Identifier
}

structure JvmRunEnvironmentResult {
    @required
    items: JvmEnvironmentItems
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