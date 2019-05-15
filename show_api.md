# Sparkle Motion Show API

Sparkle Motion is an open source lighting design and collaborative performance system, created for BAAAHS but designed
to be useful for many other sorts of installation. The general architecture is a distributed processing system, where
numerous low-cost rendering engines (Brains) are coordinated by a central entity (Pinky).

This document lays out the various components and APIs of Sparkle Motion.

## Terminology

| Term | Definition |
| --- | --- |
| **Brain** | A tiny server running on custom hardware which is physically connected to a surface's LEDs. A Brain runs shaders, taking direction from Pinky. |
| **Gadget** | A source of external data, which could be directly controlled by a user (such as a color picker or sliders), or data collected from sensors (such as audio spectral analysis). |
| **Mapper** | A program running on as-yet undefined hardware which uses computer vision techniques to map Brains to surfaces. |
| **Model** | A 3D model of surfaces and moving heads. For BAAAHS, it includes panels and other surfaces, plus the eyes. Models may have installation-specific terminology of their own. |
| **Moving Head** | A programmable moving spotlight (sometimes referred to as a Sharpy). |
| **Pinky** | A single Linux-ish server which runs shows and coordinates Brains. | 
| **Shader** | A program running on each Brain which sets the color of its LEDs based on data from a show. Multiple shaders can be combined to control the color of each LED. |
| **Simulator** | A full system simulator for Sparkle Motion that runs in a normal web browser. | 
| **Show** | A program running on Pinky which configures shaders for surfaces, takes input from gadgets, and sends data to shaders. | 
| **Show Runner** | A component of Pinky which hosts shows, providing access to gadgets and shaders. |
| **Surface** | A (roughly) flat surface illuminated with controllable LEDs (such as a panel). LEDs on a surface are directly connected to a Brain. For BAAAHS, surfaces include panels plus the face, ears, hooves, and tail. |
| **Visualizer** | Component of Sparkle Mothin which creates 3D-rendered previews of the model as illuminated by a show. |
| **Web UI** | A browser-based interface for selecting shows, presenting gadgets, and otherwise controlling the system. |

## Environment

Sparkle Motion is mostly written in Kotlin, with smaller sections written in C/C++ and JavaScript. Most development can
occur inside the simulator. To set up your development environment, see
[directions here](https://github.com/baaahs/sparklemotion).

Longer term, our intent is to make it possible to package custom shows, gadgets, and shaders together (e.g. in a zip
archive) and load them into a running Sparkle Motion instance. Currently they need to be added directly to the Sparkle
Motion codebase.

## Writing a Show

Shows take input from gadgets and use it to configure shaders, creating pretty stuff on surfaces.

At the code level, shows acquire inputs and shader outputs during an initialization phase, and implement a single method
`nextFrame()`, in which the show performs whatever calculations are needed to update the shaders.

Shows are permitted to retain state between frames.

### Acquiring Gadgets

Shows may request input from any of several types of gadgets. Shows may optionally provide a description of the
gadget's purpose.

```kotlin
val primaryColorBuf = showRunner.getGadgetBuffer(SingleColorGadget("Primary Color"))

fun nextFrame() {
  println("Primary color is ${primaryColorBuf.color}.")
}
```

#### Gadget Types
- Single color
- Palette of colors (of arbitrary size, but probably 6 or fewer)
- Slider (float, 0 to 1 continuous)
- Momentary switch (boolean)
- Toggle switch (boolean)
- X/Y Coordinates (two floats from 0 to 1)
  - 2D image of party/business side overlaid that user can pinpoint a simple (x, y) coordinate of a point in 2D space
  - joystick
- Stepper knob
- bpm/phrasing
- spectral analysis
- geocompass / accelerometer


### Acquiring Shaders

Shows may specify a shader (or an arrangement of shaders) for each surface. Every type of shader has a corresponding
`ShaderBuffer` type, used to communicate from shows to shaders.

```kotlin
val shaderBuffers = model.allSurfaces.map { surface -> showRunner.getShaderBuffer(surface, SolidColorShader()) } 

fun nextFrame() {
  shaderBuffers.forEach { shader -> shader.color = Color.ORANGE }
}
```

#### Compositing Shaders
A special type of shader can be used to combine the output of two other shaders:

```kotlin
val solidShader = SolidShader()
val sparkleShader = SparkleShader()
val compositorShader = CompositorShader(solidShader, sparkleShader)

val shaderBuffers = sheepModel.allSurfaces.map { surface ->
    val solidShaderBuffer = showRunner.getShaderBuffer(surface, solidShader)
    val sparkleShaderBuffer = showRunner.getShaderBuffer(surface, sparkleShader)
    val compositorShaderBuffer =
        showRunner.getCompositorBuffer(surface, solidShaderBuffer, sparkleShaderBuffer, CompositingMode.ADD)

    Shaders(solidShaderBuffer, sparkleShaderBuffer, compositorShaderBuffer)
}
```

Compositing shaders can be arranged recursively, allowing for an arbitrary number of shaders to contribute to a
rendering pass.

#### Shader Types

## Geometry

TBD

## Writing a Gadget

Gadgets represent an external data source, often user-controllable.

In code, a gadget extends the `Gadget` class, adding data values and implementing serialization/deserialization methods
for transferring data between UI and Pinky instances.

## Writing a Shader

A shader takes configuration data from a show and uses it to render colors to the array of pixels it controls.

In code, a shader comprises three classes: the `Shader` itself (representing its platonic ideal), and a corresponding
`Shader.Buffer` (holding data transferred from the show to the shader for every frame) and a `Shader.Renderer` (which
performs the actual work of rendering pixels on a Brain).

When a show requests a shader, Pinky creates an appropriate buffer and renderer instance on the associated Brain.

For every frame, the contents of the buffer is transferred from Pinky to the Brain, and `Renderer.draw()` is invoked.
The renderer may perform more expensive calculations during an initialization phase to optimize the cost of rendering
each frame.

Shaders should be stateless between calls to `draw()`.

### Tweening

Shaders may support inter-frame tweening by asking to be called back within a certain time window. If a new frame's
data hasn't arrived from Pinky within that window, `Renderer.draw()` will be called again with the same buffer data
and optional tween context data:

```kotlin
fun draw(buffer: Buffer, tweenContext: TweenContext? = null) {
  // perform rendering here...
  
  requestTween(10, MyTweenContext("whatever data"))
}
```