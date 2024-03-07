load("@rules_jvm_external//:defs.bzl", "java_export")

java_export(
    name = "bsp4j",
    srcs = glob(["src/**/*.java"]),
    maven_coordinates = "ch.epfl.scala:bsp4j:0.0.1",
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
