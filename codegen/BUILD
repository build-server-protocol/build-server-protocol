load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "codegen",
    srcs = glob(["src/main/kotlin/**/*.kt"]),
    resources = ["@//spec"],
    visibility = ["//visibility:public"],
    deps = [
        "@//spec-traits",
        "@maven//:software_amazon_smithy_smithy_codegen_core",
        "@maven//:software_amazon_smithy_smithy_model",
    ],
)
