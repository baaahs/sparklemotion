package baaahs.glsl

import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.UniformLocation

class Uniform internal constructor(private val gl: Kgl, val uniformLocation: UniformLocation) {
    fun set(x: Int) = gl.uniform1i(uniformLocation, x)
    fun set(x: Int, y: Int) = gl.uniform2i(uniformLocation, x, y)
    fun set(x: Int, y: Int, z: Int) = gl.uniform3i(uniformLocation, x, y, z)
    fun set(x: Float) = gl.uniform1f(uniformLocation, x)
    fun set(x: Float, y: Float) = gl.uniform2f(uniformLocation, x, y)
    fun set(x: Float, y: Float, z: Float) = gl.uniform3f(uniformLocation, x, y, z)

    companion object {
        fun find(gl: Kgl, program: Program, name: String): Uniform? {
            return program.getUniform(name)
        }
    }
}
