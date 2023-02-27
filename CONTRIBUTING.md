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
## Releasing

This repo is setup to use
[sbt-ci-release](https://github.com/sbt/sbt-ci-release) for easy releasing. To
publish a new release, you can go to the [releases
page](https://github.com/build-server-protocol/build-server-protocol/releases)
to tag a new release and auto-generate the Release Notes. The release notes are
important as this is useful for downstream users to be able to click the
"Release Notes" section of a Steward PR and be brought to this release.

