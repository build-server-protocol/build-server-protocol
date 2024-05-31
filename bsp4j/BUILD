load("@bazel_sonatype//:defs.bzl", "sonatype_java_export")
load("//:version.bzl", "BSP_VERSION")
load("//tools/rules/generator:generator.bzl", "library_generator")

library_generator(
    name = "generate-bsp4j",
    gen_tool = "//:bsp4j-generator",
    out_dir = "src",
    visibility = ["//visibility:public"],
)

sonatype_java_export(
    name = "bsp4j",
    srcs = glob(["src/**/*.java"]),
    data = [
        ":generate-bsp4j",
    ],
    maven_coordinates = "ch.epfl.scala:bsp4j:%s" % BSP_VERSION,
    maven_profile = "ch.epfl.scala",
    pom_template = "//tools/maven:pom.tpl",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_google_code_gson_gson",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_generator",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
    ],
)
