package baaahs.glsl

import baaahs.shaders.GlslShader

interface GlslManager {
    val available: Boolean

    fun createRenderer(
        fragShader: String,
        uvTranslator: UvTranslator,
        adjustableValues: List<GlslShader.AdjustableValue>,
        plugins: List<GlslPlugin> = GlslBase.plugins
    ): GlslRenderer
}