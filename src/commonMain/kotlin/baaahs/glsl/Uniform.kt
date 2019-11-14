package baaahs.glsl

import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.UniformLocation

class Uniform internal constructor(private val gl: Kgl, val uniformLocation: UniformLocation) {
    fun set(x: Int) = gl.check { gl.uniform1i(uniformLocation, x) }
    fun set(x: Int, y: Int) = gl.check { gl.uniform2i(uniformLocation, x, y) }
    fun set(x: Int, y: Int, z: Int) = gl.check { gl.uniform3i(uniformLocation, x, y, z) }
    fun set(x: Float) = gl.check { gl.uniform1f(uniformLocation, x) }
    fun set(x: Float, y: Float) = gl.check { gl.uniform2f(uniformLocation, x, y) }
    fun set(x: Float, y: Float, z: Float) = gl.check { gl.uniform3f(uniformLocation, x, y, z) }

    companion object {
        fun find(program: Program, name: String): Uniform? {
            return program.getUniform(name)
        }
    }
}
