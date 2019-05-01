# Sparkle Motion Show API

## Terminology

| Term | Definition |
| --- | --- |
| **Brain** | A tiny server running on custom hardware which is physically connected to a surface's LEDs. A Brain runs shaders, taking direction from Pinky. |
| **Gadget** | A source of external data, which could be directly controlled by a user (such as a , or collected from sensors. |
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

## Writing a Show

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

Shows may specify a shader (or an arrangement of shaders) for each surface.

```kotlin
val shaders = model.allSurfaces.map { surface -> SolidColorShader(surface).also { showRunner.setShader(surface, it) } } 

fun nextFrame() {
  shaders.forEach { shader -> shader.buffer.color = Color.ORANGE }
}
```


#### Shader Types

## Geometry

TBD

## Writing a Gadget

## Writing a Shader

