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
    companion object {
        val logger = Logger("baaahs.glsl.Shader")
    }

    init {
        compile()
    }

    private fun compile() {
//        logger.warn { "CompiledShader src: ${source}" }

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

    class CompilationException(val errorMessage: String) : Exception("GLSL Compilation Error: $errorMessage") {
        fun getErrors(): List<GlslError> {
            return errorMessage.trimEnd().split("\n").map { line ->
                pattern.matchEntire(line)?.groupValues?.let { match ->
                    @Suppress("UNUSED_VARIABLE") val file = match[1].toInt()
                    val row = match[2].toInt()
                    val message = match[3]
                    GlslError(row, 0, message)
                } ?: GlslError(-1, -1, line)
            }
        }

        companion object {
            val pattern = Regex("^ERROR: (\\d+):(\\d+): (.*)$")
        }
    }

    class LinkException(errorMessage: String) : Exception("GLSL Link Error: $errorMessage")

    class GlslError(val row: Int, val column: Int, val message: String) {
        constructor(message: String) : this(0, 0, message)
    }
}

