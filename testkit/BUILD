load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary", "scala_library")

scala_library(
    name = "testkit",
    srcs = glob(["src/main/scala/**/*.scala"]),
    visibility = ["//visibility:public"],
    deps = [
        "//bsp4j",
        "@maven//:com_google_code_gson_gson",
        "@maven//:de_danielbechler_java_object_diff",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:org_scala_lang_modules_scala_collection_compat_2_13",
        "@maven//:org_scala_lang_modules_scala_java8_compat_2_13",
        "@maven//:org_scalacheck_scalacheck_2_13",
    ],
)

scala_binary(
    name = "mockbsp",
    main_class = "ch.epfl.scala.bsp.testkit.mock.MockServer",
    visibility = ["//visibility:public"],
    deps = ["//testkit"],
)
