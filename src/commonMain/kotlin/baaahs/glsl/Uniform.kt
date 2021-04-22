package baaahs.glsl

import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.UniformLocation

interface Uniform {
    fun set(x: Int)
    fun set(x: Int, y: Int)
    fun set(x: Int, y: Int, z: Int)
    fun set(x: Float)
    fun set(x: Float, y: Float)
    fun set(x: Float, y: Float, z: Float)
    fun set(x: Float, y: Float, z: Float, w: Float)
    fun set(vector3F: Vector3F)
    fun set(textureUnit: GlContext.TextureUnit)
}

class UniformImpl internal constructor(
    private val glslProgram: GlslProgram,
    private val uniformLocation: UniformLocation
): Uniform {
    override fun set(x: Int) = withProgram { uniform1i(uniformLocation, x) }
    override fun set(x: Int, y: Int) = withProgram { uniform2i(uniformLocation, x, y) }
    override fun set(x: Int, y: Int, z: Int) = withProgram { uniform3i(uniformLocation, x, y, z) }
    override fun set(x: Float) = withProgram { uniform1f(uniformLocation, x) }
    override fun set(x: Float, y: Float) = withProgram { uniform2f(uniformLocation, x, y) }
    override fun set(x: Float, y: Float, z: Float) = withProgram { uniform3f(uniformLocation, x, y, z) }
    override fun set(x: Float, y: Float, z: Float, w: Float) = withProgram { uniform4f(uniformLocation, x, y, z, w) }

    override fun set(vector3F: Vector3F) = set(vector3F.x, vector3F.y, vector3F.z)

    override fun set(textureUnit: GlContext.TextureUnit) = textureUnit.setUniform(this)

    private fun <T> withProgram(fn: Kgl.() -> T): T {
        return glslProgram.withProgram(fn)
    }
}
