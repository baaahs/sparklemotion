# Tutorial on Show Design

## Overview

Sparkle Motion is a tool to design and run a light show.

First, you define a "scene" -- an arrangement of physical light fixtures and their controller mappings.

Next, you design a "show" -- a set of visual effects to project on a scene.

Visual effects programmed as "shaders" -- small GLSL programs that process inputs like xy coordinates from the scene, colors from another shader, and audio metadata from the sound analysis plugin, and generate outputs like another xy coordinate for distortion or projection mapping, or a color for a light.

Finally, you use the designer mode to design a UI for all the "controls" and run the light show in real time.

## Simulator Quick Start

On OS X, start the simulator:

```
brew install openjdk
export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
./gradlew --continuous jsRun
```

A brand new session starts with no Show or Scene loaded, and the Show/Scene menu open. For an existing session click the "☰" icon to open the Show/Scene menu.

## Scene Quick Start

Load a scene from the BAAAHS template. Defining a scene's model, light controllers and fixtures is an exercise for another day...

### Load the BAAAHS template

- "☰" to open the Show/Scene menu then Scene tab
- "+ New Scene"
- From template "BAAAHS" to see the sheep model 🐑
- "Save" button, enter "my-baaahs", then "save"

## Show Quick Start

Next we'll create an empty show, define a default projection, then program our first effect.

### Create an empty show

- "☰" to open the Show/Scene menu then Show tab
- "+ New Show"
- "Empty Show"
- "Save" button, enter "my-baaahs", then "save"

### Define a default projection

- "☰" to open the Show/Scene menu then Show tab
- Toggle "Design Mode"
- "✎" next to the show name
- New Projection Shader
- Shader name: "Flat Projection"
- Use the default code

  ```c
  struct ModelInfo {
      vec3 center;
      vec3 extents;
  };
  uniform ModelInfo modelInfo;

  // @return uv-coordinate
  // @param pixelLocation xyz-coordinate
  vec2 main(vec3 pixelLocation) {
      vec3 start = modelInfo.center - modelInfo.extents / 2.;
      vec3 rel = (pixelLocation - start) / modelInfo.extents;
      return rel.xy;
  }
  ```
- "Apply" then "Close"
- "Save"

### Program our first effect

You should still be in design / edit mode and see a grid of button placeholders.

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

The above effect needs a `ColorPicker`. While "Design Mode" has some support for seeing unplaced controls and placing them in the button grid, at present it is easiest to do this by editing the show template directly.

- "☰" to open the Show/Scene menu then Show tab
- "Download Show"
- Open "Untitled.sparkle"
- Add to `layouts.formats.default.tabs[0].items[]`

  ```json
  {
      "controlId": "color",
      "column": 0,
      "row": 1,
      "width": 2,
      "height": 2
  }
  ```

- "Upload Show"
- Drop "Untitled.sparkle"
- "Save" button, enter "my-baaahs", then "save"

