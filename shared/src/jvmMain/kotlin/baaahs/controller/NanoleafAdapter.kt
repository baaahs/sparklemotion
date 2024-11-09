package baaahs.controller

import baaahs.device.PixelFormat
import baaahs.io.ByteArrayWriter
import baaahs.util.Clock
import baaahs.util.Logger
import io.github.rowak.nanoleafapi.Frame
import io.github.rowak.nanoleafapi.NanoleafDevice
import io.github.rowak.nanoleafapi.NanoleafSearchCallback
import io.github.rowak.nanoleafapi.StaticEffect
import io.github.rowak.nanoleafapi.util.NanoleafDeviceMeta
import io.github.rowak.nanoleafapi.util.NanoleafSetup
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

actual class NanoleafAdapter actual constructor(
    coroutineContext: CoroutineContext,
    private val clock: Clock
) {
    private val deviceMetas = mutableSetOf<NanoleafDeviceMeta>()

    actual fun start(callback: (NanoleafDeviceMetadata) -> Unit) {
        deviceMetas.clear()

        NanoleafSetup.findNanoleafDevicesAsync(object : NanoleafSearchCallback {
            override fun onDeviceFound(meta: NanoleafDeviceMeta) {
                println("Found nanoleaf device: ${meta.deviceId} ${meta.deviceName} at ${meta.hostName}:${meta.port}")
                val deviceMeta = NanoleafDeviceMetadataData(meta.hostName, meta.port, meta.deviceId, meta.deviceName)
                callback(deviceMeta)
            }

            override fun onTimeout() {
                println("timeout!")
            }
        }, 10_000)
    }

    actual fun stop() {
    }

    actual fun getAccessToken(deviceMetadata: NanoleafDeviceMetadata): String {
        return NanoleafSetup.createAccessToken(deviceMetadata.hostName, deviceMetadata.port)
    }

    actual fun openDevice(
        deviceMetadata: NanoleafDeviceMetadata,
        accessToken: String
    ): baaahs.controller.NanoleafDevice {
        return JvmNanoleafDevice(
            deviceMetadata.hostName, deviceMetadata.port, deviceMetadata.deviceId, deviceMetadata.deviceName,
            accessToken
        )
    }

    inner class JvmNanoleafDevice(
        override val hostName: String,
        override val port: Int,
        override val deviceId: String,
        override val deviceName: String,
        val accessToken: String
    ) : baaahs.controller.NanoleafDevice {
        //    val accessToken = "xI2UVHZzcbuWWMIbig1Zv9NhajekW0oC"
        val device: NanoleafDevice = NanoleafDevice.createDevice(hostName, port, accessToken)
            .also { it.enableExternalStreaming() }
        init {
            println("device.globalOrientation = ${device.globalOrientation}")
        }
        val devicePanels = device.panelsRotated
        override val panels = devicePanels.map {
            NanoleafPanel(it.id, it.x, it.y, it.orientation, it.shape.toString())
        }
        private var lastFrameSentAtMillis: Instant? = null
        private var consecutiveFramesSent: Int = 0

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            pixelFormat: PixelFormat,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            if (bytesPerComponent != 3) error("Expected 3 bytes per component but got $bytesPerComponent")

            val nowMillis = clock.now()
            val elapsedMillis = lastFrameSentAtMillis?.let { nowMillis - it } ?: 0.milliseconds
            if (elapsedMillis < minMillisBetweenFrames) {
                logger.warn { "Dropping frame for $deviceId after $consecutiveFramesSent successfully sent," +
                        " last frame was ${elapsedMillis}ms ago." }
                consecutiveFramesSent = 0
                return
            }
            lastFrameSentAtMillis = nowMillis
            consecutiveFramesSent++

            val componentRange = 0 until componentCount
            val out = ByteArrayWriter()
            for (i in componentRange) fn(i, out)

            val reader = out.reader()
            val builder = StaticEffect.Builder(devicePanels)
            for (i in componentRange) {
                reader.offset = i * 3
                pixelFormat.readColorInts(reader) { r: Int, g: Int, b: Int ->
                    builder.setPanel(panels[i].id, Frame(r, g, b, 1))
                }
            }
            device.sendStaticEffectExternalStreaming(builder.build("sparklemotion!"))

//            val sendComponents = if (shuffleSend) componentRange.toList().shuffled() else componentRange
//            for (i in sendComponents) {
//                reader.offset = i * 3
//                pixelFormat.readColorInts(reader) { r: Int, g: Int, b: Int ->
//                    device.setPanelExternalStreaming(panels[i].id, r, g, b, 1)
//                }
//            }
        }

        init {
            println("token=${accessToken}")

            for (panel in panels) {
                println("panel = ${panel.id} ${panel.x} ${panel.y} ${panel.orientation} ${panel.shape}")
            }
        }
    }

    companion object {
        val minMillisBetweenFrames = 50.milliseconds
        const val shuffleSend = true
        private val logger = Logger<NanoleafAdapter>()
    }
}
