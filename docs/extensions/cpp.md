---
id: cpp
title: C++ Extension
sidebar_label: cpp
---

The following section contains C++-specific extensions to the build server
protocol.

## C++ Build Target

`CppBuildTarget` is a basic data structure that contains c++-specific
metadata, specifically compiler reference. This metadata is embedded in
the `data: Option[Json]` field of the `BuildTarget` definition, when
the `dataKind` field contains "cpp".

```ts
export interface CppBuildTarget {
  /** The c++ version this target is supposed to use. 
    * For example: C++11 */
  version: String;

  /** The type of compiler this target is supposed to use. 
    * For example: gcc */
  compiler: String;

  /** Uri representating path to the c compiler. 
    * For example: file:///usr/bin/gcc */
  cCompiler?: Uri;

  /** Uri representating path to the c++ compiler. 
    * For example: file:///usr/bin/g++ */
  cppCompiler?: Uri;

  /** Attributes added in the given order to COPTS
    * before compiling the target. 
    * For example: -Iexternal/gtest/include */
  copts: String[];
  
  /** Attributes prepended with -D
    * and added to the compile command line
    * For example: gcc */
  defines: String[];
  
  /** Attributes added to the linker command
    * For example: -pthread */
  linkopts: String[];
  
  /** Create a shared library.
    * The presence of this flag means that linking occurs with the -shared flag 
    * For example: gcc */
  linkshared?: Boolean;
  
}
```
