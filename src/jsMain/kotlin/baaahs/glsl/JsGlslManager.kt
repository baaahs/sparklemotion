package baaahs.glsl

import baaahs.shaders.GlslShader
import de.fabmax.kool.createContext

class JsGlslManager : GlslManager {
    override fun createRenderer(program: String, adjustableValues: List<GlslShader.AdjustableValue>) =
        GlslRenderer(createContext(), program, adjustableValues)
}