package baaahs.gl.render

import baaahs.document
import baaahs.fixtures.ResultBuffer
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.gl.WebGL2RenderingContext
import baaahs.gl.WebGLSync
import baaahs.internalTimerClock
import baaahs.time
import baaahs.util.asMillis
import com.danielgergely.kgl.Buffer
import com.danielgergely.kgl.GlBuffer
import kotlinx.coroutines.delay

actual fun pickResultDeliveryStrategy(gl: GlContext): ResultDeliveryStrategy {
    return SwitchingResultDeliveryStrategy(gl as GlBase.JsGlContext)
//    return WebGl2ResultDeliveryStrategy(gl as GlBase.JsGlContext)
}

class SwitchingResultDeliveryStrategy(private val gl: GlBase.JsGlContext): ResultDeliveryStrategy {
    val sync = SyncResultDeliveryStrategy()
    val async = WebGl2ResultDeliveryStrategy(gl)

    private fun pickStrategy() =
        if (document.asDynamic()["strategy"] == "async") async else sync

    override fun beforeRender() {
        pickStrategy().beforeRender()
    }

    override fun afterRender(frameBuffer: GlContext.FrameBuffer, resultBuffers: List<ResultBuffer>) {
        pickStrategy().afterRender(frameBuffer, resultBuffers)
    }

    override suspend fun awaitResults(frameBuffer: GlContext.FrameBuffer, resultBuffers: List<ResultBuffer>) {
        pickStrategy().awaitResults(frameBuffer, resultBuffers)
    }
}

class WebGl2ResultDeliveryStrategy(private val gl: GlBase.JsGlContext) : ResultDeliveryStrategy {
    private val webgl2 = gl.webgl

    val bufs: MutableList<Pair<Buffer, GlBuffer>> = arrayListOf()

    override fun afterRender(frameBuffer: GlContext.FrameBuffer, resultBuffers: List<ResultBuffer>) {
        resultBuffers.forEach {
            val gpuBuffer = it.gpuBuffer
            val resultType = it.type
            val cpuBuffer: Buffer = it.cpuBuffer

            frameBuffer.withRenderBufferAsAttachment0(gpuBuffer) {
                val glBuf = gl.check { createBuffer() }
                gl.check { bindBuffer(WebGL2RenderingContext.PIXEL_PACK_BUFFER, glBuf) }
                gl.check {
                    bufferData(
                        WebGL2RenderingContext.PIXEL_PACK_BUFFER,
                        cpuBuffer,
                        cpuBuffer.sizeInBytes,
                        WebGL2RenderingContext.STATIC_READ
                    )
                }

                gl.check {
                    webgl2.readPixels(
                        0, 0, gpuBuffer.curWidth, gpuBuffer.curHeight,
                        resultType.readPixelFormat, resultType.readType, 0
                    )
                }
                gl.check { bindBuffer(WebGL2RenderingContext.PIXEL_PACK_BUFFER, null) }

                bufs.add(cpuBuffer to glBuf)
//                gl.check { deleteBuffer(buf) }
            }
        }
    }

    override suspend fun awaitResults(frameBuffer: GlContext.FrameBuffer, resultBuffers: List<ResultBuffer>) {
        val startTime = internalTimerClock.now()
        val fenceSync = gl.check { webgl2.fenceSync(WebGL2RenderingContext.SYNC_GPU_COMMANDS_COMPLETE, 0) }

        val syncTime = internalTimerClock.now()
        val syncElapsed = waitForFenceSync(fenceSync)

        bufs.forEach { (cpuBuffer, glBuf) ->
            gl.check { bindBuffer(WebGL2RenderingContext.PIXEL_PACK_BUFFER, glBuf) }
            val length = cpuBuffer.sizeInBytes
            gl.check {
                webgl2.getBufferSubData(
                    WebGL2RenderingContext.PIXEL_PACK_BUFFER, 0, cpuBuffer.buffer, 0, length
                )
            }
        }
        bufs.clear()

        val now = internalTimerClock.now()
        println(
            "async.awaitResults() took ${(syncTime - startTime).asMillis()}ms (sync)" +
                    " [${syncElapsed.asMillis()}ms blocking]" +
                    " + ${(now - syncTime).asMillis()}ms (read)" +
                    " = ${(now - startTime).asMillis()}ms (total)"
        )
    }

    private suspend fun waitForFenceSync(fenceSync: WebGLSync): Double {
        val startTime = internalTimerClock.now()
        var maxTries = fenceTimeoutMs / fencePollTimeMs
        var elapsed = 0.0
        while (maxTries > 0) {
            val syncStartTime = internalTimerClock.now()
            val result = clientWaitSync(fenceSync, timeout = 0)
            elapsed += internalTimerClock.now() - syncStartTime
            if (result) return elapsed

            delay(5)
            maxTries--
        }
        error("Failed to sync after ${internalTimerClock.now() - startTime}ms!")
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
        private val fencePollTimeMs = 5
        private val fenceTimeoutMs = 5000
    }
}
