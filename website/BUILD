load("@npm//:defs.bzl", "npm_link_all_packages")
load("@npm//website:@docusaurus/core/package_json.bzl", docusaurus_bin = "bin")
load("//rules/generator:website.bzl", "website")
load("@aspect_rules_js//js:defs.bzl", "js_binary", "js_run_binary")

npm_link_all_packages(name = "node_modules")

filegroup(
    name = "website-files",
    srcs = glob([
        "generated/**/*",
        "src/**/*",
        "static/**/*",
    ]) + [
        "docusaurus.config.js",
        "package.json",
        "sidebars.json",
        ":node_modules/@docusaurus/plugin-client-redirects",
        ":node_modules/@docusaurus/preset-classic",
        ":node_modules/@easyops-cn/docusaurus-search-local",
        ":node_modules/@mdx-js/react",
        ":node_modules/react",
    ],
)

docusaurus_bin.docusaurus_binary(
    name = "docusaurus-start",
    args = [
        "start",
        "--config",
        "docusaurus.config.js",
    ],
    chdir = package_name(),
    data = [":website-files"],
    visibility = ["//visibility:public"],
)

docusaurus_bin.docusaurus_binary(
    name = "docusaurus-build",
    args = [
        "build",
        "--config",
        "docusaurus.config.js",
    ],
    chdir = package_name(),
    data = [":website-files"],
    visibility = ["//visibility:public"],
)

docusaurus_bin.docusaurus_binary(
    name = "docusaurus-deploy",
    args = [
        "deploy",
        "--config",
        "docusaurus.config.js",
    ],
    chdir = package_name(),
    data = [":website-files"],
    visibility = ["//visibility:public"],
)

website(
    name = "generate-website",
    data = ["//docs"],
    jars = [
        "//bsp4j",
        "//old-scala-codegen",
        "//codegen:codegen.jar",
        "@maven//:org_eclipse_lsp4j_org_eclipse_lsp4j_jsonrpc",
        "@maven//:com_google_code_gson_gson",
        "@rules_kotlin//kotlin/compiler:kotlin-stdlib",
        "@maven//:software_amazon_smithy_smithy_model",
        "@maven//:software_amazon_smithy_smithy_utils",
        "//spec-traits:spec-traits.jar",
        "@maven//:com_monovore_decline_2_13",
        "@maven//:org_typelevel_cats_core_2_13",
        "@maven//:org_typelevel_cats_kernel_2_13",
    ],
    library_version = "2.1.0",
)
