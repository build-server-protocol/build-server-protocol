load("//rules/scala:scala_export.bzl", "scala_export")

scala_export(
    name = "bsp4s",
    srcs = glob(["src/**/*.scala"]),
    maven_coordinates = "ch.epfl.scala:bsp4s_2.13:0.1.0",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_core_2_13",
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_macros_2_13",
        "@maven//:me_vican_jorge_jsonrpc4s_2_13",
    ],
)
