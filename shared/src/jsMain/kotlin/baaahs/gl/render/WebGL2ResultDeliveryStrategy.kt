package baaahs.gl.render

import baaahs.document
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.gl.result.ResultStorage
import baaahs.internalTimerClock
import com.danielgergely.kgl.Buffer
import com.danielgergely.kgl.GlBuffer
import com.danielgergely.kgl.Kgl
import js.buffer.ArrayBufferView
import kotlinx.coroutines.delay
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.PIXEL_PACK_BUFFER
import web.gl.WebGL2RenderingContext.Companion.STREAM_READ
import web.gl.WebGL2RenderingContext.Companion.SYNC_GPU_COMMANDS_COMPLETE
import web.gl.WebGLSync

actual fun pickResultDeliveryStrategy(gl: GlContext): ResultDeliveryStrategy {
    return SwitchingResultDeliveryStrategy(gl as GlBase.JsGlContext)
//    return WebGl2ResultDeliveryStrategy(gl as GlBase.JsGlContext)
}

class SwitchingResultDeliveryStrategy(private val gl: GlBase.JsGlContext): ResultDeliveryStrategy {
    val sync = SyncResultDeliveryStrategy()
    val async = WebGl2ResultDeliveryStrategy(gl)

    private fun pickStrategy() =
        if (document.asDynamic()["strategy"] != "sync") async else sync

    override fun beforeRender() {
        pickStrategy().beforeRender()
    }

    override fun afterRender(frameBuffer: GlContext.FrameBuffer, resultStorage: ResultStorage) {
        pickStrategy().afterRender(frameBuffer, resultStorage)
    }

    override suspend fun awaitResults(frameBuffer: GlContext.FrameBuffer, resultStorage: ResultStorage) {
        pickStrategy().awaitResults(frameBuffer, resultStorage)
    }
}

// See https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/WebGL_best_practices#use_non-blocking_async_data_readback
class WebGl2ResultDeliveryStrategy(private val gl: GlBase.JsGlContext) : ResultDeliveryStrategy {
    private val webgl2 = gl.webgl

    val bufs: MutableList<Pair<Buffer, GlBuffer>> = arrayListOf()

    override fun afterRender(frameBuffer: GlContext.FrameBuffer, resultStorage: ResultStorage) {
        resultStorage.resultBuffers.forEach {
            val gpuBuffer = it.gpuBuffer
            val resultType = it.type
            val cpuBuffer: Buffer = it.cpuBuffer

            frameBuffer.withRenderBufferAsAttachment0(gpuBuffer) {
                val glBuf = gl.check { createBuffer() }
                gl.check { bindBuffer(PIXEL_PACK_BUFFER, glBuf) }
                gl.check { webgl2.bufferData(PIXEL_PACK_BUFFER, it.cpuBufferSizeInBytes, STREAM_READ) }

                gl.check {
                    webgl2.readPixels(
                        0, 0, gpuBuffer.curWidth, gpuBuffer.curHeight,
                        resultType.readFormat.unsafeCast<GLenum>(),
                        resultType.readType.unsafeCast<GLenum>(),
                        0
                    )
                }
                gl.check { bindBuffer(PIXEL_PACK_BUFFER, null) }

                bufs.add(cpuBuffer to glBuf)
            }
        }
    }

    override suspend fun awaitResults(frameBuffer: GlContext.FrameBuffer, resultStorage: ResultStorage) {
        FenceSync(gl).await()

        bufs.forEach { (cpuBuffer, glBuf) ->
            gl.check { bindBuffer(PIXEL_PACK_BUFFER, glBuf) }
            gl.check {
                webgl2.getBufferSubData(
                    PIXEL_PACK_BUFFER, 0,
                    cpuBuffer.getJsBufferWithOffset().unsafeCast<ArrayBufferView>(),
                    0, 0
                )
            }
            gl.check { bindBuffer(PIXEL_PACK_BUFFER, null) }
            gl.check { deleteBuffer(glBuf) }
        }
        bufs.clear()
    }
}

private fun Kgl.bindBuffer(target: GLenum, bufferId: GlBuffer?) =
    bindBuffer(target as Int, bufferId)

class FenceSync(private val gl: GlBase.JsGlContext) {
    private val webgl2 = gl.webgl

    private val fenceSync = gl.check { webgl2.fenceSync(SYNC_GPU_COMMANDS_COMPLETE, 0) }

    suspend fun await() {
        fenceSync ?: error("Fence sync not supported.")

        gl.check { webgl2.flush() }

        val startTime = internalTimerClock.now()
        val maxTries = fenceTimeoutMs / delayBetweenSyncChecksMs
        var tries = 0
        while (tries ++ < maxTries) {
            val result = clientWaitSync(fenceSync, timeout = 0)
            if (result) {
                gl.check { webgl2.deleteSync(fenceSync) }
                return
            }

            delay(delayBetweenSyncChecksMs)
        }

        gl.check { webgl2.deleteSync(fenceSync) }
        error("Fence sync failed after ${internalTimerClock.now() - startTime}ms, $tries tries!")
    }

    private fun clientWaitSync(fence: WebGLSync, timeout: Int): Boolean {
        return when (
            val result = gl.check { gl.webgl.clientWaitSync(fence, 0, timeout) }
        ) {
            WebGL2RenderingContext.ALREADY_SIGNALED -> true
            WebGL2RenderingContext.TIMEOUT_EXPIRED -> false
            WebGL2RenderingContext.CONDITION_SATISFIED -> true
            WebGL2RenderingContext.WAIT_FAILED -> false
            else -> error("Unknown result $result.")
        }
    }

    companion object {
        private const val fenceTimeoutMs = 5000
        private const val delayBetweenSyncChecksMs = 3L
    }
}