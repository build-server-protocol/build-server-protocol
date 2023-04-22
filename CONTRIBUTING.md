# Contributing

Firstly, thanks for taking the time to contribute! Here's hopefully some helpful
info to get you started.

## Before Adding a Feature

Before a new feature or endpoint is added to this repo you'll want to make sure
that there is agreement amongst the main parties using BSP. In order for this to
happen it's always preferred that you make sure there is an
[Issue](https://github.com/build-server-protocol/build-server-protocol/issues)
or a
[Discussion](https://github.com/build-server-protocol/build-server-protocol/discussions/landing)
started before you start working on your new feature. With that in mind, we also
understand that sometimes it's easier to just hack something in to show what
you're trying to do. If that's the case, don't hesitate and feel free to create
a draft PR explaining the feature. This will ensure everyone is on the same
page, and provide you with the best contributing experience. It also helps keep
the maintainers on the same page about what's happening.

NOTE: If you're just making a fix or a small improvement on something, it's not
necessary to have an issue or discussion first.

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

## Some notes on maintenance

As BSP is becoming more popular and widely used it's important that all the main
parties involved come to an agreement on what is going to be added and merge
into the protocol. In order to ensure this happen we have a [feature request
template](./.github/ISSUE_TEMPLATE/feature_request.yml) that will have a section
that each party can check making it clear that everyone is on the same page.
When a PR is created, the [CODEOWNERS](./.github/CODEOWNERS) file will ensure
that one person from each party is also assigned to review. This person can
either review or tag someone more relevant on their team. In order to keep a
timely review process, if no one from the team is heard from for 2 weeks, it's
assumed that everyone is on board with the changes.

### Main Parties Involved

These are the main parties involved in the maintenance of this repo and protocol.

- [JetBrains](https://www.jetbrains.com/)
- [Metals Team](https://github.com/scalameta)
- [Scala Center](https://scala.epfl.ch/)
