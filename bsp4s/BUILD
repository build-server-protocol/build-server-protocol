load("//rules/scala:scala_export.bzl", "sonatype_scala_export")
load("//rules/generator:generator.bzl", "library_generator")
load("//:version.bzl", "BSP_VERSION")

library_generator(
    name = "generate-bsp4s",
    gen_tool = "//:bsp4s-generator",
    out_dir = "src",
    visibility = ["//visibility:public"],
)

sonatype_scala_export(
    name = "bsp4s",
    srcs = glob(["src/**/*.scala"]),
    data = [":generate-bsp4s"],
    maven_coordinates = "ch.epfl.scala:bsp4s_2.13:%s" % BSP_VERSION,
    maven_profile = "ch.epfl.scala",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_core_2_13",
        "@maven//:com_github_plokhotnyuk_jsoniter_scala_jsoniter_scala_macros_2_13",
        "@maven//:me_vican_jorge_jsonrpc4s_2_13",
    ],
)
