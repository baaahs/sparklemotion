package baaahs.dmx

import baaahs.controller.Controller
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayWriter
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.TransportConfig
import baaahs.model.Model

class DirectDmxController(private val device: Dmx.Device) : Controller {
    override val controllerId: ControllerId
        get() = ControllerId("DMX", device.id)
    override val fixtureMapping: FixtureMapping?
        get() = null
    private val universe = device.asUniverse()

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?,
        pixelCount: Int
    ): Transport = object : Transport {
        private val buffer = run {
            transportConfig as DmxTransportConfig
            val (start, end) = transportConfig
            universe.writer(start, end - start)
        }

        override val name: String
            get() = "DMX Transport"

        override fun deliverBytes(byteArray: ByteArray) {
            byteArray.forEachIndexed { i, byte -> buffer[i] = byte }
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            val buf = ByteArrayWriter(bytesPerComponent)
            for (componentIndex in 0 until componentCount) {
                buf.offset = 0
                fn(componentIndex, buf)

                val bytes = buf.toBytes()
                val startChannel = componentCount * bytesPerComponent
                for (i in 0 until bytesPerComponent) {
                    buffer[startChannel + i] = bytes[i]
                }
            }
        }
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = emptyList()
}

/**
 * @param startChannel Zero-based.
 * @param endChannel Zero-based.
 */
data class DmxTransportConfig(
    val startChannel: Int,
    val endChannel: Int
) : TransportConfig