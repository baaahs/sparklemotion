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


Shader stages:

| _edit_                       | `MutableShader` |
| `mutableShader.build()`      | `Shader` |
| `glslAnalyzer.parse()`       | `GlslCode` |
| `glslAnalyzer.detectDialect` | `ShaderDialect` |
| `glslAnalyzer.validate()`    | `ShaderAnalyzsis` |
| `glslAnalyzer.openShader()`  | `OpenShader` |
| `autoWirer.autoWire()`       | `UnresolvedPatch` |
| `unresolvedPatch.confirm()`  | `MutablePatch`    |
| `ShowOpener()`               | `OpenPatch`       |
| `PatchResolver`              | `RenderPlan`      |
