package baaahs.glsl

actual object GlslBase {
    actual val plugins: MutableList<GlslPlugin> = mutableListOf()
    actual val manager: GlslManager by lazy { LwjglGlslManager() }
}