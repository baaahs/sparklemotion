package baaahs.glsl

import baaahs.Logger
import com.danielgergely.kgl.GL_COMPILE_STATUS
import com.danielgergely.kgl.GL_TRUE
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.Shader

class CompiledShader(
    private val gl: Kgl,
    internal val id: Shader,
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
            logger.error {
                "Failed to compile shader: $infoLog\n" +
                        "Version: \${gl.getParameter(GL_VERSION)}\n" +
                        "GLSL Version: \${gl.getParameter(GL_SHADING_LANGUAGE_VERSION)}\n" +
                        "\n" +
                        source
            }
            throw CompilationException(infoLog ?: "huh?")
        }
    }

    class CompilationException(val errorMessage: String) : Exception("GLSL Compilation Error: $errorMessage")
}
