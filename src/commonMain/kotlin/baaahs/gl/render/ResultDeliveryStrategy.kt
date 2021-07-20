package baaahs.gl.render

import baaahs.fixtures.ResultBuffer
import baaahs.gl.GlContext

interface ResultDeliveryStrategy {
    fun beforeRender() {}

    fun afterRender(
        frameBuffer: GlContext.FrameBuffer,
        resultBuffers: List<ResultBuffer>
    ) {}

    suspend fun awaitResults(
        frameBuffer: GlContext.FrameBuffer,
        resultBuffers: List<ResultBuffer>
    ) {}
}

class SyncResultDeliveryStrategy : ResultDeliveryStrategy {
    override suspend fun awaitResults(frameBuffer: GlContext.FrameBuffer, resultBuffers: List<ResultBuffer>) {
        resultBuffers.forEach {
            val gpuBuffer = it.gpuBuffer
            val resultType = it.type

            frameBuffer.withRenderBufferAsAttachment0(gpuBuffer) {
                gpuBuffer.readPixels(
                    0, 0, gpuBuffer.curWidth, gpuBuffer.curHeight,
                    resultType.readPixelFormat, resultType.readType, it.cpuBuffer
                )
            }
        }
    }
}

expect fun pickResultDeliveryStrategy(gl: GlContext): ResultDeliveryStrategy