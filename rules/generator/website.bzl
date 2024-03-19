load("@aspect_bazel_lib//lib:run_binary.bzl", "run_binary")
load("@aspect_bazel_lib//lib:write_source_files.bzl", "write_source_files")
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")
load("@rules_multirun//:defs.bzl", "command")

def website(name, jars, data, library_version, **kwargs):
    scala_binary(
        name = "mdoc",
        deps = ["@maven//:org_scalameta_mdoc_2_13"],
        main_class = "mdoc.Main",
    )

    # extract jars from deps using $(location) and join them with ':'
    classpath = ":".join(["$(location {})".format(jar) for jar in jars])

    run_binary(
        name = "%s_run" % name,
        tool = ":mdoc",
        out_dirs = ["website-generated"],
        srcs = jars + data,
        args = [
            "--classpath",
            classpath,
            "--in",
            "docs",
            "--out",
            "$(RULEDIR)/website-generated",
            "--site.LIBRARY_VERSION",
            library_version,
        ],
        **kwargs
    )

    write_source_files(
        name = name,
        files = {
            "generated": "%s_run" % name,
        },
        suggested_update_target = "%s_command" % name,
    )

    command(
        name = "%s_command" % name,
        command = ":%s" % name,
        visibility = ["//visibility:public"],
    )
