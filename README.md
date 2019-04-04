# Sparkle Motion

[![CircleCI:master](https://circleci.com/gh/baaahs/sparklemotion.svg?style=svg)](https://circleci.com/gh/baaahs/sparklemotion)

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
./gradlew serve
```

## CI & Deployment

Continuous build here: https://circleci.com/gh/baaahs/sparklemotion

Passing builds are automatically deployed here: https://baaahs.github.io/sparklemotion
