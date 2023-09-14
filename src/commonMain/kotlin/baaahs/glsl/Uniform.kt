package baaahs.glsl

import baaahs.Color
import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.UniformLocation

interface GlslUniform {
    fun set(value: Boolean) = set(if (value) 1 else 0)

    fun set(x: Int)
    fun set(x: Int, y: Int)
    fun set(x: Int, y: Int, z: Int)
    fun set(x: Int, y: Int, z: Int, w: Int)

    fun set(vector2I: Vector2I) = set(vector2I.x, vector2I.y)
//    fun set(vector3I: Vector3I) = set(vector3I.x, vector3I.y, vector3I.z)
//    fun set(vector4I: Vector4I) = set(vector4I.x, vector4I.y, vector4I.z, vector4I.w)

    fun set(x: Float)
    fun set(x: Float, y: Float)
    fun set(x: Float, y: Float, z: Float)
    fun set(x: Float, y: Float, z: Float, w: Float)

    fun set(vector2F: Vector2F) = set(vector2F.x, vector2F.y)
    fun set(vector3F: Vector3F) = set(vector3F.x, vector3F.y, vector3F.z)
    fun set(vector4F: Vector4F) = set(vector4F.x, vector4F.y, vector4F.z, vector4F.w)

    fun set(matrix: Matrix4F)
    fun set(eulerAngle: EulerAngle)
    fun set(textureUnit: GlContext.TextureUnit)

    fun set(value: Any) =
        when (value) {
            is Boolean -> set(value)

            is Int -> set(value)
            is Vector2I -> set(value)
//            is Vector3I -> uniform.set(newValue)
//            is Vector4I -> uniform.set(newValue)

            is Float -> set(value)
            is Vector2F -> set(value)
            is Vector3F -> set(value)
            is Vector4F -> set(value)

            is Matrix4F -> set(value)
            is EulerAngle -> set(value)
            is GlContext.TextureUnit -> set(value)

            is Color -> set(value.redF, value.greenF, value.blueF, value.alphaF)

            else -> error("unsupported uniform type ${value::class.simpleName}")
        }
}

interface Uniform

class IntUniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Int) = uniform.set(x)
}

class Int2Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Int, y: Int) = uniform.set(x, y)
}

class Int3Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Int, y: Int, z: Int) = uniform.set(x, y, z)
}

class Int4Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Int, y: Int, z: Int, w: Int) = uniform.set(x, y, z, w)
}

class FloatUniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Float) = uniform.set(x)
}

class Float2Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Float, y: Float) = uniform.set(x, y)
    fun set(vector2F: Vector2F) = uniform.set(vector2F.x, vector2F.y)
}

class Float3Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Float, y: Float, z: Float) = uniform.set(x, y, z)
    fun set(vector3F: Vector3F) = uniform.set(vector3F.x, vector3F.y, vector3F.z)
}

class Float4Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(x: Float, y: Float, z: Float, w: Float) = uniform.set(x, y, z, w)
    fun set(vector4F: Vector4F) = uniform.set(vector4F.x, vector4F.y, vector4F.z, vector4F.w)
}

class Matrix4Uniform(private val uniform: GlslUniform) : Uniform {
    fun set(matrix: Matrix4F) = uniform.set(matrix)
}

class EulerAngleUniform(private val uniform: GlslUniform) : Uniform {
    fun set(eulerAngle: EulerAngle) = uniform.set(eulerAngle)
}

class TextureUniform(val uniform: GlslUniform) : Uniform {
    fun set(textureUnit: GlContext.TextureUnit) = uniform.set(textureUnit)
}

class UniformImpl internal constructor(
    private val glslProgram: GlslProgram,
    private val uniformLocation: UniformLocation
): GlslUniform {
    override fun set(x: Int) = withProgram { uniform1i(uniformLocation, x) }
    override fun set(x: Int, y: Int) = withProgram { uniform2i(uniformLocation, x, y) }
    override fun set(x: Int, y: Int, z: Int) = withProgram { uniform3i(uniformLocation, x, y, z) }
    override fun set(x: Int, y: Int, z: Int, w: Int) = withProgram { uniform4i(uniformLocation, x, y, z, w) }
    override fun set(x: Float) = withProgram { uniform1f(uniformLocation, x) }
    override fun set(x: Float, y: Float) = withProgram { uniform2f(uniformLocation, x, y) }
    override fun set(x: Float, y: Float, z: Float) = withProgram { uniform3f(uniformLocation, x, y, z) }
    override fun set(x: Float, y: Float, z: Float, w: Float) = withProgram { uniform4f(uniformLocation, x, y, z, w) }

    override fun set(matrix: Matrix4F) = withProgram {
        uniformMatrix4fv(uniformLocation, false, matrix.elements)
    }

    override fun set(eulerAngle: EulerAngle) =
        set(eulerAngle.xRad.toFloat(), eulerAngle.yRad.toFloat(), eulerAngle.zRad.toFloat())

    override fun set(textureUnit: GlContext.TextureUnit) = textureUnit.setUniform(this)

    private fun <T> withProgram(fn: Kgl.() -> T): T {
        return glslProgram.withProgram(fn)
    }
}
