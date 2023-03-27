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

## Building the docs

The docs for this site are build with [mdoc](https://scalameta.org/mdoc/) to
typecheck Scala snippets and also to generate a static site via
[Docusaurus](https://docusaurus.io/).

To build the site locally you'll want to follow the below steps:

1. `sbt mdoc` to typecheck and copy the `/docs` to where they need to be
2. `cd website` to go into the website directory where the site is managed
3. `yarn install` to ensure everything is installed
4. `yarn start` to build and start the server

**NOTE**: that if you're attempting to use the built-in local search step 4 will
need to be replaced with the following 2 steps:

1. `yarn build` to fully build the site
1. `yarn serve` to serve the site
