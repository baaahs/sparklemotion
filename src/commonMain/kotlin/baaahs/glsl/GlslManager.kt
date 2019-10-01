package baaahs.glsl

import baaahs.shaders.GlslShader

interface GlslManager {
    fun createRenderer(fragShader: String, adjustableValues: List<GlslShader.AdjustableValue>): GlslRenderer
}