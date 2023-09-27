load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "bsp4s",
    srcs = glob(["src/**/*.scala"]),
    deps = [
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_core_2_13",
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_macros_2_13",
        "@maven//:me_vican_jorge_jsonrpc4s_2_13",
    ],
    visibility = ["//visibility:public"],
)
