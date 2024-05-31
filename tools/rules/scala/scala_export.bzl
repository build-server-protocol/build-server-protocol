load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")
load("@bazel_sonatype//:defs.bzl", "sonatype_java_export")
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")
load("@rules_jvm_external//:defs.bzl", "javadoc", "pom_file")
load("@rules_jvm_external//private/rules:maven_project_jar.bzl", "maven_project_jar")
load("@bazel_sonatype//private/rules:sonatype_publish.bzl", "sonatype_publish")

SCALA_RULES_STDLIBS = [
    "@io_bazel_rules_scala_scala_library//:io_bazel_rules_scala_scala_library",
]

SCALA_MAVEN_STDLIBS = [
    "@maven//:org_scala_lang_scala_library",
]

DEFAULT_EXCLUDED_WORKSPACES = [
    "com_google_protobuf",
    "protobuf",
]

# def scala_export(
#         name,
#         maven_coordinates,
#         deploy_env = [],
#         excluded_workspaces = {name: None for name in DEFAULT_EXCLUDED_WORKSPACES},
#         pom_template = None,
#         maven_profile = None,
#         visibility = None,
#         tags = [],
#         testonly = None,
#         deps = [],
#         **kwargs):
#     """Extends `scala_library` to allow maven artifacts to be uploaded. This
#     rule is the Scala version of `java_export`.
#
#     This macro can be used as a drop-in replacement for `scala_library`, but
#     also generates an implicit `name.publish` target that can be run to publish
#     maven artifacts derived from this macro to a maven repository. The publish
#     rule understands the following variables (declared using `--define` when
#     using `bazel run`):
#
#       * `maven_repo`: A URL for the repo to use. May be "https" or "file".
#       * `maven_user`: The user name to use when uploading to the maven repository.
#       * `maven_password`: The password to use when uploading to the maven repository.
#
#     This macro also generates a `name-pom` target that creates the `pom.xml` file
#     associated with the artifacts. The template used is derived from the (optional)
#     `pom_template` argument, and the following substitutions are performed on
#     the template file:
#
#       * `{groupId}`: Replaced with the maven coordinates group ID.
#       * `{artifactId}`: Replaced with the maven coordinates artifact ID.
#       * `{version}`: Replaced by the maven coordinates version.
#       * `{type}`: Replaced by the maven coordintes type, if present (defaults to "jar")
#       * `{dependencies}`: Replaced by a list of maven dependencies directly relied upon
#         by scala_library targets within the artifact.
#
#     The "edges" of the artifact are found by scanning targets that contribute to
#     runtime dependencies for the following tags:
#
#       * `maven_coordinates=group:artifact:type:version`: Specifies a dependency of
#         this artifact.
#       * `maven:compile-only`: Specifies that this dependency should not be listed
#         as a dependency of the artifact being generated.
#
#     To skip generation of the javadoc jar, add the `no-javadocs` tag to the target.
#
#     Generated rules:
#       * `name`: A `scala_library` that other rules can depend upon.
#       * `name-docs`: A javadoc jar file.
#       * `name-pom`: The pom.xml file.
#       * `name.publish`: To be executed by `bazel run` to publish to a maven repo.
#
#     Args:
#       name: A unique name for this target
#       maven_coordinates: The maven coordinates for this target.
#       pom_template: The template to be used for the pom.xml file.
#       deploy_env: A list of labels of java targets to exclude from the generated jar
#       visibility: The visibility of the target
#       kwargs: These are passed to [`scala_library`](https://github.com/bazelbuild/rules_scala/blob/master/docs/scala_library.md),
#         and so may contain any valid parameter for that rule.
#     """
#
#     maven_coordinates_tags = ["maven_coordinates=%s" % maven_coordinates]
#     lib_name = "%s-scala-lib" % name
#
#     javadocopts = kwargs.pop("javadocopts", [])
#
#     # Ensure that the scala stdlib is included in the deploy_env
#     updated_deploy_env = list(deploy_env)
#     for scala_stdlib in SCALA_RULES_STDLIBS:
#         if scala_stdlib not in deploy_env:
#             updated_deploy_env.append(scala_stdlib)
#
#     # Scala stdlib taken from rules_scala doesn't include maven coordinates
#     updated_deps = deps + SCALA_MAVEN_STDLIBS
#
#     # Construct the scala_library we'll export from here.
#     scala_library(
#         name = lib_name,
#         tags = tags + maven_coordinates_tags,
#         testonly = testonly,
#         deps = updated_deps,
#     )
#
#     sonatype_java_export(
#         name = name,
#         maven_coordinates = maven_coordinates,
#         lib_name = lib_name,
#         deploy_env = updated_deploy_env,
#         excluded_workspaces = excluded_workspaces,
#         pom_template = pom_template,
#         maven_profile = maven_profile,
#         visibility = visibility,
#         tags = tags,
#         testonly = testonly,
#         javadocopts = javadocopts,
#     )

