package baaahs.glsl

import baaahs.shaders.GlslShader

interface GlslManager {
    val available: Boolean

    fun createRenderer(
        fragShader: String,
        uvTranslator: UvTranslator,
        params: List<GlslShader.Param>,
        plugins: List<GlslPlugin> = GlslBase.plugins
    ): GlslRenderer
}