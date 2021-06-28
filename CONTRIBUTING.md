# Contributing

## Updating protocol
To change the protocol (e.g. by adding a new field to some request parameters) one has to:
1. Update the appropriate`.xtend` file in the `bsp4j` module.
2. Manually re-generate java sources via  `sbt bsp4j/xtend`
3. Update the corresponding classes in `bsp4s` module

While working on changes in `bsp4j` you can use
```
sbt
> ~; bsp4j/xtend ; bsp4j/compile
```

## Formatting

To ensure the Scala sources are formatted correctly you can run:

```sh
sbt scalaFormat
```

