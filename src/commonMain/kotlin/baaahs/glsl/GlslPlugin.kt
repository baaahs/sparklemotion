package baaahs.glsl

interface GlslPlugin {
    fun forRenderer(renderer: GlslRenderer): RendererPlugin

    interface RendererPlugin {
        fun beforeRender()
    }
}