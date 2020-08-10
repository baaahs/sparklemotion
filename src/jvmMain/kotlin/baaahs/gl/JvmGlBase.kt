package baaahs.gl

actual object GlBase {
    actual val manager: GlManager by lazy { LwjglGlManager() }
}