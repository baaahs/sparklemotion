# Sparkle Motion

[![CircleCI:master](https://circleci.com/gh/baaahs/sparklemotion.svg?style=svg)](https://circleci.com/gh/baaahs/sparklemotion)

## Documentation
* [Show API](show_api.md)
* [API docs](https://baaahs.github.io/sparklemotion/doc/sparklemotion/)

![gif demo](/demo.gif)
![BRC 2019](/brc-2019.gif)

## Prerequisites

1. Install [Java](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
1. Install [Gradle](https://gradle.org/install/):

   `brew install gradle`
1. Install [Node.js](https://nodejs.org/en/download/):

   `brew install node`

## Running from source

* Open as a gradle project with [IntelliJ](https://www.jetbrains.com/idea/download/)
* Navigate to the top level directory and install JS dependencies with this command. Note: you MUST have [Node.js](https://nodejs.org/en/download/) installed!
  - `npm install`
* Transpile the JS by using the command:
  - `npm run build`
* Open `src/jsMain/resources/index.html` using "Open in Browser -> Chrome" from IntelliJ context menu

## Local build

Run this in one shell window:

```sh
./gradlew -t jsJar
```

Run this in a second shell window and go to http://localhost:8001/index.html :

```sh
npm run start:dev
```

Not that at least one of the above steps seems to perform a somewhat important build process
related to making the UI available. Presuming you have run jsJar and start:dev at least once
and poked at the resulting simulator UI, you should be able to do the following to run Pinky
in *standalone JVM mode*. This is required if you want to talk to actual Brain hardware.

    ./gradlew runPinkyJvm
    
When running in this mode you should be able to access the UI at http://localhost:8004 

Note that this is different from the URL above (it doesn't have the `index.html` part). If you
load the index page you'll be loading a full local simulation into your browser and won't be talking
to the Pinky instance which is actually (presumably) talking to the Brains.

## CI & Deployment

Continuous build here: https://circleci.com/gh/baaahs/sparklemotion

Passing builds are automatically deployed here: https://baaahs.github.io/sparklemotion
