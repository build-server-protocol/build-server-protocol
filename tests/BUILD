load("@io_bazel_rules_scala//scala:scala.bzl", "scala_test")

scala_test(
    name = "tests",
    srcs = glob(["src/test/scala/**/*.scala"]),
    deps = [
        "//bsp4s",
        "//bsp4j",
        "//testkit",
        "@maven//:com_googlecode_java_diff_utils_diffutils",
        "@maven//:org_scala_lang_modules_scala_java8_compat_2_13",
        "@maven//:org_scala_sbt_ipcsocket_ipcsocket",
        "@maven//:org_scalatest_scalatest_2_13",
        "@maven//:org_scalatestplus_scalacheck_1_16_2_13",
        "@maven//:org_scalacheck_scalacheck_2_13",
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_core_2_13",
        "@maven//:me_vican_jorge_jsonrpc4s_2_13",
        "@maven//:com_google_code_gson_gson",
        "@maven//:com_google_guava_guava",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:io_monix_monix_2_13",
        "@maven//:io_monix_monix_execution_2_13",
        "@maven//:io_monix_monix_eval_2_13",
        "@maven//:com_outr_scribe_2_13",
    ],
    visibility = ["//visibility:public"],
)
