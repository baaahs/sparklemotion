package baaahs.glsl

import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import com.danielgergely.kgl.GL_TEXTURE_2D
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.Texture
import com.danielgergely.kgl.UniformLocation

interface GlslUniform {
    val name: String

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

class TextureUniform(
    val uniform: GlslUniform,
    private val gl: GlContext
) : Uniform {
    private var texture: Texture? = null

    fun set(texture: Texture?) {
        this.texture = texture
    }

    fun bindTextureUnitForRender(index: Int) {
        if (index > gl.maxTextureUnit)
            error("Too many texture units in use; max=${gl.maxTextureUnit}.")

        gl.bindActiveTexture(index, GL_TEXTURE_2D, texture)
        gl.check { uniform.set(index) }
    }
}

class UniformImpl internal constructor(
    private val glslProgram: GlslProgram,
    override val name: String,
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

    private fun <T> withProgram(fn: Kgl.() -> T): T {
        return glslProgram.withProgram(fn)
    }
}
