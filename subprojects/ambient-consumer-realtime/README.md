# ambient-consumer-realtime

(under development)

[![Maven Central Version](https://img.shields.io/maven-central/v/us.aldwin.ambient.consumer/ambient-consumer-realtime)](https://central.sonatype.com/artifact/us.aldwin.ambient.consumer/ambient-consumer-realtime)
[![javadoc](https://javadoc.io/badge2/us.aldwin.ambient.consumer/ambient-consumer-realtime/javadoc.svg)](https://javadoc.io/doc/us.aldwin.ambient.consumer/ambient-consumer-realtime)

https://central.sonatype.com/artifact/us.aldwin.ambient.consumer/ambient-consumer-realtime

Docs (javadoc.io): https://javadoc.io/doc/us.aldwin.ambient.consumer/ambient-consumer-realtime

Docs (GH pages): https://njaldwin.github.io/ambient-consumer/

```maven
<dependency>
    <groupId>us.aldwin.ambient.consumer</groupId>
    <artifactId>ambient-consumer-realtime</artifactId>
    <version>VERSION</version>
</dependency>
```

```gradle
implementation("us.aldwin.ambient.consumer:ambient-consumer-realtime:VERSION")
```

## Building

Use `./gradlew` to build the project.

e.g.

```console
./gradlew --stacktrace clean ktlintFormat build dependencyUpdates ktlintCheck test assemble publish makeDocs
```

## Repository/Publishing Setup

- set up and publish a GPG key
- add GPG key information to GitHub (`Secrets and variables -> Actions`) in `JRELEASER_GPG_PASSPHRASE`, `JRELEASER_GPG_PUBLIC_KEY`, and `JRELEASER_GPG_SECRET_KEY`
- set up a Maven Central account
- perform DNS TXT verification in Maven Central for the group ID's domain
- create a token in Maven Central
- add the Maven credentials to GitHub (`Secrets and variables -> Actions`) in `JRELEASER_MAVENCENTRAL_TOKEN` and `JRELEASER_MAVENCENTRAL_USERNAME`
- set up the `gh-pages` branch for github-pages (`Environments`)

## Publishing

To publish a release, update the version number in `build.gradle.kts`, then create a new version tag pointing to the latest commit in `master`.  Tags must be in [semver](https://semver.org/) format with a `v` prefix (i.e. `vMAJOR.MINOR.PATCH`).

The GitHub action will automatically build and publish the release to Maven Central and GitHub Pages, then create a GitHub Release.

Tags with a prerelease version (e.g. `-alpha.1`) will be marked as prereleases in GitHub and will not be linked from the `/stable` link in the docs.

## Etc

Based on https://github.com/NJAldwin/maven-central-test

Library template: https://github.com/NJAldwin/jvm-library-template
