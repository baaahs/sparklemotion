package baaahs.sm.brain

import baaahs.Color
import baaahs.Pinky
import baaahs.controller.*
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.Transport
import baaahs.fixtures.TransportConfig
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.scene.ControllerConfig
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableBrainControllerConfig
import baaahs.scene.MutableControllerConfig
import baaahs.shaders.PixelBrainShader
import baaahs.sm.brain.proto.*
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.coroutines.CoroutineContext

class BrainManager(
    private val firmwareDaddy: FirmwareDaddy,
    link: Network.Link,
    private val networkStats: Pinky.NetworkStats,
    private val clock: Clock,
    coroutineContext: CoroutineContext
) : BaseControllerManager(controllerTypeName) {
    private var isStartedUp = false
    private var mapperMessageCallback: ((MapperHelloMessage) -> Unit)? = null

    private val udpSocket = link.listenFragmentingUdp(Ports.PINKY, object : Network.UdpListener {
        override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
            if (!isStartedUp) return

            CoroutineScope(coroutineContext).launch {
                when (val message = parse(bytes)) {
                    is BrainHelloMessage -> foundBrain(fromAddress, message)
                    is PingMessage -> receivedPing(fromAddress, message)
                    is MapperHelloMessage -> mapperMessageCallback?.invoke(message)
                }
            }
        }
    })

    internal val activeBrains: MutableMap<BrainId, BrainController> = mutableMapOf()

    val brainCount: Int
        get() = activeBrains.size

    fun listenForMapperMessages(handler: (MapperHelloMessage) -> Unit) {
        mapperMessageCallback = handler
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>) {
    }

    override fun start() {
        isStartedUp = true
    }

    override fun stop() {
        TODO("not implemented")
    }

    fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage,
        isSimulatedBrain: Boolean = false
    ) {
        val brainId = BrainId(msg.brainId)

        logger.debug {
            "Hello from $brainId (surface=${msg.surfaceName ?: "[unknown]"}) at $brainAddress"
        }

        // Decide whether or not to tell this brain it should use a different firmware
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.debug {
                "The firmware daddy doesn't like $brainId" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }

        val existingController = activeBrains[brainId]
        if (existingController != null) {
            // Duplicate packet?
            logger.debug { "Ignore hello from ${existingController.controllerId}, duplicate packet?" }
            return
        }

        val controller = BrainController(brainAddress, brainId, isSimulatedBrain)
        activeBrains[brainId] = controller
        notifyListeners { onAdd(controller) }
    }

    inner class BrainController(
        private val brainAddress: Network.Address,
        private val brainId: BrainId,
        private val isSimulatedBrain: Boolean,
        private val startedAt: Time = clock.now()
    ) : Controller {
        override val controllerId: ControllerId
            get() = brainId.asControllerId()

        override val state: ControllerState
            get() = State(brainId.uuid, brainAddress.asString(), startedAt)
        override val defaultFixtureConfig: FixtureConfig
            get() = BrainManager.defaultFixtureConfig

        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?,
            pixelCount: Int
        ): Transport {
            return BrainTransport(this, brainAddress, brainId, isSimulatedBrain)
        }

        override fun getAnonymousFixtureMappings(): List<FixtureMapping> {
            return listOf(FixtureMapping(
                null,
                fixtureType = PixelArrayDevice,
                BrainManager.defaultFixtureConfig,
                pixelLocations = null
            ))
        }
    }

    @Serializable
    data class State(
        override val title: String,
        override val address: String?,
        override val onlineSince: Time?
    ) : ControllerState()

    inner class BrainTransport(
        private val brainController: BrainController,
        internal val brainAddress: Network.Address,
        val brainId: BrainId,
        private val isSimulatedBrain: Boolean,
        val firmwareVersion: String? = null,
        val idfVersion: String? = null
    ) : Transport {
        var hadException: Boolean = false
            private set

        private var pixelBuffer = pixelShader.createBuffer(0)

        override val name: String
            get() = "Brain ${brainId.uuid} at $brainAddress"
        override val controller: Controller
            get() = brainController

        override fun deliverBytes(byteArray: ByteArray) {
            val pixelCount = byteArray.size / 3

            if (pixelCount != pixelBuffer.colors.size) {
                pixelBuffer = pixelShader.createBuffer(pixelCount)
            }

            for (i in 0 until pixelCount) {
                val j = i * 3
                pixelBuffer.colors[i] = Color(byteArray[j], byteArray[j + 1], byteArray[j + 2])
            }

            deliverShaderMessage()
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            if (componentCount != pixelBuffer.colors.size) {
                pixelBuffer = pixelShader.createBuffer(componentCount)
            }

            val buf = ByteArrayWriter(bytesPerComponent)
            for (i in 0 until componentCount) {
                buf.offset = 0
                fn(i, buf)
                val colorBytes = buf.toBytes()

                // Using Color's int constructor fixes a bug in Safari causing
                // color values above 127 to be treated as 0. Untested. :-(
                pixelBuffer.colors[i] = Color(
                    colorBytes[0].toInt() and 0xff,
                    colorBytes[1].toInt() and 0xff,
                    colorBytes[2].toInt() and 0xff)
            }

            deliverShaderMessage()
        }

        private fun deliverShaderMessage() {
            val message = BrainShaderMessage(pixelBuffer.brainShader, pixelBuffer).toBytes()
            try {
                if (!isSimulatedBrain)
                    udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)
            } catch (e: Exception) {
                // Couldn't send to Brain? Schedule to remove it.
                hadException = true
                notifyListeners { onError(brainController) }
                //                pendingBrains[brainId] = this

                logger.error(e) { "Error sending to $brainId, will take offline" }
            }

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }
    }

    /** If we want a pong back from a [BrainShaderMessage], send this. */
    private fun generatePongPayload(): ByteArray {
        return ByteArrayWriter().apply {
            writeLong(clock.now().asMillis())
        }.toBytes()
    }

    fun receivedPing(fromAddress: Network.Address, message: PingMessage) {
        if (message.isPong) {
            val originalSentAt = ByteArrayReader(message.data).readLong()
            val elapsedMs = clock.now().asMillis() - originalSentAt
            logger.debug { "Shader pong from $fromAddress took ${elapsedMs}ms" }
        }
    }

    companion object : ControllerManager.MetaManager {
        override val controllerTypeName: String = "Brain"

        const val defaultPixelCount = 2048
        private val defaultFixtureConfig = PixelArrayDevice.Config(
            defaultPixelCount,
            PixelArrayDevice.PixelFormat.RGB8,
            1f,
            LinearSurfacePixelStrategy()
        )

        private val logger = Logger<BrainManager>()
        private val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.DIRECT_RGB)

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            val title = state?.title
                ?: controllerId?.id
                ?: "brainXXXX"
            return MutableBrainControllerConfig(BrainControllerConfig(title))
        }
    }
}

@Serializable
data class BrainInfo(
    val id: BrainId,
    val address: String,
    val modelEntity: String?,
    val pixelCount: Int,
    val mappedPixelCount: Int,
    val status: Status,
    val onlineSince: Time
) {
    enum class Status {
        Online
    }
}

@Serializable
@SerialName("Brain")
data class BrainControllerConfig(
    override val title: String,
    override val fixtures: List<FixtureMappingData> = emptyList()
) : ControllerConfig {
    override val controllerType: String
        get() = BrainManager.controllerTypeName

    override fun edit(): MutableControllerConfig =
        MutableBrainControllerConfig(this)
}

@Serializable(with = BrainIdSerializer::class)
data class BrainId(val uuid: String) {
    fun asControllerId(): ControllerId = ControllerId(BrainManager.controllerTypeName, uuid)
}

class BrainIdSerializer : KSerializer<BrainId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BrainId", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): BrainId = BrainId(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: BrainId) = encoder.encodeString(value.uuid)
}