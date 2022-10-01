package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.gl.result.ResultStorage

interface ResultDeliveryStrategy {
    fun beforeRender() {}

    fun afterRender(
        frameBuffer: GlContext.FrameBuffer,
        resultStorage: ResultStorage
    ) {}

    suspend fun awaitResults(
        frameBuffer: GlContext.FrameBuffer,
        resultStorage: ResultStorage
    ) {}
}

class SyncResultDeliveryStrategy : ResultDeliveryStrategy {
    override suspend fun awaitResults(frameBuffer: GlContext.FrameBuffer, resultStorage: ResultStorage) {
        resultStorage.resultBuffers.forEach {
            val gpuBuffer = it.gpuBuffer
            val resultType = it.type

            frameBuffer.withRenderBufferAsAttachment0(gpuBuffer) {
                gpuBuffer.readPixels(
                    0, 0, gpuBuffer.curWidth, gpuBuffer.curHeight,
                    resultType.readFormat, resultType.readType, it.cpuBuffer
                )
            }
        }
    }
}

fun GlContext.pickResultDeliveryStrategy() =
    pickResultDeliveryStrategy(this)

expect fun pickResultDeliveryStrategy(gl: GlContext): ResultDeliveryStrategy