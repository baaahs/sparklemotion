package baaahs.glsl

import baaahs.Logger
import com.danielgergely.kgl.GL_COMPILE_STATUS
import com.danielgergely.kgl.GL_TRUE
import com.danielgergely.kgl.Shader

class CompiledShader(
    private val gl: GlslContext,
    type: Int,
    private val source: String
) {
    val shaderId: Shader = gl.runInContext {
        gl.check { createShader(type) ?: throw IllegalStateException() }
    }

    init {
        compile()
    }

    private fun compile() {
//        logger.warn { "CompiledShader src: ${source}" }

        gl.runInContext {
            gl.check { shaderSource(shaderId, source) }
            gl.check { compileShader(shaderId) }
        }
    }

    fun validate() {
        gl.runInContext {
            if (gl.check { getShaderParameter(shaderId, GL_COMPILE_STATUS) } != GL_TRUE) {
                val infoLog = gl.check { getShaderInfoLog(shaderId) }
                logger.warn {
                    "Failed to compile shader: $infoLog\n" +
                            "Version: \${gl.getParameter(GL_VERSION)}\n" +
                            "GLSL Version: \${gl.getParameter(GL_SHADING_LANGUAGE_VERSION)}\n" +
                            "\n" +
                            source
                }
                throw CompilationException(infoLog ?: "huh?")
            }
        }
    }

    companion object {
        val logger = Logger("CompiledShader")
    }
}

