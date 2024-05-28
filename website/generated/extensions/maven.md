---
id: maven
title: Maven Extension
sidebar_label: Maven
---

The following section contains Maven-specific extensions to the build server
protocol.

## BSP version

`2.2.0`

## DependencyModuleData kinds

### MavenDependencyModule

This structure is embedded in
the `data?: DependencyModuleData` field, when
the `dataKind` field contains `"maven"`.

#### MavenDependencyModule

`MavenDependencyModule` is a basic data structure that contains maven-like
metadata. This metadata is embedded in the `data: Option[Json]` field of the `DependencyModule` definition, when the `dataKind` field contains "maven".

```ts
export interface MavenDependencyModule {
  organization: string;

  name: string;

  version: string;

  /** List of module's artifacts with different classifiers.
   * For example: [
   *   {uri = "../scala-library-2.13.5.jar"},
   *   {uri = "../scala-library-2.13.5-sources.jar", classifier = "sources"}
   * ] */
  artifacts: MavenDependencyModuleArtifact[];

  scope?: string;
}
```

#### MavenDependencyModuleArtifact

```ts
export interface MavenDependencyModuleArtifact {
  /** Path to jar */
  uri: URI;

  /** Empty or `sources`|`docs` */
  classifier?: string;
}
```
