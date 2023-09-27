load("@bazel_skylib//rules:run_binary.bzl", "run_binary")
load("@bazel_skylib//rules:copy_directory.bzl", "copy_directory")
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")
load("@rules_multirun//:defs.bzl", "command")

def website(name, jars, data, library_version, **kwargs):
    scala_binary(
        name = "mdoc",
        deps = ["@maven//:org_scalameta_mdoc_2_13"],
        main_class = "mdoc.Main",
    )

    # extract jars from deps using $(location) and join them with :
    classpath = ":".join(["$(location {})".format(jar) for jar in jars])

    run_binary(
        name = "%s_run" % name,
        tool = ":mdoc",
        outs = ["website-generated"],
        srcs = jars + data,
        args = [
            "--classpath",
            classpath,
            "--in",
            "docs",
            "--out",
            "$(location website-generated)",
            "--site.LIBRARY_VERSION",
            library_version,
        ],
        **kwargs
    )

    native.sh_binary(
        name = name,
        srcs = ["//rules/generator:copy_to_repo.sh"],
        args = ["website-generated", "website/generated"],
        data = [":website-generated"],
    )

    command(
        name = "%s_command" % name,
        command = ":%s" % name,
        arguments = ["website-generated", "website/generated"],
        data = [":website-generated"],
    )