def sonatype_scala_export(
        name,
        maven_coordinates,
        maven_profile,
        pom_template = None,
        visibility = None,
        tags = [],
        **kwargs):
    """Extends `java_library` to allow maven artifacts to be uploaded.

    This macro can be used as a drop-in replacement for `java_library`, but
    also generates an implicit `name.publish` target that can be run to publish
    maven artifacts derived from this macro to a maven repository. The publish
    rule understands the following variables (declared using `--define` when
    using `bazel run`):

      * `maven_repo`: A URL for the repo to use. May be "https".
      * `maven_user`: The user name to use when uploading to the maven repository.
      * `maven_password`: The password to use when uploading to the maven repository.

    This macro also generates a `name-pom` target that creates the `pom.xml` file
    associated with the artifacts. The template used is derived from the (optional)
    `pom_template` argument, and the following substitutions are performed on
    the template file:

      * `{groupId}`: Replaced with the maven coordinates group ID.
      * `{artifactId}`: Replaced with the maven coordinates artifact ID.
      * `{version}`: Replaced by the maven coordinates version.
      * `{type}`: Replaced by the maven coordinates type, if present (defaults to "jar")
      * `{dependencies}`: Replaced by a list of maven dependencies directly relied upon
        by java_library targets within the artifact.

    The "edges" of the artifact are found by scanning targets that contribute to
    runtime dependencies for the following tags:

      * `maven_coordinates=group:artifact:type:version`: Specifies a dependency of
        this artifact.
      * `maven:compile_only`: Specifies that this dependency should not be listed
        as a dependency of the artifact being generated.

    Generated rules:
      * `name`: A `java_library` that other rules can depend upon.
      * `name-docs`: A javadoc jar file.
      * `name-pom`: The pom.xml file.
      * `name.publish`: To be executed by `bazel run` to publish to a maven repo.

    Args:
      name: A unique name for this target
      maven_coordinates: The maven coordinates for this target.
      pom_template: The template to be used for the pom.xml file.
      visibility: The visibility of the target
      kwargs: These are passed to [`java_library`](https://docs.bazel.build/versions/master/be/java.html#java_library),
        and so may contain any valid parameter for that rule.
    """

    tags = tags + ["maven_coordinates=%s" % maven_coordinates]
    lib_name = "%s-lib" % name

    # Construct the scala_library we'll export from here.
    scala_library(
        name = lib_name,
        tags = tags,
        **kwargs
    )

    # Merge the jars to create the maven project jar
    maven_project_jar(
        name = "%s-project" % name,
        target = ":%s" % lib_name,
        tags = tags,
    )

    native.filegroup(
        name = "%s-maven-artifact" % name,
        srcs = [
            ":%s-project" % name,
        ],
        output_group = "maven_artifact",
    )

    native.filegroup(
        name = "%s-maven-source" % name,
        srcs = [
            ":%s-project" % name,
        ],
        output_group = "maven_source",
    )

    javadoc(
        name = "%s-docs" % name,
        deps = [
            ":%s-project" % name,
        ],
    )

    pom_file(
        name = "%s-pom" % name,
        target = ":%s" % lib_name,
        pom_template = pom_template,
    )

    sonatype_publish(
        name = "%s.publish" % name,
        coordinates = maven_coordinates,
        maven_profile = maven_profile,
        pom = "%s-pom" % name,
        javadocs = "%s-docs" % name,
        artifact_jar = ":%s-maven-artifact" % name,
        source_jar = ":%s-maven-source" % name,
        visibility = visibility,
    )

    # Finally, alias the primary output
    native.alias(
        name = name,
        actual = ":%s-project" % name,
        visibility = visibility,
    )
