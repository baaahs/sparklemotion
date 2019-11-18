package baaahs.glsl

interface GlslPlugin {
    fun forRenderer(renderer: GlslRenderer): RendererPlugin

    interface RendererPlugin {
        val glslPreamble: String

        fun afterCompile(program: Program)

        fun beforeRender()
    }
}