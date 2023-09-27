---
id: sbt
title: sbt Extension
sidebar_label: sbt
---

The following section contains sbt-specific extensions to the build server
protocol. This extension allows BSP clients to provide language support for sbt
build files.

## BSP version

`2.1.0`

## BuildTargetData kinds

### SbtBuildTarget

This structure is embedded in
the `data?: BuildTargetData` field, when
the `dataKind` field contains `"sbt"`.

#### SbtBuildTarget

`SbtBuildTarget` is a basic data structure that contains sbt-specific metadata
for providing editor support for sbt build files.

For example, say we have a project in `/foo/bar` defining projects `A` and `B`
and two meta builds `M1` (defined in `/foo/bar/project`) and `M2` (defined in
`/foo/bar/project/project`).

The sbt build target for `M1` will have `A` and `B` as the defined targets and
`M2` as the parent. Similarly, the sbt build target for `M2` will have `M1` as
the defined target and no parent.

Clients can use this information to reconstruct the tree of sbt meta builds. The
`parent` information can be defined from `children` but it's provided by the
server to simplify the data processing on the client side.

```ts
export interface SbtBuildTarget {
  sbtVersion: string;

  autoImports: string[];

  scalaBuildTarget: ScalaBuildTarget;

  parent?: BuildTargetIdentifier;

  children: BuildTargetIdentifier[];
}
```
