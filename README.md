# Sparkle Motion

[![CircleCI:main](https://circleci.com/gh/baaahs/sparklemotion.svg?style=svg)](https://circleci.com/gh/baaahs/sparklemotion)

[Discord](https://discord.gg/cxW2XtpqjS) | [Demo](https://baaahs.github.io/sparklemotion)

Sparkle Motion is the system used to control the lights on [BAAAHS](http://baaahs.org), but it's
designed so it could be applied to pretty much any lighting project. It includes a browser-based
light show designer and performance interface, 3D light mapping, IP-based control protocol, and
custom LED control hardware and firmware. We designed it all pretty much from scratch, because
we're nerds. We hope you'll have some fun with it and maybe find it useful.

| ![SparkleMotionDemo-20220308](https://user-images.githubusercontent.com/40298/157351650-5b3338b7-757a-4e76-bfe2-743a06bb2891.gif) | <img src="/brc-2019.gif" alt="BAAAHS at BRC 2019" width="400"> |
| --- | --- |
| The Sparkle Motion [Simulator](https://baaahs.github.io/sparklemotion) | BAAAHS at Black Rock City, 2019 |


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
> Sparkle Motion can control LED strips using
> [sACN](https://artisticlicenceintegration.com/technology-brief/technology-resource/sacn-and-art-net/)
> or our custom IP protocol, and Sharpy-style moving heads
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

## Scene Configuration

Sparkle Motion shows may be designed with a specific model and display fixtures in mind, but most shows can be applied to any model and fixtures.

To facilitate this, Sparkle Motion separates configuration of scene elements (the physical model and fixtures) from visuals and the performance interface.

| Term | Definition |
| --- | --- |
| **Driver** | A pluggable software component that can talk to specific types of display controller hardware, e.g. Brains, WLED, or DMX USB dongles. |
| **Controller** | A physical component directly connected to display hardware, e.g. a Sparkle Motion Brain, a WLED controller, or a DMX USB dongle. One or more fixtures may be associated with a controller. |
| **Fixture** | A physical display device, e.g. a moving head, a pixel array surface (a.k.a. panel), an LED bar, etc. |
| **Scene/Stage?** | The collection of fixtures under control of Sparkle Motion, which may be identified as model entities, or anonymous. |
| **Geometry** | The physical shape of a pixel array fixture. |
| **Model** | A 3-dimensional model to which fixtures may be mapped. Models may be composed of OBJ files and explicitly placed entities. |
| **Entity** | An object within the model, e.g. a sheep panel or eye. |

Note that in some cases a single controller may control multiple fixtures. Commonly, a physical DMX USB dongle may have
multiple moving heads attached. It's possible (but less common) for a single brain or WLED controller to be attached to
multiple physical lighting fixtures.

Also, it's possible for a DMX controller to manage multiple DMX universes, e.g. in the case of a WLED controller
managing more than 170 pixels.

ERD:

Driver <->* Controller <->* Fixture <->? Entity *<--> Model

A fixture may be *identified* or *anomnymous*.

Identified fixtures are associated with an entity in the model, and therefore have a known position and geometry.

Anonymous fixtures are mostly for stuff like bikers-by. They are randomly placed within the model. Pixel arrays are assumed to be linear.

## Mapper

Sparkle Morton's mapper lets you detect arbitrarily-placed lights on a 2D or 3D model using just a camera
connected to a laptop.

(more TK)

## Hardware

(more TK)

[More here](brain/sw/README.md).

## Simulator

The entire system can be run within a web browser in simulation mode ([here!](https://baaahs.github.io/sparklemotion)). Every component is modeled in software
so you can see how it will behave in the real world.


-->

## Very Old Documentation
* [Show API](show_api.md) (outdated)
* [API docs](https://baaahs.github.io/sparklemotion/doc/sparklemotion/) (outdated)

## Prerequisites

1. Install [Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
    - If using an arm64 mac, DMX device support for the native build of sparklemotion requires an x86 jdk which can be
      downloaded [here](https://jdk.java.net/archive/).
    - Make sure to pick a JDK < version 20 as [gradle does not currently
      support
it](https://youtrack.jetbrains.com/issue/KT-57669/Add-Java-20-to-JvmTarget).
1. Open as a gradle project with [IntelliJ](https://www.jetbrains.com/idea/download/)

## Running from source

### Simulator Mode

In simulator mode, most of Sparkle Motion runs within a web browser.

Run this in a shell window; a browser window will open with the simulator:

    ./gradlew --continuous jsRun

### Production Mode

In production mode, Sparkle Motion runs in a JVM process and talks to real hardware.

To start it, run:

    ./gradlew run

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
