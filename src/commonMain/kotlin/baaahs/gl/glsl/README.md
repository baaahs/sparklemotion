## GLSL handling

### Patches, providers, and ports, oh my!

Shaders declare uniforms.

Shaders (Shader Fragments?)
- type = { `color` | `transform` | `filter` | `function` }
- input ports ~= uniforms
- output ports ~= return values

Controls/Inputs/Generators
- output ports ~= results

Patches
- nodes ~= Shaders/Controls/Inputs/Generators
- links ~= acyclic directed graph of (node output port ref -> node output port ref)

Scenes
- patches
- event bindings (cues?)
  - UI buttons
  - keyboard
  - MIDI events

Shows
- all of above

Libraries
- collection of shaders


Input Ports
- content type = { `color` | ? }

Port Refs
-

Shader -> Uniform -> InputPort
  -> PortRef