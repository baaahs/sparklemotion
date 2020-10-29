package baaahs.fixtures

import baaahs.Color
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.geom.Vector4F
import baaahs.gl.GlContext
import com.danielgergely.kgl.*

interface DeviceParamType {
    val renderPixelFormat: Int
    val readPixelFormat: Int
    val readType: Int
    val stride: Int

    fun createParamBuffer(gl: GlContext, index: Int): DeviceParamBuffer
}

object ColorParam : DeviceParamType {
    override val renderPixelFormat: Int
        get() = GlContext.GL_RGBA8
    override val readPixelFormat: Int
        get() = GL_RGBA
    override val readType: Int
        get() = GL_UNSIGNED_BYTE
    override val stride: Int
        get() = 4

    override fun createParamBuffer(gl: GlContext, index: Int): Buffer {
        return Buffer(gl, index)
    }

    class Buffer(gl: GlContext, paramIndex: Int) : DeviceParamBuffer(gl, paramIndex, ColorParam) {
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
    }
}

// Yuck. XY and XYZ fail, at least on WebGL. Maybe they work on others?

object FloatXParam : FloatsParam(
    // Haven't tested this, but I'm assuming it doesn't work.
    1, GL_R32F, GL_RED
) {
    override fun createParamBuffer(gl: GlContext, index: Int): DeviceParamBuffer {
        return XParamBuffer(gl, index, this)
    }

    class XParamBuffer(gl: GlContext, index: Int, type: DeviceParamType) : FloatsParam.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Float {
            val offset = pixelIndex * type.stride

            return floatBuffer[offset]
        }
    }
}

object FloatXyParam : FloatsParam(
    // This doesn't work in WebGL2 because... dunno.
    //    2, GL_RG32F, GL_RG
    // readPixels() fails with INVALID_OPERATION.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createParamBuffer(gl: GlContext, index: Int): DeviceParamBuffer {
        return XyParamBuffer(gl, index, this)
    }

    class XyParamBuffer(gl: GlContext, index: Int, type: DeviceParamType) : FloatsParam.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector2F {
            val offset = pixelIndex * type.stride

            return Vector2F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1]
            )
        }
    }
}

object FloatXyzParam : FloatsParam(
    // This doesn't work in WebGL2 because EXT_color_buffer_float doesn't have RGB32F!?
    //    3, GlContext.GL_RGB32F, GL_RGB
    // framebufferRenderbuffer() fails with INVALID_ENUM.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createParamBuffer(gl: GlContext, index: Int): DeviceParamBuffer {
        return XyzParamBuffer(gl, index, this)
    }

    class XyzParamBuffer(gl: GlContext, index: Int, type: DeviceParamType) : FloatsParam.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector3F {
            val offset = pixelIndex * type.stride

            return Vector3F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2]
            )
        }
    }
}

object FloatXyzwParam : FloatsParam(4, GlContext.GL_RGBA32F, GL_RGBA) {
    override fun createParamBuffer(gl: GlContext, index: Int): DeviceParamBuffer {
        return XyzwParamBuffer(gl, index, this)
    }

    class XyzwParamBuffer(gl: GlContext, index: Int, type: DeviceParamType) : FloatsParam.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector4F {
            val offset = pixelIndex * type.stride

            return Vector4F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2],
                w = floatBuffer[offset + 3]
            )
        }
    }
}


abstract class FloatsParam(
    private val floatCount: Int,
    override val renderPixelFormat: Int,
    override val readPixelFormat: Int
) : DeviceParamType {
    override val readType: Int
        get() = GL_FLOAT
    override val stride: Int
        get() = floatCount

    open class Buffer(gl: GlContext, index: Int, type: DeviceParamType) : DeviceParamBuffer(gl, index, type) {
        protected lateinit var floatBuffer: FloatBuffer

        override val cpuBuffer: com.danielgergely.kgl.Buffer
            get() = floatBuffer

        override fun resizeBuffer(size: Int) {
            floatBuffer = FloatBuffer(size * type.stride)
        }
    }
}
