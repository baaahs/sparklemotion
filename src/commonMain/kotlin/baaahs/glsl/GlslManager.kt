package baaahs.glsl

import baaahs.shaders.GlslShader

interface GlslManager {
    fun createRenderer(program: String, adjustableValues: List<GlslShader.AdjustableValue>): GlslRenderer
}