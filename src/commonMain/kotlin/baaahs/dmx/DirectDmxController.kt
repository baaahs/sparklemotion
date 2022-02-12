package baaahs.dmx

import baaahs.controller.Controller
import baaahs.controller.ControllerId
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayWriter
import baaahs.mapper.FixtureMapping
import baaahs.mapper.TransportConfig
import baaahs.model.Model
import baaahs.scene.ControllerConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class DirectDmxController(private val device: Dmx.Device) : Controller {
    override val controllerId: ControllerId
        get() = ControllerId(controllerType, device.id)
    override val fixtureMapping: FixtureMapping?
        get() = null
    private val universe = device.asUniverse()

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?,
        pixelCount: Int
    ): Transport = DirectDmxTransport(transportConfig as DirectDmxTransportConfig)

    inner class DirectDmxTransport(private val transportConfig: DirectDmxTransportConfig) : Transport {
        private val buffer = run {
            val (start, end) = transportConfig
            universe.writer(start, end - start + 1)
        }

        override val name: String
            get() = "DMX Transport"
        override val controller: Controller
            get() = this@DirectDmxController
        override val config: TransportConfig = transportConfig

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
                val startChannel = componentIndex * bytesPerComponent
                for (i in 0 until bytesPerComponent) {
                    buffer[startChannel + i] = bytes[i]
                }
            }
        }
    }

    override fun afterFrame() {
        universe.sendFrame()
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = emptyList()

    companion object {
        val controllerType = "DMX"
    }
}

@Serializable
@SerialName("DirectDMX")
class DirectDmxControllerConfig(
    override val controllerType: String = DirectDmxController.controllerType,
    override val title: String = "Direct DMX"
) : ControllerConfig

/**
 * @param startChannel Zero-based.
 * @param endChannel Zero-based.
 */
@Serializable
@SerialName("DirectDMX")
data class DirectDmxTransportConfig(
    val startChannel: Int,
    val endChannel: Int
) : TransportConfig