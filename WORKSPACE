load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

bazel_skylib_version = "1.4.1"

bazel_skylib_sha = "b8a1527901774180afc798aeb28c4634bdccf19c4d98e7bdd1ce79d1fe9aaad7"

http_archive(
    name = "bazel_skylib",
    sha256 = bazel_skylib_sha,
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/%s/bazel-skylib-%s.tar.gz" % (bazel_skylib_version, bazel_skylib_version),
        "https://github.com/bazelbuild/bazel-skylib/releases/download/%s/bazel-skylib-%s.tar.gz" % (bazel_skylib_version, bazel_skylib_version),
    ],
)

http_archive(
    name = "rules_python",
    sha256 = "5868e73107a8e85d8f323806e60cad7283f34b32163ea6ff1020cf27abef6036",
    strip_prefix = "rules_python-0.25.0",
    url = "https://github.com/bazelbuild/rules_python/releases/download/0.25.0/rules_python-0.25.0.tar.gz",
)

load("@rules_python//python:repositories.bzl", "py_repositories")

py_repositories()

rules_scala_version = "6.1.0"

rules_scala_sha = "cc590e644b2d5c6a87344af5e2c683017fdc85516d9d64b37f15d33badf2e84c"

http_archive(
    name = "io_bazel_rules_scala",
    sha256 = rules_scala_sha,
    strip_prefix = "rules_scala-%s" % rules_scala_version,
    url = "https://github.com/bazelbuild/rules_scala/releases/download/v%s/rules_scala-v%s.tar.gz" % (rules_scala_version, rules_scala_version),
)

load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")

# Stores Scala version and other configuration
# 2.12 is a default version, other versions can be use by passing them explicitly:
# scala_config(scala_version = "2.11.12")
# Scala 3 requires extras...
#   3.2 should be supported on master. Please note that Scala artifacts for version (3.2.2) are not defined in
#   Rules Scala, they need to be provided by your WORKSPACE. You can use external loader like
#   https://github.com/bazelbuild/rules_jvm_external
scala_config(scala_version = "2.13.6")

load("@io_bazel_rules_scala//scala:scala.bzl", "rules_scala_setup", "rules_scala_toolchain_deps_repositories")

# loads other rules Rules Scala depends on
rules_scala_setup()

# Loads Maven deps like Scala compiler and standard libs. On production projects you should consider
# defining a custom deps toolchains to use your project libs instead
rules_scala_toolchain_deps_repositories(fetch_sources = True)

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")

rules_proto_dependencies()

rules_proto_toolchains()

load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")

scala_register_toolchains()

load("@io_bazel_rules_scala//testing:scalatest.bzl", "scalatest_repositories", "scalatest_toolchain")

scalatest_repositories()

scalatest_toolchain()

rules_kotlin_version = "1.8"

rules_kotlin_sha = "01293740a16e474669aba5b5a1fe3d368de5832442f164e4fbfc566815a8bc3a"

http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = rules_kotlin_sha,
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/v%s/rules_kotlin_release.tgz" % rules_kotlin_version],
)

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")

kotlin_repositories()  # if you want the default. Otherwise see custom kotlinc distribution below

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")

kt_register_toolchains()  # to use the default toolchain, otherwise see toolchains below

RULES_JVM_EXTERNAL_TAG = "5.3"

