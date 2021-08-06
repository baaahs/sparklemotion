package baaahs.fixtures

import baaahs.Color
import baaahs.Pixels
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.geom.Vector4F
import baaahs.gl.GlContext
import baaahs.model.Model
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.*

interface ResultType<T : ResultBuffer> {
    val renderPixelFormat: Int
    val readPixelFormat: Int
    val readType: Int
    val stride: Int

    fun createResultBuffer(gl: GlContext, index: Int): T
}

object ColorResultType : ResultType<ColorResultType.Buffer> {
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

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): ColorFixtureResults {
            return ColorFixtureResults(this, bufferOffset, fixture)
        }
    }

    class ColorFixtureResults(
        private val buffer: Buffer,
        pixelOffset: Int,
        private val fixture: Fixture,
    ) : FixtureResults(pixelOffset, fixture.pixelCount), Pixels {
        private val transport = fixture.transport

        override val size: Int
            get() = pixelCount

        override operator fun get(i: Int): Color = buffer[pixelOffset + i]

        override fun set(i: Int, color: Color) = TODO("not implemented")

        override fun set(colors: Array<Color>) = TODO("not implemented")

        override fun iterator(): Iterator<Color> {
            return iterator {
                for (i in 0 until pixelCount) yield(get(i))
            }
        }

        override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) {
            val fixtureConfig = fixture.fixtureConfig as PixelArrayDevice.Config
            val buf = fixtureConfig.writeData(this)

            transport.deliverBytes(buf)
            remoteVisualizers.sendFrameData(entity) { out ->
                out.writeInt(fixture.pixelCount)
                out.writeBytes(buf)
            }
        }
    }
}

// Yuck. XY and XYZ fail, at least on WebGL. Maybe they work on others?

object FloatResultType : FloatsResultType<FloatResultType.ResultBuffer>(
    // Haven't tested this, but I'm assuming it doesn't work.
    1, GL_R32F, GL_RED
) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Float {
            val offset = pixelIndex * type.stride

            return floatBuffer[offset]
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return FloatFixtureResults(this, bufferOffset, fixture.pixelCount)
        }
    }

    class FloatFixtureResults(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : FixtureResults(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Float = buffer[pixelOffset + pixelIndex]

        override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) = TODO("FloatFixtureResults.send() not implemented")
    }
}

object Vec2ResultType : FloatsResultType<Vec2ResultType.ResultBuffer>(
    // This doesn't work in WebGL2 because... dunno.
    //    2, GL_RG32F, GL_RG
    // readPixels() fails with INVALID_OPERATION.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector2F {
            val offset = pixelIndex * type.stride

            return Vector2F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return Vec2FixtureResults(this, bufferOffset, fixture.pixelCount)
        }
    }

    class Vec2FixtureResults(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : FixtureResults(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector2F = buffer[pixelOffset + pixelIndex]

        override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) {
            TODO("Vec2FixtureResults.send() not implemented")
        }
    }
}

object Vec3ResultType : FloatsResultType<Vec3ResultType.ResultBuffer>(
    // This doesn't work in WebGL2 because EXT_color_buffer_float doesn't have RGB32F!?
    //    3, GlContext.GL_RGB32F, GL_RGB
    // framebufferRenderbuffer() fails with INVALID_ENUM.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector3F {
            val offset = pixelIndex * type.stride

            return Vector3F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return Vec3FixtureResults(this, bufferOffset, fixture.pixelCount)
        }
    }

    class Vec3FixtureResults(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : FixtureResults(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector3F = buffer[pixelOffset + pixelIndex]

        override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) {
            TODO("Vec3FixtureResults.send() not implemented")
        }
    }
}

object Vec4ResultType : FloatsResultType<Vec4ResultType.ResultBuffer>(4, GlContext.GL_RGBA32F, GL_RGBA) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector4F {
            val offset = pixelIndex * type.stride

            return Vector4F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2],
                w = floatBuffer[offset + 3]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return Vec4FixtureResults(this, bufferOffset, fixture.pixelCount)
        }
    }

    class Vec4FixtureResults(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : FixtureResults(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector4F = buffer[pixelOffset + pixelIndex]

        override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) {
            TODO("Vec4FixtureResults.send() not implemented")
        }
    }
}


abstract class FloatsResultType<T : FloatsResultType.Buffer>(
    private val floatCount: Int,
    override val renderPixelFormat: Int,
    override val readPixelFormat: Int
) : ResultType<T> {
    override val readType: Int
        get() = GL_FLOAT
    override val stride: Int
        get() = floatCount

    abstract class Buffer(
        gl: GlContext, index: Int, type: ResultType<*>
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
