package baaahs.glsl

import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE
import com.danielgergely.kgl.Kgl

class Program private constructor(private val gl: Kgl, internal val id: com.danielgergely.kgl.Program) {
    fun getInfoLog(): String? = gl.getProgramInfoLog(id)
    fun attachShader(shader: Shader) = gl.attachShader(id, shader.id)
    fun link(): Boolean {
        gl.linkProgram(id)
        return gl.getProgramParameter(id, GL_LINK_STATUS) == GL_TRUE
    }
    fun bind() = gl.useProgram(id)

    fun getUniform(name: String): Uniform? = gl.getUniformLocation(id, name)?.let { Uniform(gl, it) }

    companion object {
        fun create(gl: Kgl): Program = Program(gl, gl.createProgram() ?: throw IllegalStateException())
    }
}
