package baaahs.sm.brain

import baaahs.Color
import baaahs.Pinky
import baaahs.controller.*
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.fixtures.*
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.scene.*
import baaahs.shaders.PixelBrainShader
import baaahs.sm.brain.proto.*
import baaahs.ui.View
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.unixMillis
import baaahs.visualizer.entity.visualizerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

class BrainManager(
    private val firmwareDaddy: FirmwareDaddy,
    private val link: Network.Link,
    private val networkStats: Pinky.NetworkStats,
    private val clock: Clock,
    coroutineContext: CoroutineContext
) : BaseControllerManager<BrainManager.BrainController, BrainControllerConfig, BrainManager.BrainState>(controllerTypeName) {
    private var isStartedUp = false
    private var mapperMessageCallback: ((MapperHelloMessage) -> Unit)? = null

    private val udpSocket = link.listenFragmentingUdp(Ports.PINKY, object : Network.UdpListener {
        override suspend fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
            if (!isStartedUp) return

            val message = parse(bytes)
            CoroutineScope(coroutineContext).launch {
                when (message) {
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

    override fun onChange(
        controllerId: ControllerId,
        oldController: BrainController?,
        controllerConfig: Change<BrainControllerConfig?>,
        controllerState: Change<BrainState?>,
        fixtureMappings: Change<List<FixtureMapping>>
    ): BrainController? {
        val newConfig = controllerConfig.newValue
        val newState = controllerState.newValue

        val brainId = BrainId(controllerId.id)
        val address = newState?.address ?: newConfig?.address ?: return null

        if (newState?.isGoingOffline == true) {
            activeBrains.remove(brainId)
            return null
        }

        return BrainController(
            link.createAddress(address), brainId,
            newState?.firmwareVersion,
            newConfig?.defaultFixtureOptions,
            newConfig?.defaultTransportConfig,
            newState?.isSimulatedBrain == true
        ).also { activeBrains[brainId] = it }
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

        logger.info {
            "Hello from $brainId (surface=${msg.surfaceName ?: "[unknown]"}) " +
                    "at $brainAddress [firmware=${msg.firmwareVersion}]"
        }

        // Decide whether to tell this brain it should use a different firmware
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.warn {
                "The firmware daddy doesn't like $brainId" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }

        val existingController = activeBrains[brainId]
        if (existingController != null && existingController.lastError == null && existingController.brainAddress == brainAddress) {
            // Duplicate packet?
            logger.warn { "Ignore hello from ${existingController.controllerId} @ $brainAddress, duplicate packet?" }
            return
        } else if (existingController != null) {
            // Refresh this brain as its address may have changed
            // TODO(kcking): could we instead refresh the brain's transport so we don't have to deal with re-initializing the fixture/controller?
            removeBrain(brainId)
        }

        onStateChange(brainId.asControllerId()) { fromState ->
            BrainState(
                brainId.uuid, brainAddress.asString(), clock.now(),
                msg.firmwareVersion, null, null, isSimulatedBrain
            )
        }
    }

    fun removeBrain(brainId: BrainId) {
        onStateChange(brainId.asControllerId()) { fromState ->
            fromState?.copy(isGoingOffline = true)
        }
    }

    inner class BrainController(
        val brainAddress: Network.Address,
        private val brainId: BrainId,
        val firmwareVersion: String?,
        override val defaultFixtureOptions: FixtureOptions?,
        override val defaultTransportConfig: TransportConfig?,
        private val isSimulatedBrain: Boolean
    ) : Controller {
        override val controllerId: ControllerId
            get() = brainId.asControllerId()

        var lastError: Exception? = null
            internal set
        var lastErrorAt: Instant? = null
            internal set

        override val transportType: TransportType
            get() = BrainTransportType

        override fun createFixtureResolver(): FixtureResolver = object : FixtureResolver {
            override fun createTransport(
                entity: Model.Entity?,
                fixtureConfig: FixtureConfig,
                transportConfig: TransportConfig?
            ): Transport = BrainTransport(
                this@BrainController, brainAddress, brainId, isSimulatedBrain,
                transportConfig = transportConfig
            )
        }

        override fun getAnonymousFixtureMappings(): List<FixtureMapping> {
            return listOf(FixtureMapping(
                null,
                BrainManager.defaultFixtureOptions
            ))
        }
        public fun getAddress(): Network.Address {
          return this.brainAddress
        }
    }

    @Serializable
    data class BrainState(
        override val title: String,
        override val address: String?,
        override val onlineSince: Instant?,
        override val firmwareVersion: String?,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Instant? = null,
        val isSimulatedBrain: Boolean = false,
        val isGoingOffline: Boolean = false,
    ) : ControllerState()

    inner class BrainTransport(
        private val brainController: BrainController,
        internal val brainAddress: Network.Address,
        val brainId: BrainId,
        private val isSimulatedBrain: Boolean,
        val firmwareVersion: String? = null,
        val idfVersion: String? = null,
        val transportConfig: TransportConfig?
    ) : Transport {
        private var pixelBuffer = pixelShader.createBuffer(0)
        private val pixelOffset = 0

        override val name: String
            get() = "Brain ${brainId.uuid} at $brainAddress"
        override val controller: Controller
            get() = brainController
        override val config: TransportConfig?
            get() = transportConfig

        override fun deliverBytes(byteArray: ByteArray) {
            val pixelCount = byteArray.size / 3

            if (pixelCount != pixelBuffer.colors.size) {
                pixelBuffer = pixelShader.createBuffer(pixelCount + pixelOffset)
            }

            for (i in 0 until pixelCount) {
                val j = i * 3
                pixelBuffer.colors[i + pixelOffset * 3] = Color(byteArray[j], byteArray[j + 1], byteArray[j + 2])
            }

            deliverShaderMessage()
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            if (componentCount != pixelBuffer.colors.size) {
                pixelBuffer = pixelShader.createBuffer(componentCount + pixelOffset)
            }

            val buf = ByteArrayWriter(bytesPerComponent)
            for (i in 0 until componentCount) {
                buf.offset = 0
                fn(i, buf)
                val colorBytes = buf.toBytes()

                // Using Color's int constructor fixes a bug in Safari causing
                // color values above 127 to be treated as 0. Untested. :-(
                pixelBuffer.colors[i + pixelOffset] = Color(
                    colorBytes[0].toInt() and 0xff,
                    colorBytes[1].toInt() and 0xff,
                    colorBytes[2].toInt() and 0xff)
            }

            deliverShaderMessage()
        }

        private fun deliverShaderMessage() {
            val message = BrainShaderMessage(pixelBuffer.brainShader, pixelBuffer).toBytes()
            val now = clock.now()
            if (brainController.lastErrorAt?.let { (now - it) >= waitPeriodAfterNetworkError } ?: true) {
                try {
                    if (!isSimulatedBrain)
                        udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)
                } catch (e: Exception) {
                    // Couldn't send to Brain? Schedule to remove it.
                    onStateChange(brainId.asControllerId()) { fromState ->
                        fromState?.copy(
                            lastErrorMessage = e.message,
                            lastErrorAt = now
                        )
                    }
                    //                pendingBrains[brainId] = this

                    logger.error(e) { "Error sending to $brainId, will take offline" }
                }

                networkStats.packetsSent++
                networkStats.bytesSent += message.size
            }
        }
    }

    /** If we want a pong back from a [BrainShaderMessage], send this. */
    private fun generatePongPayload(): ByteArray {
        return ByteArrayWriter().apply {
            writeLong(clock.now().unixMillis)
        }.toBytes()
    }

    fun receivedPing(fromAddress: Network.Address, message: PingMessage) {
        if (message.isPong) {
            val originalSentAt = ByteArrayReader(message.data).readLong()
            val elapsedMs = clock.now().unixMillis - originalSentAt
            logger.debug { "Shader pong from $fromAddress took ${elapsedMs}ms" }
        }
    }

    companion object : ControllerManager.Meta {
        override val controllerTypeName: String = "Brain"
        override val controllerIcon: String
            get() = "baaahs-brain.svg"

        val waitPeriodAfterNetworkError = 5.seconds

        private const val defaultPixelCount = 2048
        override val defaultFixtureOptions = PixelArrayDevice.Options(
            defaultPixelCount,
            PixelFormat.RGB8,
            1f,
            LinearSurfacePixelStrategy()
        )

        private val logger = Logger<BrainManager>()
        private val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.DIRECT_RGB)

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig =
            MutableBrainControllerConfig(
                state?.title
                    ?: controllerId?.id
                    ?: "brainXXXX",
                null, null, null
            )
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
    val onlineSince: Instant
) {
    enum class Status {
        Online
    }
}

@Serializable
@SerialName("Brain")
data class BrainControllerConfig(
    override val title: String,
    val address: String? = null,
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : ControllerConfig {
    override val controllerType: String
        get() = BrainManager.controllerTypeName
    override val emptyTransportConfig: TransportConfig
        get() = BrainTransportConfig()

    override fun edit(): MutableControllerConfig =
        MutableBrainControllerConfig(
            title, address, defaultFixtureOptions?.edit(), defaultTransportConfig?.edit()
        )

    override fun createPreviewBuilder(): PreviewBuilder = object : PreviewBuilder {
        override fun createFixturePreview(fixtureOptions: FixtureOptions, transportConfig: TransportConfig): FixturePreview {
            return object : FixturePreview {
                override val fixtureOptions: ConfigPreview
                    get() = fixtureOptions.preview()
                override val transportConfig: ConfigPreview
                    get() = transportConfig.preview()
            }
        }
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

object BrainTransportType : TransportType {
    override val id: String
        get() = "Brain"
    override val title: String
        get() = "Brain"
    override val emptyConfig: TransportConfig
        get() = BrainTransportConfig()
    override val isConfigurable: Boolean
        get() = false
}

@Serializable
@SerialName("Brain")
class BrainTransportConfig() : TransportConfig {
    override val transportType: TransportType
        get() = BrainTransportType

    override fun edit(): MutableTransportConfig =
        MutableBrainTransportConfig(this)

    override fun plus(other: TransportConfig?): TransportConfig =
        this

    override fun preview(): ConfigPreview = object : ConfigPreview {
        override fun summary(): List<ConfigPreviewNugget> = emptyList()
    }
}

class MutableBrainTransportConfig(config: BrainTransportConfig) : MutableTransportConfig {
    // Nothing is configurable right now.

    override val transportType: TransportType
        get() = BrainTransportType

    override fun build(): TransportConfig =
        BrainTransportConfig()

    override fun getEditorView(
        editingController: EditingController<*>
    ): View = visualizerBuilder.getBrainTransportConfigEditorView(editingController, this)

    override fun toSummaryString(): String =
        ""
}