RULES_JVM_EXTERNAL_SHA = "d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-{}".format(RULES_JVM_EXTERNAL_TAG),
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_TAG, RULES_JVM_EXTERNAL_TAG),
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "software.amazon.smithy:smithy-model:1.34.0",
        "software.amazon.smithy:smithy-codegen-core:1.34.0",
        "software.amazon.smithy:smithy-utils:1.34.0",

        # tests
        "org.junit.jupiter:junit-jupiter:5.10.0",
        "org.junit.jupiter:junit-jupiter-api:5.10.0",
        "org.junit.jupiter:junit-jupiter-engine:5.10.0",
        "org.junit.jupiter:junit-jupiter-params:5.10.0",
        "org.junit.platform:junit-platform-console:1.10.0",

        # docs
        "org.scalameta:mdoc_2.13:2.3.7",

        # scala
        "org.scala-lang.modules:scala-collection-compat_2.13:2.11.0",
        "com.lihaoyi:os-lib_2.13:0.9.1",
        "com.lihaoyi:geny_2.13:1.0.0",
        "com.monovore:decline_2.13:2.4.1",
        "org.typelevel:cats-core_2.13:2.9.0",
        "org.typelevel:cats-kernel_2.13:2.9.0",

        # scala runtime libs
        "com.github.plokhotnyuk.jsoniter-scala:jsoniter-scala-core_2.13:2.23.2",
        "com.github.plokhotnyuk.jsoniter-scala:jsoniter-scala-macros_2.13:2.23.2",
        "me.vican.jorge:jsonrpc4s_2.13:0.1.0",

        # testkit
        "org.scalacheck:scalacheck_2.13:1.17.0",
        "de.danielbechler:java-object-diff:0.95",
        "org.scala-lang.modules:scala-java8-compat_2.13:1.0.2",
        "org.scala-lang.modules:scala-collection-compat_2.13:2.11.0",

        # scala tests
        "com.googlecode.java-diff-utils:diffutils:1.3.0",
        "org.scala-sbt.ipcsocket:ipcsocket:1.0.1",
        "org.scalatest:scalatest_2.13:3.2.16",
        "org.scalatestplus:scalacheck-1-16_2.13:3.2.14.0",

        # these must match the versions used by jsonrpc4s
        "io.monix:monix_2.13:3.2.0",
        "io.monix:monix-eval_2.13:3.2.0",
        "io.monix:monix-execution_2.13:3.2.0",
        "com.outr:scribe_2.13:3.5.5",

        # lsp4j 21.1 causes "b = new (this);" bug (missing ToStringBuilder)
        "org.eclipse.lsp4j:org.eclipse.lsp4j:0.20.1",
        "org.eclipse.lsp4j:org.eclipse.lsp4j.generator:0.20.1",
        "org.eclipse.lsp4j:org.eclipse.lsp4j.jsonrpc:0.20.1",

        # lsp4j deps versions must be aligned with lsp4j version
        "org.eclipse.xtend:org.eclipse.xtend.core:2.28.0",
        "org.eclipse.xtend:org.eclipse.xtend.lib:2.28.0",
        "org.eclipse.xtext:org.eclipse.xtext.xbase.lib:2.28.0",
        "com.google.guava:guava:30.1.1-jre",
        "com.google.inject:guice:7.0.0",
    ],
    fetch_sources = True,
    repositories = [
        "https://maven.google.com",
        "https://repo.maven.apache.org/maven2",
        "https://repo1.maven.org/maven2",
        "https://jitpack.io",
    ],
)

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "aspect_rules_js",
    sha256 = "77c4ea46c27f96e4aadcc580cd608369208422cf774988594ae8a01df6642c82",
    strip_prefix = "rules_js-1.32.2",
    url = "https://github.com/aspect-build/rules_js/releases/download/v1.32.2/rules_js-v1.32.2.tar.gz",
)

load("@aspect_rules_js//js:repositories.bzl", "rules_js_dependencies")

rules_js_dependencies()

load("@rules_nodejs//nodejs:repositories.bzl", "DEFAULT_NODE_VERSION", "nodejs_register_toolchains")

nodejs_register_toolchains(
    name = "nodejs",
    node_version = DEFAULT_NODE_VERSION,
)

load("@aspect_rules_js//npm:repositories.bzl", "npm_translate_lock")

npm_translate_lock(
    name = "npm",
    data = ["//website:package.json"],
    pnpm_lock = "//website:pnpm-lock.yaml",
    update_pnpm_lock = True,
    verify_node_modules_ignored = "//:.bazelignore",
)

load("@npm//:repositories.bzl", "npm_repositories")

npm_repositories()

http_archive(
    name = "rules_multirun",
    sha256 = "9cd384e42b2da00104f0e18f25e66285aa21f64b573c667638a7a213206885ab",
    strip_prefix = "rules_multirun-0.6.1",
    url = "https://github.com/keith/rules_multirun/archive/refs/tags/0.6.1.tar.gz",
)

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "aspect_rules_format",
    commit = "e7c4f86e470f7ddb4b363cf1d343bde56594277b",
    remote = "https://github.com/agluszak/bazel-super-formatter.git",
)

load("@aspect_rules_format//format:repositories.bzl", "rules_format_dependencies")

rules_format_dependencies()

load("@aspect_rules_format//format:dependencies.bzl", "parse_dependencies")

parse_dependencies()

# Installs toolchains for running programs under Node, Python, etc.
# Be sure to register your own toolchains before this.
# Most users should do this LAST in their WORKSPACE to avoid getting our versions of
# things like the Go toolchain rather than the one you intended.
load("@aspect_rules_format//format:toolchains.bzl", "format_register_toolchains")

format_register_toolchains()
