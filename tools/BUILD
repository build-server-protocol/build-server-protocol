load("@aspect_rules_lint//format:defs.bzl", "multi_formatter_binary")

multi_formatter_binary(
    name = "format",
    java = ":java-format",
    kotlin = ":ktfmt",
)


java_binary(
    name = "java-format",
    jvm_flags = [
        "--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    ],
    main_class = "com.google.googlejavaformat.java.Main",
    runtime_deps = ["@google-java-format//jar"],
)

java_binary(
    name = "ktfmt",
    main_class = "com.facebook.ktfmt.cli.Main",
    runtime_deps = ["@ktfmt//jar"],
)