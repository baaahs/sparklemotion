package baaahs.fixtures

import baaahs.Color
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.geom.Vector4F
import baaahs.gl.GlContext
import com.danielgergely.kgl.*

interface ResultType {
    val renderPixelFormat: Int
    val readPixelFormat: Int
    val readType: Int
    val stride: Int

    fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer
}

object ColorResultType : ResultType {
    override val renderPixelFormat: Int
        get() = GlContext.GL_RGBA8
    override val readPixelFormat: Int
        get() = GL_RGBA
    override val readType: Int
        get() = GL_UNSIGNED_BYTE
    override val stride: Int
        get() = 4

    override fun createResultBuffer(gl: GlContext, index: Int): Buffer {
        return Buffer(gl, index)
    }

    class Buffer(gl: GlContext, resultIndex: Int) : ResultBuffer(gl, resultIndex, ColorResultType) {
        private lateinit var byteBuffer: ByteBuffer

        override val cpuBuffer: com.danielgergely.kgl.Buffer
            get() = byteBuffer

        override fun resizeBuffer(size: Int) {
            byteBuffer = ByteBuffer(size * stride)
        }

        operator fun get(pixelIndex: Int): Color {
            val offset = pixelIndex * stride

            return Color(
                red = byteBuffer[offset],
                green = byteBuffer[offset + 1],
                blue = byteBuffer[offset + 2],
                alpha = byteBuffer[offset + 3]
            )
        }

        override fun getView(pixelOffset: Int, pixelCount: Int): ColorResultView {
            return ColorResultView(this, pixelOffset, pixelCount)
        }
    }

    class ColorResultView(
        private val buffer: Buffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : ResultView(pixelOffset, pixelCount), Iterable<Color> {
        operator fun get(pixelIndex: Int): Color = buffer[pixelOffset + pixelIndex]

        override fun iterator(): Iterator<Color> {
            return iterator {
                for (i in 0 until pixelCount) yield(get(i))
            }
        }
    }
}

// Yuck. XY and XYZ fail, at least on WebGL. Maybe they work on others?

object FloatResultType : FloatsResultType(
    // Haven't tested this, but I'm assuming it doesn't work.
    1, GL_R32F, GL_RED
) {
    override fun createResultBuffer(gl: GlContext, index: Int): baaahs.fixtures.ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Float {
            val offset = pixelIndex * type.stride

            return floatBuffer[offset]
        }

        override fun getView(pixelOffset: Int, pixelCount: Int): ResultView {
            return FloatResultView(this, pixelOffset, pixelCount)
        }
    }

    class FloatResultView(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : ResultView(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Float = buffer[pixelOffset + pixelIndex]
    }
}

object Vec2ResultType : FloatsResultType(
    // This doesn't work in WebGL2 because... dunno.
    //    2, GL_RG32F, GL_RG
    // readPixels() fails with INVALID_OPERATION.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createResultBuffer(gl: GlContext, index: Int): baaahs.fixtures.ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector2F {
            val offset = pixelIndex * type.stride

            return Vector2F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1]
            )
        }

        override fun getView(pixelOffset: Int, pixelCount: Int): ResultView {
            return Vec2ResultView(this, pixelOffset, pixelCount)
        }
    }

    class Vec2ResultView(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : ResultView(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector2F = buffer[pixelOffset + pixelIndex]
    }
}

object Vec3ResultType : FloatsResultType(
    // This doesn't work in WebGL2 because EXT_color_buffer_float doesn't have RGB32F!?
    //    3, GlContext.GL_RGB32F, GL_RGB
    // framebufferRenderbuffer() fails with INVALID_ENUM.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createResultBuffer(gl: GlContext, index: Int): baaahs.fixtures.ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector3F {
            val offset = pixelIndex * type.stride

            return Vector3F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2]
            )
        }

        override fun getView(pixelOffset: Int, pixelCount: Int): ResultView {
            return Vec3ResultView(this, pixelOffset, pixelCount)
        }
    }

    class Vec3ResultView(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : ResultView(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector3F = buffer[pixelOffset + pixelIndex]
    }
}

object Vec4ResultType : FloatsResultType(4, GlContext.GL_RGBA32F, GL_RGBA) {
    override fun createResultBuffer(gl: GlContext, index: Int): baaahs.fixtures.ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector4F {
            val offset = pixelIndex * type.stride

            return Vector4F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2],
                w = floatBuffer[offset + 3]
            )
        }

        override fun getView(pixelOffset: Int, pixelCount: Int): ResultView {
            return Vec4ResultView(this, pixelOffset, pixelCount)
        }
    }

    class Vec4ResultView(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : ResultView(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector4F = buffer[pixelOffset + pixelIndex]
    }
}


abstract class FloatsResultType(
    private val floatCount: Int,
    override val renderPixelFormat: Int,
    override val readPixelFormat: Int
) : ResultType {
    override val readType: Int
        get() = GL_FLOAT
    override val stride: Int
        get() = floatCount

    abstract class Buffer(
        gl: GlContext, index: Int, type: ResultType
    ) : ResultBuffer(
        gl.also { gl.ensureResultBufferCanContainFloats() }, index, type
    ) {
        protected lateinit var floatBuffer: FloatBuffer

        override val cpuBuffer: com.danielgergely.kgl.Buffer
            get() = floatBuffer

        override fun resizeBuffer(size: Int) {
            floatBuffer = FloatBuffer(size * type.stride)
        }
    }
}
