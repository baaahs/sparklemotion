package baaahs.fixtures

import baaahs.gl.GlContext
import com.danielgergely.kgl.Buffer
import com.danielgergely.kgl.GL_COLOR_ATTACHMENT0

interface DeviceType {
    val resultParams: List<DeviceParam>
}

class DeviceParam(val title: String, val type: DeviceParamType) {
    fun allocate(gl: GlContext, index: Int): DeviceParamBuffer {
        return type.createParamBuffer(gl, index)
    }
}

abstract class DeviceParamBuffer(
    gl: GlContext,
    private val paramIndex: Int,
    val type: DeviceParamType
) {
    private var curWidth = 0
    private var curHeight = 0
    private var cpuBufferSize = 0

    private val gpuBuffer = gl.createRenderBuffer()
    abstract val cpuBuffer: Buffer

    // Storage smaller than 16x1 causes a GL error.
    init { resize(16, 1) }

    fun resize(width: Int, height: Int) {
        gpuBuffer.storage(type.renderPixelFormat, width, height)
        curWidth = width
        curHeight = height

        val bufferSize = width * height
        if (cpuBufferSize != bufferSize) {
            resizeBuffer(bufferSize)
            cpuBufferSize = bufferSize
        }
    }

    abstract fun resizeBuffer(size: Int)

    fun attachTo(fb: GlContext.FrameBuffer) {
        fb.attach(gpuBuffer, GL_COLOR_ATTACHMENT0 + paramIndex)
    }

    fun afterFrame(frameBuffer: GlContext.FrameBuffer) {
        frameBuffer.withRenderBufferAsAttachment0(gpuBuffer) {
            gpuBuffer.readPixels(
                0, 0, gpuBuffer.curWidth, gpuBuffer.curHeight,
                type.readPixelFormat, type.readType, cpuBuffer
            )
        }
    }

    fun release() {
        gpuBuffer.release()
    }
}