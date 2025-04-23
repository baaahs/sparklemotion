# Tutorial on Show Design

## Overview

Sparkle Motion is a tool to design and run a light show.

First we define a "scene", an arrangement of physical light fixtures and their hardware controller mappings.

Next we program a "show", a set of visual effects to project on a scene.

Then we build visual effects programmed as "shaders", small GLSL programs that process inputs like coordinates from the scene, colors from upstream shaders, the time, and/or sound analysis info, and generate outputs like a new color for a light, or a translated coordinate for distortion or projection mapping.

Finally we design the UI for all the effects and their "controls" and run the light show in real time!

## Simulator Quick Start

On OS X, start the simulator:

```
brew install openjdk
export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
./gradlew --continuous jsBrowserDevelopmentRun
```

A brand new session starts with no Show or Scene loaded and the Show/Scene menu open. For an existing session:

- Open the Show/Scene menu with:
  - `Esc` keyboard shortcut
  - Click the "☰" icon
- Turn on Design Mode with:
  - `D` keyboard shortcut
  - Toggle the "Design Mode" switch

To reset the simulator in Chrome:

- Open Developer Tools
- Go to Application -> Local Storage -> http://localhost:8000
- Right click and "Clear"
- Reload the browser

## Scene Quick Start

Load a scene from the "Hi-Res" template. Defining a scene's model, light controllers and fixtures is an exercise for another day...

### Load the Hi-Res template

- Open the Show/Scene menu then Scene tab
- "+ New Scene"
- From template "Hi-Res" to see a grid of lights
- "Save" button, enter "tutorial", then "save"

## Show Quick Start

Next we will create an empty show, define a default projection, program our first shaders, then run the show.

### Create an empty show

- Open the Show/Scene menu then Show tab
- "+ New Show"
- "Empty Show"
- "Save" button, enter "tutorial", then "save"

### Define a default projection

- Open the Show/Scene menu then Show tab and toggle "Design Mode"
- "✎" next to the show name
- From Shader Library...
- Select "Flat Projection"
- "Apply" then "Close"
- "Save"

### Program our first shader

Now we can program our first shader, a solid color that we can change with a color picker control. This demonstrates a simple shader that returns a single color for all coordinates for the scene.

We should still be in design / edit mode and see a grid of button placeholders.

- "+" -> New Button
- New Paint Shader
- Shader name: "Solid Color"
- Code

  ```c
  // Solid Color
  uniform vec4 color; // @@ColorPicker

  // @return color
  // @param uvIn uv-coordinate
  vec4 main(vec2 uvIn) {
      return vec4(color.rgb, 1.);
  }
  ```

- "Apply" then "Close"
- "Save"

#### Controls

The above shader needs a `ColorPicker` control. We can add a "vacuity" -- an empty -- to the button grid where all unplaced controls for active shaders will show up. So in this case when we turn on "Solid Color" we will see its color picker.

- Open the Show/Scene menu then Show tab and toggle "Design Mode"
- "+" -> New Vacuity
- "Apply" then "Close"
- Drag the corner to expand it to more of the grid
- "Save"

### Program our second shader

Now we can program our next shader that adds a horizontal and vertical scan line on top of the solid color. This demonstrates a shader that modifies an "upstream" color and uses the `time` component.

- "+" -> New Button
- New Filter Shader
- Shader name: "Scanner"
- Code

  ```c
  // Scanner
  uniform float time;

  // @param fragCoord uv-coordinate
  // @return color
  vec4 upstreamColor(vec2 fragCoord);

  // @return color
  // @param uvIn uv-coordinate
  // @param inColor color
  vec4 main(vec2 uvIn) {
      vec4 c = upstreamColor(uvIn);
      float width = .025;

      float xScan = cos(time) / 2. + .5;
      if (abs(uvIn.x - xScan) < width) {
          c.rgb *= .5;
          c.r = 0.; // 1. - c.r;
      }

      float yScan = sin(time) / 2. + .5;
      if (abs(uvIn.y - yScan) < width) {
          c.rgb *= .5;
          c.g = 0.; //  = 1. - c.g;
      }

      return vec4(c.r, c.g, c.b, 1.);
  }
  ```

- "Apply" then "Close"
- "Save"

## Running a Show Quick Start

To run the show exit "Design Mode" and interact with the buttons and controls.

- Press the "Solid Color" button to turn it on. It has a dashed border when active.
- Use the color picker to change the color. See the sheep in the visualizer change color!
- Press the "Scanner" button to turn it on. See the sheep in the visualizer with scan lines!

## Next Steps

The concepts above will help us build out a full-featured show.

Similar to the "Hi-Res" scene templates there is a "BAAAHS" template. This has a model of the sheep, and additionally has eyes we can control with a shader that returns `MovingHeadParams` for pan, tilt, color and brightness. Currently its recommended to use "Hi-Res" in the simuator for best performance and to occasionally test results on "BAAAHS" before a show.

Similar to the "default projection", we can add more shaders that are always running. The shader library also has "Brightness" and "HSB" (hue, saturation, brightness") shaders which are useful to shift the color or brightness of everything running upstream.

Similar to `ColorPicker`, we can use the `Slider`, `Switch`, and `XyPad` controls in a shader and control it through the UI.

Similar to the "vacuity" you can explicitly place shader controls in the UI grid. Currently you may need to download, modify then upload the show JSON for the best control over the UI.

Similar to the `time` component, we can use `beatInfo`, `beatLink` and `soundAnalysis` components with sound information to make shaders that are sound reactive.

There are lots of shaders in the built-in shader library and on https://shadertoy.com. Have fun!