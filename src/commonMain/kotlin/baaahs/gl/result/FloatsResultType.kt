package baaahs.gl.result

import baaahs.gl.GlContext
import com.danielgergely.kgl.FloatBuffer
import com.danielgergely.kgl.GL_FLOAT

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
