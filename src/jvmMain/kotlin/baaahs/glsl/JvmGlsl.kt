package baaahs.glsl

actual object GlslBase {
    actual val manager: GlslManager by lazy { LwjglGlslManager() }
}