package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import com.danielgergely.kgl.Buffer
import com.danielgergely.kgl.GL_COLOR_ATTACHMENT0

abstract class ResultBuffer(
    gl: GlContext,
    private val resultIndex: Int,
    val type: ResultType<*>,
    private val storageFormat: Int
) {
    var unitCount: Int = 0
        private set

    private var curWidth = 0
    private var curHeight = 0
    private var cpuBufferSize = 0

    val gpuBuffer = gl.createRenderBuffer()
    abstract val cpuBuffer: Buffer
    var cpuBufferSizeInBytes: Int = -1
        private set

    // Storage smaller than 16x1 causes a GL error.
    init {
        resize(16, 1)
    }

    fun resize(width: Int, height: Int) {
        if (width == curWidth && height == curHeight) return

        gpuBuffer.storage(storageFormat, width, height)
        curWidth = width
        curHeight = height

        val bufferSize = width * height
        unitCount = bufferSize
        if (cpuBufferSize != bufferSize) {
            cpuBufferSizeInBytes = resizeBuffer(bufferSize)
            cpuBufferSize = bufferSize
        }
    }

    /**
     * @return the buffer size in bytes.
     */
    abstract fun resizeBuffer(size: Int): Int

    fun attachTo(fb: GlContext.FrameBuffer) {
        fb.attach(gpuBuffer, GL_COLOR_ATTACHMENT0 + resultIndex)
    }

    abstract fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults

    fun release() {
        gpuBuffer.release()
    }
}