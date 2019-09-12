package baaahs.glsl

import org.khronos.webgl.WebGLRenderingContext

actual object GlslBase {
    actual val manager: GlslManager by lazy { JsGlslManager() }
}
