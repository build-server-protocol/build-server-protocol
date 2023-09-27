load("@rules_multirun//:defs.bzl", "command")

def _impl(ctx):
    name = ctx.label.name
    library_name = ctx.attr.library_name
    generator_script = ctx.actions.declare_file("%s_gen.sh" % library_name)
    output = ctx.actions.declare_directory("%s_gen" % library_name)

    ctx.actions.run(
        outputs = [generator_script, output],
        arguments = [library_name, output.path, generator_script.path],
        progress_message = "Generating the %s library" % library_name,
        executable = ctx.executable.gen_tool,
    )

    return [DefaultInfo(executable = generator_script)]

_library_generator = rule(
    executable = True,
    implementation = _impl,
    attrs = {
        "library_name": attr.string(mandatory = True),
        "gen_tool": attr.label(
            executable = True,
            allow_files = True,
            cfg = "exec",
        ),
    },
)

def library_generator(name, library_name, gen_tool, **kwargs):
    _library_generator(
        name = name,
        library_name = library_name,
        gen_tool = gen_tool,
        **kwargs
    )

    command(
        name = "%s_command" % name,
        command = ":%s" % name,
        **kwargs
    )
