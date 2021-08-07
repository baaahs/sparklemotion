package baaahs.gl.param

import baaahs.gl.GlContext
import baaahs.gl.GlContext.Companion.GL_RGB32F
import baaahs.gl.GlContext.Companion.GL_RGBA32F
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.result.BufferView
import baaahs.glsl.Uniform
import com.danielgergely.kgl.*
import kotlin.math.min

class FloatsParamBuffer(val id: String, val stride: Int, private val gl: GlContext) : ParamBuffer {
    private val textureUnit = gl.getTextureUnit(this)
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
        with(textureUnit) {
            bindTexture(texture)
            configure(GL_NEAREST, GL_NEAREST)

            val (internalFormat, format) = when(stride) {
                1 -> GL_R32F to GL_RED
                2 -> GL_RG32F to GL_RG
                3 -> GL_RGB32F to GL_RGB
                4 -> GL_RGBA32F to GL_RGBA
                else -> error("Stride currently has to be between 1 and 4.")
            }

            uploadTexture(
                0,
                internalFormat, width, height, 0,
                format, GL_FLOAT, FloatBuffer(floats)
            )
        }
    }

    override fun setTexture(uniform: Uniform) {
        uniform.set(textureUnit)
    }

    override fun bind(glslProgram: GlslProgram): ProgramFeed {
        val uniform = glslProgram.getUniform(id)

        return object : ProgramFeed {
            override val isValid get() = uniform != null

            override fun setOnProgram() {
                if (uniform != null) {
                    textureUnit.bindTexture(texture)
                    uniform.set(textureUnit)
                }
            }
        }
    }

    fun scoped(
        renderTarget: FixtureRenderTarget,
        callback: ((Int) -> Float)? = null
    ) = object : BufferView<Float> {
        val offset = renderTarget.pixel0Index
        val size = renderTarget.pixelCount

        override fun set(pixelIndex: Int, t: Float) = set(pixelIndex, 0, t)

        override fun set(pixelIndex: Int, index: Int, t: Float) {
            if (pixelIndex > size) throw IndexOutOfBoundsException("$pixelIndex > $size")
            floats[(offset + pixelIndex) * stride + index] = t
        }

        override fun get(pixelIndex: Int): Float = get(pixelIndex, 0)

        override fun get(pixelIndex: Int, index: Int): Float {
            if (pixelIndex > size) throw IndexOutOfBoundsException("$pixelIndex > $size")
            return floats[(offset + pixelIndex) * stride]
        }
    }.also {
        if (callback != null) {
            val offset = renderTarget.pixel0Index
            for (pixelIndex in 0 until renderTarget.pixelCount) {
                floats[(offset + pixelIndex) * stride] = callback.invoke(pixelIndex)
            }
        }
    }

    override fun release() {
        gl.check { deleteTexture(texture) }
        textureUnit.release()
    }
}