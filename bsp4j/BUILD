load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "bsp4j",
    srcs = glob(["src/**/*.java"]),
    deps = [
        "@maven//:com_google_code_gson_gson",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_generator",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_core",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_lib",
        "@maven//:org_eclipse_xtext_org_eclipse_xtext_xbase_lib",
    ],
    visibility = ["//visibility:public"],
)
