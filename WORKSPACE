load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

BAZEL_SKYLIB_VERSION = "1.4.2"

BAZEL_SKYLIB_SHA = "66ffd9315665bfaafc96b52278f57c7e2dd09f5ede279ea6d39b2be471e7e3aa"

http_archive(
    name = "bazel_skylib",
    sha256 = BAZEL_SKYLIB_SHA,
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/%s/bazel-skylib-%s.tar.gz" % (BAZEL_SKYLIB_VERSION, BAZEL_SKYLIB_VERSION),
        "https://github.com/bazelbuild/bazel-skylib/releases/download/%s/bazel-skylib-%s.tar.gz" % (BAZEL_SKYLIB_VERSION, BAZEL_SKYLIB_VERSION),
    ],
)

RULES_PYTHON_VERSION = "0.26.0"

RULES_PYTHON_SHA = "9d04041ac92a0985e344235f5d946f71ac543f1b1565f2cdbc9a2aaee8adf55b"

http_archive(
    name = "rules_python",
    sha256 = RULES_PYTHON_SHA,
    strip_prefix = "rules_python-%s" % RULES_PYTHON_VERSION,
    url = "https://github.com/bazelbuild/rules_python/releases/download/%s/rules_python-%s.tar.gz" % (RULES_PYTHON_VERSION, RULES_PYTHON_VERSION),
)

load("@rules_python//python:repositories.bzl", "py_repositories")

py_repositories()

RULES_SCALA_VERSION = "6.2.0"

RULES_SCALA_SHA = "ae4e74b6c696f40544cafb06b26bf4e601f83a0f29fb6500f0275c988f8cfe40"

http_archive(
    name = "io_bazel_rules_scala",
    sha256 = RULES_SCALA_SHA,
    strip_prefix = "rules_scala-%s" % RULES_SCALA_VERSION,
    url = "https://github.com/bazelbuild/rules_scala/releases/download/v%s/rules_scala-v%s.tar.gz" % (RULES_SCALA_VERSION, RULES_SCALA_VERSION),
)

load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")

scala_config(scala_version = "2.13.12")

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

RULES_KOTLIN_VERSION = "1.8.1"

RULES_KOTLIN_SHA = "a630cda9fdb4f56cf2dc20a4bf873765c41cf00e9379e8d59cd07b24730f4fde"

http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = RULES_KOTLIN_SHA,
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/v%s/rules_kotlin_release.tgz" % RULES_KOTLIN_VERSION],
)

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")

kotlin_repositories()  # if you want the default. Otherwise see custom kotlinc distribution below

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")

kt_register_toolchains()  # to use the default toolchain, otherwise see toolchains below

RULES_JVM_EXTERNAL_VERSION = "5.3"

RULES_JVM_EXTERNAL_SHA = "d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-{}".format(RULES_JVM_EXTERNAL_VERSION),
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_VERSION, RULES_JVM_EXTERNAL_VERSION),
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

RULES_JS_VERSION = "1.32.2"

RULES_JS_SHA = "77c4ea46c27f96e4aadcc580cd608369208422cf774988594ae8a01df6642c82"

http_archive(
    name = "aspect_rules_js",
    sha256 = RULES_JS_SHA,
    strip_prefix = "rules_js-%s" % RULES_JS_VERSION,
    url = "https://github.com/aspect-build/rules_js/releases/download/v%s/rules_js-v%s.tar.gz" % (RULES_JS_VERSION, RULES_JS_VERSION),
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

RULES_MULTIRUN_VERSION = "0.6.1"

RULES_MULTIRUN_SHA = "9cd384e42b2da00104f0e18f25e66285aa21f64b573c667638a7a213206885ab"

http_archive(
    name = "rules_multirun",
    sha256 = RULES_MULTIRUN_SHA,
    strip_prefix = "rules_multirun-%s" % RULES_MULTIRUN_VERSION,
    url = "https://github.com/keith/rules_multirun/archive/refs/tags/%s.tar.gz" % RULES_MULTIRUN_VERSION,
)

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "aspect_rules_format",
    commit = "a416c6b3744ce9f9f4307a2a9b135328eb009de9",
    remote = "https://github.com/agluszak/bazel-super-formatter.git",
)

load("@aspect_rules_format//format:repositories.bzl", "rules_format_dependencies")

rules_format_dependencies()

load("@aspect_rules_format//format:dependencies.bzl", "parse_dependencies")

parse_dependencies()

load("@aspect_rules_format//format:dependencies.bzl", "rules_format_setup")

rules_format_setup()

# Installs toolchains for running programs under Node, Python, etc.
# Be sure to register your own toolchains before this.
# Most users should do this LAST in their WORKSPACE to avoid getting our versions of
# things like the Go toolchain rather than the one you intended.
load("@aspect_rules_format//format:toolchains.bzl", "format_register_toolchains")

format_register_toolchains()
