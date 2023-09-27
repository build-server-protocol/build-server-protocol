load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_binary")

scala_library(
    name = "testkit",
    srcs = glob(["src/main/scala/**/*.scala"]),
    deps = [
        "//bsp4j",
        "@maven//:org_scalacheck_scalacheck_2_13",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:com_google_code_gson_gson",
        "@maven//:de_danielbechler_java_object_diff",
        "@maven//:org_scala_lang_modules_scala_java8_compat_2_13",
        "@maven//:org_scala_lang_modules_scala_collection_compat_2_13",
    ],
    visibility = ["//visibility:public"],
)

scala_binary(
    name = "mockbsp",
    deps = ["//testkit"],
    main_class = "ch.epfl.scala.bsp.testkit.mock.MockServer",
    visibility = ["//visibility:public"],
)
