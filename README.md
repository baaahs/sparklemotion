# Sparkle Motion

[![CircleCI:master](https://circleci.com/gh/baaahs/sparklemotion.svg?style=svg)](https://circleci.com/gh/baaahs/sparklemotion)

## Documentation
* [Show API](show_api.md)
* [API docs](https://baaahs.github.io/sparklemotion/doc/sparklemotion/)

| | |
| --- | --- |
| <img src="/demo.gif" alt="Simulator" width="400"> <br/> [Simulator](https://baaahs.github.io/sparklemotion) | <img src="/brc-2019.gif" alt="BRC 2019" width="400"> |

## Prerequisites

1. Install [Java](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
1. Install [Node.js](https://nodejs.org/en/download/):

   `brew install node`

## Running from source

* Open as a gradle project with [IntelliJ](https://www.jetbrains.com/idea/download/)
* Navigate to the top level directory and install JS dependencies with this command. Note: you MUST have [Node.js](https://nodejs.org/en/download/) installed!
  - `npm install`
* Transpile the JS by using the command:
  - `npm run build`
* Open `src/jsMain/resources/index.html` using "Open in Browser -> Chrome" from IntelliJ context menu

### Simulator Mode

In simulator mode, most of Sparkle Motion runs within a web browser.

Run this in one shell window:

    ./gradlew -t jsJar

Run this in a second shell window and go to http://localhost:8000/:

    npm run start:dev

A few bits of Sparkle Motion can't run inside a browser, specifically beat detection and sound analysis.
To enable those, run this:

    ./gradlew runBridgeJvm

### Production Mode

In production mode, Sparkle Motion runs in a JVM process and talks to real hardware.

To start it, run:

    ./gradlew runPinkyJvm

If you don't have a Brain running locally, do this too:

    ./gradlew runBrainJvm
    
When running in this mode you should be able to access the UI at http://localhost:8004 

## CI & Deployment

Continuous build here: https://circleci.com/gh/baaahs/sparklemotion

Passing builds are automatically deployed here: https://baaahs.github.io/sparklemotion
