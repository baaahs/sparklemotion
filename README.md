# Sparkle Motion

[![CircleCI:main](https://circleci.com/gh/baaahs/sparklemotion.svg?style=svg)](https://circleci.com/gh/baaahs/sparklemotion)

## Documentation
* [Show API](show_api.md)
* [API docs](https://baaahs.github.io/sparklemotion/doc/sparklemotion/)

| | |
| --- | --- |
| <img src="/demo.gif" alt="Simulator" width="400"> <br/> [Simulator](https://baaahs.github.io/sparklemotion) | <img src="/brc-2019.gif" alt="BRC 2019" width="400"> |

## Prerequisites

1. Install [Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
1. Open as a gradle project with [IntelliJ](https://www.jetbrains.com/idea/download/)

## Running from source

### Simulator Mode

In simulator mode, most of Sparkle Motion runs within a web browser.

Run this in a shell window; a browser window will open with the simulator:

    ./gradlew --continuous jsRun

### Production Mode

In production mode, Sparkle Motion runs in a JVM process and talks to real hardware.

To start it, run:

    ./gradlew runPinkyJvm

If you don't have a Brain running locally, do this too:

    ./gradlew runBrainJvm
    
When running in this mode you should be able to access the UI at http://localhost:8004 

### Builds the production (minified) js package

To build the production minified js package run:

```
./gradlew jsBrowserWebpack
```

## CI & Deployment

Continuous build here: https://circleci.com/gh/baaahs/sparklemotion

Passing builds are automatically deployed here: https://baaahs.github.io/sparklemotion
