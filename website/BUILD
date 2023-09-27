load("@npm//:defs.bzl", "npm_link_all_packages")
load("@npm//website:@docusaurus/core/package_json.bzl", docusaurus_bin = "bin")

load("@aspect_rules_js//js:defs.bzl", "js_run_binary", "js_binary")

npm_link_all_packages(name = "node_modules")

filegroup(
    name = "website-files",
    srcs = glob(["generated/**/*", "src/**/*", "static/**/*"]) + [
        "sidebars.json",
        "docusaurus.config.js",
        "package.json",
        ":node_modules/@docusaurus/preset-classic",
        ":node_modules/@docusaurus/plugin-client-redirects",
        ":node_modules/@easyops-cn/docusaurus-search-local",
        ":node_modules/react",
        ":node_modules/@mdx-js/react",
    ],
)

docusaurus_bin.docusaurus_binary(
    name = "docusaurus-start",
    args = ["start", "--config", "docusaurus.config.js"],
    chdir = package_name(),
    data = [":website-files"],
    visibility = ["//visibility:public"],
)

docusaurus_bin.docusaurus_binary(
    name = "docusaurus-build",
    args = ["build", "--config", "docusaurus.config.js"],
    chdir = package_name(),
    data = [":website-files"],
    visibility = ["//visibility:public"],
)

docusaurus_bin.docusaurus_binary(
    name = "docusaurus-deploy",
    args = ["deploy", "--config", "docusaurus.config.js"],
    chdir = package_name(),
    data = [":website-files"],
    visibility = ["//visibility:public"],
)
