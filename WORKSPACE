load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

# Needed until Bazel 7 allows MODULE.bazel to directly call repository rules
load(
    "@aspect_rules_lint//format:repositories.bzl",
    "fetch_java_format",
    "fetch_ktfmt",
)

fetch_java_format()

fetch_ktfmt()

RULES_SCALA_VERSION = "6.5.0"

RULES_SCALA_SHA = "3b00fa0b243b04565abb17d3839a5f4fa6cc2cac571f6db9f83c1982ba1e19e5"

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
rules_scala_toolchain_deps_repositories()

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")

rules_proto_dependencies()

rules_proto_toolchains()

load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")

scala_register_toolchains()

load("@io_bazel_rules_scala//testing:scalatest.bzl", "scalatest_repositories", "scalatest_toolchain")

scalatest_repositories()

scalatest_toolchain()

BAZEL_SONATYPE_TAG = "1.1.1"

BAZEL_SONATYPE_SHA = "6d1bc7da15dae958274df944eb46e9757e14187cda6decd66fc1aeeb1ea21758"

http_archive(
    name = "bazel_sonatype",
    sha256 = BAZEL_SONATYPE_SHA,
    strip_prefix = "bazel-sonatype-{}".format(BAZEL_SONATYPE_TAG),
    url = "https://github.com/JetBrains/bazel-sonatype/archive/v{}.zip".format(BAZEL_SONATYPE_TAG),
)

# --------------------------------------------------------------------------------------------------------------------
load("@bazel_sonatype//:defs.bzl", "sonatype_dependencies")

sonatype_dependencies()
