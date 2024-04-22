load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")
load("@io_bazel_rules_scala//scala:scala_import.bzl", "scala_import")
load("@rules_multirun//:defs.bzl", "command", "multirun")
load("//tools/rules/generator:generator.bzl", "library_generator")
load("//tools/rules/generator:website.bzl", "website")

exports_files([".scalafmt.conf"])

# This is a wrapper which ensures that all generators are first run in parallel and then
#  the formatter is run sequentially, i.e. to avoid the formatter running in parallel
#  with the generators.
multirun(
    name = "generate",
    commands = [
        "//:generate_all_command",
        "//:format_all_command",
    ],
)

multirun(
    name = "generate_all",
    commands = [
        "//bsp4j:generate-bsp4j_command",
        "//bsp4s:generate-bsp4s_command",
        "//website:generate-website_command",
    ],
    jobs = 0,
)

command(
    name = "generate_all_command",
    command = ":generate_all",
)

command(
    name = "format_all_command",
    command = "//tools/format",
)

java_binary(
    name = "bsp4j-generator",
    main_class = "bsp.codegen.bsp4j.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//codegen:old-scala-codegen",
    ],
)

java_binary(
    name = "bsp4s-generator",
    main_class = "bsp.codegen.bsp4s.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//codegen:old-scala-codegen",
    ],
)

command(
    name = "publish_bsp4j",
    command = "//bsp4j:bsp4j.publish",
)

command(
    name = "publish_bsp4s",
    command = "//bsp4s:bsp4s.publish",
)

multirun(
    name = "publish",
    commands = [
        ":publish_bsp4j",
        ":publish_bsp4s",
    ],
)
