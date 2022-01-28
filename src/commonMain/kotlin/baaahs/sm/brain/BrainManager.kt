package baaahs.sm.brain

import baaahs.Color
import baaahs.Pinky
import baaahs.PubSub
import baaahs.controller.BaseControllerManager
import baaahs.controller.Controller
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.TransportConfig
import baaahs.model.Model
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.publishProperty
import baaahs.scene.ControllerConfig
import baaahs.shaders.PixelBrainShader
import baaahs.sm.brain.proto.*
import baaahs.sm.webapi.Topics
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
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
    pubSub: PubSub.IServer,
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

    private var brainData by publishProperty(pubSub, Topics.brains, emptyMap())

    val brainCount: Int
        get() = activeBrains.size

    fun listenForMapperMessages(handler: (MapperHelloMessage) -> Unit) {
        mapperMessageCallback = handler
    }

    override fun start() {
        isStartedUp = true
    }

    override fun onConfigChange(controllerConfigs: Map<String, ControllerConfig>) {
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

        val controller = BrainController(brainAddress, brainId, isSimulatedBrain, msg)
        activeBrains[brainId] = controller
        notifyListeners { onAdd(controller) }

        brainData.toMutableMap().apply {
            this[msg.brainId] = BrainInfo(
                brainId, brainAddress.asString(), null, 0, 0,
                BrainInfo.Status.Online, clock.now()
            )
        }
        brainData
    }

    override fun logStatus() {
        logger.info { "Sending to $brainCount brains." }
    }

    inner class BrainController(
        private val brainAddress: Network.Address,
        private val brainId: BrainId,
        private val isSimulatedBrain: Boolean,
        private val msg: BrainHelloMessage
    ) : Controller {
        override val controllerId: ControllerId
            get() = brainId.asControllerId()

        override val fixtureMapping: FixtureMapping
            get() = FixtureMapping(null, defaultPixelCount, null, defaultFixtureConfig)

        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?,
            pixelCount: Int
        ): Transport {
            return BrainTransport(this, brainAddress, brainId, isSimulatedBrain)
        }

        override fun getAnonymousFixtureMappings(): List<FixtureMapping> {
            return listOf(FixtureMapping(null, defaultPixelCount, null, defaultFixtureConfig))
        }
    }

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
        override val controllerId: ControllerId
            get() = brainId.asControllerId()

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
                pixelBuffer.colors[i] = Color(colorBytes[0], colorBytes[1], colorBytes[2])
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

    companion object {
        const val controllerTypeName: String = "Brain"
        const val defaultPixelCount = 2048
        private val defaultFixtureConfig = PixelArrayDevice.Config(
            defaultPixelCount,
            PixelArrayDevice.PixelFormat.RGB8,
            1f,
            LinearSurfacePixelStrategy()
        )

        private val logger = Logger<BrainManager>()
        private val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.DIRECT_RGB)
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

@Serializable(with = BrainIdSerializer::class)
data class BrainId(val uuid: String) {
    fun asControllerId(): ControllerId = ControllerId(BrainManager.controllerTypeName, uuid)
}

class BrainIdSerializer : KSerializer<BrainId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BrainId", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): BrainId = BrainId(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: BrainId) = encoder.encodeString(value.uuid)
}