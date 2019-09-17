package baaahs.glsl

import baaahs.Logger
import com.danielgergely.kgl.*

class Shader private constructor(
    private val gl: Kgl,
    internal val id: com.danielgergely.kgl.Shader,
    private val source: String
) {
    val logger = Logger("baaahs.glsl.Shader")

    init {
        compile()
    }

    private fun compile() {
        gl.shaderSource(id, source)
        gl.compileShader(id)

        if (gl.getShaderParameter(id, GL_COMPILE_STATUS) != GL_TRUE) {
            val infoLog = gl.getShaderInfoLog(id)
            logger.warn {
                "Failed to compile shader: $infoLog\n" +
                        "Version: \${gl.getParameter(GL_VERSION)}\n" +
                        "GLSL Version: \${gl.getParameter(GL_SHADING_LANGUAGE_VERSION)}\n" +
                        "\n" +
                        source
            }
            throw RuntimeException("Failed to compile shader: $infoLog")
        }
    }

    companion object {
        fun createVertexShader(gl: Kgl, source: String): Shader {
            val shaderId = gl.check { gl.createShader(GL_VERTEX_SHADER) } ?: throw IllegalStateException()
            return Shader(gl, shaderId, source)
        }

        fun createFragmentShader(gl: Kgl, source: String): Shader {
            val shaderId = gl.createShader(GL_FRAGMENT_SHADER) ?: throw IllegalStateException()
            return Shader(gl, shaderId, source)
        }
    }
}
