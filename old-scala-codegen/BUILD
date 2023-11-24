load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "old-scala-codegen",
    srcs = glob(["src/main/scala/**/*.scala"]),
    resources = glob(["src/main/resources/**/*"]),  # "@//spec" is not here because a copy is already in the new codegen
    visibility = ["//visibility:public"],
    deps = [
        "@//spec-traits",
        "@//codegen",
        "@maven//:org_scala_lang_modules_scala_collection_compat_2_13",
        "@maven//:software_amazon_smithy_smithy_codegen_core",
        "@maven//:software_amazon_smithy_smithy_model",
        "@maven//:software_amazon_smithy_smithy_utils",
        "@maven//:com_lihaoyi_os_lib_2_13",
        "@maven//:com_lihaoyi_geny_2_13",
        "@maven//:com_monovore_decline_2_13",
        "@maven//:org_typelevel_cats_core_2_13",
        "@maven//:org_typelevel_cats_kernel_2_13",
        # xtend deps
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_inject_guice",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_lib",
        "@maven//:org_eclipse_xtend_org_eclipse_xtend_core",
        "@maven//:org_eclipse_xtext_org_eclipse_xtext_xbase_lib",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_generator",
    ],
)
