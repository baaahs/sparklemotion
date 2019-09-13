package baaahs.glsl

import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.WebGLRenderingContext

private fun getJsGlContext_CHEAT(): WebGLRenderingContext {
    return js("de.fabmax.kool.JsImpl.gl")
}

actual fun glFinish() {
    getJsGlContext_CHEAT().finish()
}

actual fun glDrawArrays(mode: Int, first: Int, count: Int) =
    getJsGlContext_CHEAT().drawArrays(mode, first, count)

actual fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: Any?) {
    getJsGlContext_CHEAT().readPixels(x, y, width, height, format, type, pixels as ArrayBufferView)
}

actual fun glTexImage2D(
    target: Int,
    level: Int,
    internalformat: Int,
    width: Int,
    height: Int,
    border: Int,
    format: Int,
    type: Int,
    pixels: Any
) =
    getJsGlContext_CHEAT().texImage2D(
        target,
        level,
        internalformat,
        width,
        height,
        border,
        format,
        type,
//        (pixels as Uint8BufferImpl?)?.buffer
        pixels as ArrayBufferView
    )