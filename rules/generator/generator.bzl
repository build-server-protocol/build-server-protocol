load("@rules_multirun//:defs.bzl", "command")
load("@aspect_bazel_lib//lib:run_binary.bzl", "run_binary")
load("@aspect_bazel_lib//lib:write_source_files.bzl", "write_source_files")

def library_generator(name, gen_tool, out_dir, **kwargs):
    run_binary(
        name = "%s_run" % name,
        tool = gen_tool,
        out_dirs = [out_dir],
        args = ["$(RULEDIR)/%s" % out_dir],
    )

    write_source_files(
        name = name,
        files = {
            out_dir: "%s_run" % name,
        },
        suggested_update_target = "%s_command" % name,
        **kwargs
    )

    command(
        name = "%s_command" % name,
        command = ":%s" % name,
        **kwargs
    )
