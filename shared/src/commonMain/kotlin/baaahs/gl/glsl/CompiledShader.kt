package baaahs.gl.glsl

import baaahs.gl.GlContext
import baaahs.util.Logger
import com.danielgergely.kgl.GL_COMPILE_STATUS
import com.danielgergely.kgl.GL_TRUE
import com.danielgergely.kgl.Shader

class CompiledShader(
    private val gl: GlContext,
    type: Int,
    internal val source: String
) {
    val shaderId: Shader = gl.runInContext {
        gl.check {
            createShader(type)
        } ?: throw ResourceAllocationException("Failed to allocate a GL shader.")
    }

    init {
        compile()
    }

    private fun compile() {
        gl.runInContext {
            gl.noCheck { shaderSource(shaderId, source) }
            gl.noCheck { compileShader(shaderId) }
        }
    }

    fun validate() {
        gl.runInContext {
            if (gl.check { getShaderParameter(shaderId, GL_COMPILE_STATUS) } != GL_TRUE) {
                val infoLog = gl.check { getShaderInfoLog(shaderId) }
                throw CompilationException(infoLog ?: "huh?", source)
            }
        }
    }

    fun release() {
        gl.runInContext { gl.check { deleteShader(shaderId) } }
    }

    companion object {
        val logger = Logger("CompiledShader")
    }
}

