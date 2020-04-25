package baaahs.glsl

import baaahs.Logger
import com.danielgergely.kgl.*

expect object GlslBase {
    val plugins: MutableList<GlslPlugin>
    val manager: GlslManager
}

inline fun <T> Kgl.check(fn: () -> T): T {
    val result = fn.invoke()
    checkForGlError()
    return result
}

fun Kgl.checkForGlError() {
    while (true) {
        val error = getError()
        val code = when (error) {
            GL_INVALID_ENUM -> "GL_INVALID_ENUM"
            GL_INVALID_VALUE -> "GL_INVALID_VALUE"
            GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
            GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
            GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
//            GL_CONTEXT_LOST_WEBGL -> "GL_CONTEXT_LOST_WEBGL"
            GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
            else -> "unknown error $error"
        }
        if (error != 0) {
            val logger = Logger("GlslBase")
            logger.error { "OpenGL Error: $code" }
            throw RuntimeException("OpenGL Error: $code")
        } else return
    }
}
