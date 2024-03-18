load("@rules_jvm_external//:defs.bzl", "java_export")
load("//rules/generator:generator.bzl", "library_generator")
load("//:version.bzl", "BSP_VERSION")

library_generator(
    name = "generate-bsp4j",
    gen_tool = "//:bsp4j-generator",
    out_dir = "src",
    visibility = ["//visibility:public"],
)

java_export(
    name = "bsp4j",
    srcs = glob(["src/**/*.java"]),
    data = [
        ":generate-bsp4j",
    ],
    maven_coordinates = "ch.epfl.scala:bsp4j:%s" % BSP_VERSION,
    pom_template = "//tools/maven:pom.tpl",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_google_code_gson_gson",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_core",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_lib",
        "@maven//:org_eclipse_xtext_org_eclipse_xtext_xbase_lib",
    ],
)
