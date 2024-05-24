load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "codegen",
    srcs = glob(["src/main/kotlin/bsp/codegen/common/**/*.kt"]),
    resources = ["@//spec"] + glob(["src/main/resources/**/*"]),
    visibility = ["//visibility:public"],
    deps = [
        "@//spec-traits",
        "@maven//:com_google_code_gson_gson",
        "@maven//:software_amazon_smithy_smithy_codegen_core",
        "@maven//:software_amazon_smithy_smithy_model",
    ],
)

kt_jvm_library(
    name = "bsp4j",
    srcs = glob(["src/main/kotlin/bsp/codegen/bsp4j/**/*.kt"]),
    visibility = ["//visibility:public"],
    deps = [
        "@//codegen",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_inject_guice",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_generator",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_core",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_lib",
        "@maven//:org_eclipse_xtext_org_eclipse_xtext_xbase_lib",
    ],
)

kt_jvm_library(
    name = "bsp4s",
    srcs = glob(["src/main/kotlin/bsp/codegen/bsp4s/**/*.kt"]),
    visibility = ["//visibility:public"],
    deps = [
        "@//codegen",
    ],
)

kt_jvm_library(
    name = "docs",
    srcs = glob(["src/main/kotlin/bsp/codegen/docs/**/*.kt"]),
    visibility = ["//visibility:public"],
    deps = [
        "@//codegen",
    ],
)
