package baaahs.glsl

import baaahs.shaders.GlslShader

class JsGlslManager : GlslManager {
    override fun createRenderer(program: String, adjustableValues: List<GlslShader.AdjustableValue>) =
        JsGlslRenderer(program, adjustableValues)
}