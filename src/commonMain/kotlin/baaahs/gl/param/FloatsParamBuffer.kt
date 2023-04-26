package baaahs.gl.param

import baaahs.gl.GlContext
import baaahs.gl.GlContext.Companion.GL_RGB32F
import baaahs.gl.GlContext.Companion.GL_RGBA32F
import baaahs.gl.render.ComponentRenderTarget
import baaahs.gl.result.BufferView
import baaahs.glsl.TextureUniform
import com.danielgergely.kgl.*
import kotlin.math.min

class FloatsParamBuffer(val id: String, val stride: Int, private val gl: GlContext) : ParamBuffer {
    private val texture = gl.check { createTexture() }
    private var floats = FloatArray(0)
    private var width = 0
    private var height = 0

    override fun resizeBuffer(width: Int, height: Int) {
        val size = width * height

        val newFloats = FloatArray(size * stride)
        floats.copyInto(newFloats, 0, 0, min(floats.size, size * stride))
        floats = newFloats

        this.width = width
        this.height = height
    }

    override fun uploadToTexture() {
        if (width == 0 || height == 0) return

        with(gl) {
            texture.configure(GL_NEAREST, GL_NEAREST)
            val (internalFormat, format) = when (stride) {
                1 -> GL_R32F to GL_RED
                2 -> GL_RG32F to GL_RG
                3 -> GL_RGB32F to GL_RGB
                4 -> GL_RGBA32F to GL_RGBA
                else -> error("Stride currently has to be between 1 and 4.")
            }
            texture.upload(
                0,
                internalFormat, width, height, 0,
                format, GL_FLOAT, FloatBuffer(floats)
            )
        }
    }

    override fun setTexture(uniform: TextureUniform) {
        uniform.set(texture)
    }

    fun scoped(
        renderTarget: ComponentRenderTarget,
        callback: ((Int) -> Float)? = null
    ) = object : BufferView<Float> {
        val offset = renderTarget.component0Index
        val size = renderTarget.componentCount

        override fun set(componentIndex: Int, t: Float) = set(componentIndex, 0, t)

        override fun set(componentIndex: Int, index: Int, t: Float) {
            if (componentIndex > size) throw IndexOutOfBoundsException("$componentIndex > $size")
            floats[(offset + componentIndex) * stride + index] = t
        }

        override fun get(componentIndex: Int): Float = get(componentIndex, 0)

        override fun get(componentIndex: Int, index: Int): Float {
            if (componentIndex > size) throw IndexOutOfBoundsException("$componentIndex > $size")
            return floats[(offset + componentIndex) * stride]
        }
    }.also {
        if (callback != null) {
            val offset = renderTarget.component0Index
            for (componentIndex in 0 until renderTarget.componentCount) {
                floats[(offset + componentIndex) * stride] = callback.invoke(componentIndex)
            }
        }
    }

    override fun release() {
        gl.check { deleteTexture(texture) }
    }
}