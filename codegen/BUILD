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

# This one is deprecated. Use the new codegen for writing new generators.
# scala_library(
#     name = "old-scala-codegen",
#     srcs = glob(["src/main/scala/**/*.scala"]),
#     resources = glob(["src/main/resources/**/*"]),  # "@//spec" is not here because a copy is already in the new codegen
#     visibility = ["//visibility:public"],
#     deps = [
#         "@//spec-traits",
#         "@//codegen",
#         "@maven//:org_scala_lang_modules_scala_collection_compat_2_13",
#         "@maven//:software_amazon_smithy_smithy_codegen_core",
#         "@maven//:software_amazon_smithy_smithy_model",
#         "@maven//:software_amazon_smithy_smithy_utils",
#         "@maven//:com_lihaoyi_os_lib_2_13",
#         "@maven//:com_lihaoyi_geny_2_13",
#         "@maven//:com_monovore_decline_2_13",
#         "@maven//:org_typelevel_cats_core_2_13",
#         "@maven//:org_typelevel_cats_kernel_2_13",
#         # xtend deps
#         "@maven//:com_google_guava_guava",
#         "@maven//:com_google_inject_guice",
#         "@maven//:org_eclipse_xtend_org_eclipse_xtend_lib",
#         "@maven//:org_eclipse_xtend_org_eclipse_xtend_core",
#         "@maven//:org_eclipse_xtext_org_eclipse_xtext_xbase_lib",
#         "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j",
#         "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
#         "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_generator",
#     ],
# )
