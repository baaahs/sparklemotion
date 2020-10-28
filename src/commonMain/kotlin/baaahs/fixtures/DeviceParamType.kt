package baaahs.fixtures

import baaahs.Color
import baaahs.geom.Vector2F
import baaahs.gl.GlContext
import baaahs.gl.render.RenderEngine
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
        get() = RenderEngine.GlConst.GL_RGBA8
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

object XyParam : DeviceParamType {
    override val renderPixelFormat: Int
        get() = RenderEngine.GlConst.GL_RGBA32F
    override val readPixelFormat: Int
        get() = GL_RGBA
    override val readType: Int
        get() = GL_FLOAT
    override val stride: Int
        get() = 4

    override fun createParamBuffer(gl: GlContext, index: Int): DeviceParamBuffer {
        return Buffer(gl, index)
    }

    class Buffer(gl: GlContext, index: Int) : DeviceParamBuffer(gl, index, XyParam) {
        private lateinit var floatBuffer: FloatBuffer

        override val cpuBuffer: com.danielgergely.kgl.Buffer
            get() = floatBuffer

        override fun resizeBuffer(size: Int) {
            floatBuffer = FloatBuffer(size * stride)
        }

        operator fun get(pixelIndex: Int): Vector2F {
            val offset = pixelIndex * ColorParam.stride

            return Vector2F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1]
            )
        }
    }
}
