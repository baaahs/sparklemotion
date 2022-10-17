package baaahs.gl.result

import baaahs.gl.GlContext
import com.danielgergely.kgl.FloatBuffer
import com.danielgergely.kgl.GL_FLOAT

/** This doesn't work on iOS devices as of iOS 14.7. */
abstract class FloatsResultType<T : FloatsResultType.Buffer>(
    private val floatCount: Int,
    override val readFormat: Int
) : ResultType<T> {
    override val readType: Int
        get() = GL_FLOAT
    override val stride: Int
        get() = floatCount

    abstract class Buffer(
        gl: GlContext, index: Int, type: FloatsResultType<*>
    ) : ResultBuffer(
        gl, index, type,
        run {
            when {
                gl.checkIfResultBufferCanContainFloats() -> {
                    when (type.floatCount) {
                        1 -> GlContext.GL_R32F
                        2 -> GlContext.GL_RG32F // Doesn't work in WebGL.
                        3 -> GlContext.GL_RGB32F // Doesn't work in WebGL.
                        4 -> GlContext.GL_RGBA32F
                        else -> error("huh?")
                    }
                }
                gl.checkIfResultBufferCanContainHalfFloats(required = true) -> {
                    when (type.floatCount) {
                        1 -> GlContext.GL_R16F
                        2 -> GlContext.GL_RG16F // Doesn't work in WebGL.
                        3 -> GlContext.GL_RGB16F // Doesn't work in WebGL.
                        4 -> GlContext.GL_RGBA16F
                        else -> error("huh?")
                    }
                }
                else -> error("huh?")
            }
        }
    ) {
        protected lateinit var floatBuffer: FloatBuffer

        override val cpuBuffer: com.danielgergely.kgl.Buffer
            get() = floatBuffer

        override var sizeInBytes: Int = 0

        override fun resizeBuffer(size: Int) {
            floatBuffer = FloatBuffer(size * type.stride)
            sizeInBytes = size * type.stride
        }
    }
}
