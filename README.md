# Sparkle Motion

[![CircleCI:main](https://circleci.com/gh/baaahs/sparklemotion.svg?style=svg)](https://circleci.com/gh/baaahs/sparklemotion)

Sparkle Motion is the system used to control the lights on [BAAAHS](http://baaahs.org), but it's
designed so it could be applied to pretty much any lighting project. It includes a browser-based
light show designer and performance interface, 3D light mapping, IP-based control protocol, and
custom LED control hardware and firmware. We designed it all pretty much from scratch, because
we're nerds. We hope you'll have some fun with it and maybe find it useful.

| | |
| --- | --- |
| <img src="/demo.gif" alt="Simulator image" width="400"> <br/> The Sparkle Motion [Simulator](https://baaahs.github.io/sparklemotion) | <img src="/brc-2019.gif" alt="BAAAHS at BRC 2019" width="400"> <br/> BAAAHS at Black Rock City, 2019|


> **tl;dr:**
>
> Shows are built out of small scripts called shaders, which are written in
> [GLSL](https://www.khronos.org/opengl/wiki/Core_Language_(GLSL)). Lots of [awesome](http://glslsandbox.com/)
> [free](https://www.shadertoy.com/) shaders and [dev](https://github.com/radixzz/awesome-glsl)
> [tools](https://shaderfrog.com/) already exist on the internet. In the show designer, you can create or
> import shaders, and attach them to buttons or sliders in a customizable performance UI. Shaders can be
> combined in interesting ways to create new effects. You can make shows reactive to the environment by
> connecting external sensor data—like a beat detector, midi controller, sound spectral analysis, or a
> webcam—to shader variables.
>
> Sparkle Motion can currently control LED strips using a custom IP protocol, and Sharpy-style moving heads
> over DMX, but it could be extended to control pretty much any kind of device. Lights can be mapped to a
> 3D model using computer vision. Individual lighting fixtures, or groups of fixtures, can be controlled
> separately. All types of lights are controlled using the same language and idioms.
>
> On the hardware side, Sparkle Motion includes specs for an ESP32-based controller which you could build
> (or [buy from us](mailto:info@baaahs.org)!) managing WS2812-family LEDs, which is controlled over
> Ethernet or WiFi.

----

## Show Designer

A _show_ is a collection of shaders, attached to UI elements, which can be arbitrarily combined and made reactive to the
environment using sensors,

(more TK)

## Mapper

Sparkle Morton's mapper lets you detect arbitrarily-placed lights on a 2D or 3D model using just a camera
connected to a laptop.

(more TK)

## Hardware

(more TK)

[More here](brain/sw/README.md).

## Simulator

The entire system can be run within a web browser in simulation mode. Every component is modeled in software
so you can see how it will behave in the real world.


-->

## Old Documentation
* [Show API](show_api.md) (outdated)
* [API docs](https://baaahs.github.io/sparklemotion/doc/sparklemotion/) (outdated)

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
