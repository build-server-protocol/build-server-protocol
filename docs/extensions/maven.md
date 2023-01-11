---
id: maven
title: Maven Extension
sidebar_label: Maven
---

### Maven Dependency Module

`MavenDependencyModule` is a basic data structure that contains maven-like
metadata. This metadata is embedded in the `data: Option[Json]` field of the `DependencyModule` definition, when the `dataKind` field contains "maven".

```ts
export interface MavenDependencyModule {
  organization: String;
  name: String;
  version: String;
  scope?: String;

  /** List of module's artifacts with different classifiers.
     For example: [
       {uri = "../scala-library-2.13.5.jar"},
       {uri = "../scala-library-2.13.5-sources.jar", classifier = "sources"}
     ]*/
  artifacts: MavenDependencyModuleArtifact[];
}

export interface MavenDependencyModuleArtifact {
  /** Path to jar*/
  uri: Uri;

  /** Empty or `sources`|`docs` */
  classifier?: String;
}
```
