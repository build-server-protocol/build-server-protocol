# Contributing

## Updating bsp4j `*.xtend` files

Whenever `*.xtend` files are updated you need to manually re-generate Java
source files with the `sbt bsp4j/xtend` task.  For example, while iterating on
changes in bsp4j you can use

```
sbt
> ~; bsp4j/xtend ; bsp4j/compile
```

