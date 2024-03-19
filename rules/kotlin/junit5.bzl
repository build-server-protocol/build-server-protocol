load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_test")

def kt_test(name, src, classname = "", deps = [], runtime_deps = [], **kwargs):
    if type(src) != "string":
        fail("'src' has to be a string with file name!")

    if (classname == ""):
        classname = _guess_classname(name)

    kt_jvm_test(
        name = name,
        main_class = "org.junit.platform.console.ConsoleLauncher",
        args = ["--select-class=" + classname, "--fail-if-no-tests"],
        srcs = [src],
        deps = deps + [
            "@maven//:org_junit_jupiter_junit_jupiter_api",
            "@maven//:org_junit_jupiter_junit_jupiter_engine",
            "@maven//:org_junit_jupiter_junit_jupiter_params",
            "@maven//:org_junit_platform_junit_platform_console",
        ],
        **kwargs
    )

def _guess_classname(name):
    package = _guess_class_package()

    return package + "." + name

def _guess_class_package():
    package_name = native.package_name()
    package_name_without_last_slash = package_name.rstrip("/")
    _, _, package_name_without_org = package_name_without_last_slash.partition("/org/")
    real_package_name_with_slashes = "org/" + package_name_without_org
    package_name_with_dots = real_package_name_with_slashes.replace("/", ".")

    return package_name_with_dots
