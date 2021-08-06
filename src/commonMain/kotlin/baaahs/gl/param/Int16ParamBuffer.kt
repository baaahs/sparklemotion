package baaahs.gl.param

import baaahs.gl.GlContext
import baaahs.gl.GlContext.Companion.GL_RED_INTEGER
import baaahs.gl.GlContext.Companion.GL_RGB16I
import baaahs.gl.GlContext.Companion.GL_RGBA16I
import baaahs.gl.GlContext.Companion.GL_RGBA_INTEGER
import baaahs.gl.GlContext.Companion.GL_RGB_INTEGER
import baaahs.gl.GlContext.Companion.GL_RG_INTEGER
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.result.BufferView
import baaahs.glsl.Uniform
import com.danielgergely.kgl.GL_INT
import com.danielgergely.kgl.GL_NEAREST
import com.danielgergely.kgl.GL_R16I
import com.danielgergely.kgl.GL_RG16I
import kgl.IntBuffer
import kotlin.math.min

// TODO: This should support 16 or 32 bits, and signed or unsigned. Currently 16/signed.
class Int16ParamBuffer(val id: String, val stride: Int, private val gl: GlContext) : ParamBuffer {
    private val textureUnit = gl.getTextureUnit(this)
    private val texture = gl.check { createTexture() }
    private var ints = IntArray(0)
    private var width = 0
    private var height = 0

    override fun resizeBuffer(width: Int, height: Int) {
        val size = width * height

        val newInts = IntArray(size * stride)
        ints.copyInto(newInts, 0, 0, min(ints.size, size * stride))
        ints = newInts

        this.width = width
        this.height = height
    }

    override fun uploadToTexture() {
        with(textureUnit) {
            bindTexture(texture)
            configure(GL_NEAREST, GL_NEAREST)

            val (internalFormat, format) = when(stride) {
                1 -> GL_R16I to GL_RED_INTEGER
                2 -> GL_RG16I to GL_RG_INTEGER
                3 -> GL_RGB16I to GL_RGB_INTEGER
                4 -> GL_RGBA16I to GL_RGBA_INTEGER
                else -> error("Stride currently has to be between 1 and 4.")
            }

            uploadTexture(
                0,
                internalFormat, width, height, 0,
                format, GL_INT, IntBuffer(ints)
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
        callback: ((Int) -> Int)? = null
    ) = object : BufferView<Int> {
        val offset = renderTarget.pixel0Index
        val size = renderTarget.pixelCount

        override fun set(pixelIndex: Int, t: Int) = set(pixelIndex, 0, t)

        override fun set(pixelIndex: Int, index: Int, t: Int) {
            if (pixelIndex > size) throw IndexOutOfBoundsException("$pixelIndex > $size")
            ints[(offset + pixelIndex) * stride + index] = t
        }

        override fun get(pixelIndex: Int): Int = get(pixelIndex, 0)

        override fun get(pixelIndex: Int, index: Int): Int {
            if (pixelIndex > size) throw IndexOutOfBoundsException("$pixelIndex > $size")
            return ints[(offset + pixelIndex) * stride]
        }
    }.also {
        if (callback != null) {
            val offset = renderTarget.pixel0Index
            for (pixelIndex in 0 until renderTarget.pixelCount) {
                ints[(offset + pixelIndex) * stride] = callback.invoke(pixelIndex)
            }
        }
    }

    override fun release() {
        gl.check { deleteTexture(texture) }
        textureUnit.release()
    }
}