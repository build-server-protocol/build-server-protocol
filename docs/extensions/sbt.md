---
id: sbt
title: sbt Extension
sidebar_label: sbt
---

The following section contains sbt-specific extensions to the build server
protocol. This extension allows BSP clients to provide language support for sbt
build files.

`SbtBuildTarget` is a basic data structure that contains sbt-specific metadata
for providing editor support for sbt build files. This metadata is embedded in
the `data: Option[Json]` field of the `BuildTarget` definition when the
`dataKind` field contains "sbt".

```scala
trait SbtBuildTarget {
  /** The sbt version. Useful to support version-dependent syntax. */
  def sbtVersion: String

  /** A sequence of Scala imports that are automatically imported in the sbt build files. */
  def autoImports: List[String]

  /** The Scala build target describing the scala
   * version and scala jars used by this sbt version. */
  def scalaBuildTarget: ScalaBuildTarget

  /** An optional parent if the target has an sbt meta project. */
  def parent: Option[BuildTargetIdentifier]

  /** The inverse of parent, list of targets that have this build target
    * defined as their parent. It can contain normal project targets or
    * sbt build targets if this target represents an sbt meta-meta build. */
  def children: List[BuildTargetIdentifier]
}
```

For example, say we have a project in `/foo/bar` defining projects `A` and `B`
and two meta builds `M1` (defined in `/foo/bar/project`) and `M2` (defined in
`/foo/bar/project/project`).

The sbt build target for `M1` will have `A` and `B` as the defined targets and
`M2` as the parent. Similarly, the sbt build target for `M2` will have `M1` as
the defined target and no parent.

Clients can use this information to reconstruct the tree of sbt meta builds. The
`parent` information can be defined from `children` but it's provided by the
server to simplify the data processing on the client side.
