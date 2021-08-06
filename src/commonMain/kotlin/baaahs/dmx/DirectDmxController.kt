package baaahs.dmx

import baaahs.controller.Controller
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
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
        val buffer = run {
            transportConfig as DmxTransportConfig
            val (start, end) = transportConfig
            universe.writer(start, end - start)
        }

        override val name: String
            get() = "DMX Transport"

        override fun deliverBytes(byteArray: ByteArray) {
//            if (byteArray.size > buffer.)
            byteArray.forEachIndexed { i, byte -> buffer[i] = byte }
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