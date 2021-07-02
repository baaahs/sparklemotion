package baaahs

import baaahs.fixtures.*
import baaahs.geom.Vector2F
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.mapper.ControllerId
import baaahs.mapper.MappingResults
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelBrainShader
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asMillis
import baaahs.visualizer.remote.RemoteVisualizable
import baaahs.visualizer.remote.RemoteVisualizerServer
import baaahs.visualizer.remote.RemoteVisualizers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class BrainManager(
    private val fixtureManager: FixtureManager,
    private val firmwareDaddy: FirmwareDaddy,
    private val mappingResults: MappingResults,
    private val udpSocket: Network.UdpSocket,
    private val networkStats: Pinky.NetworkStats,
    private val clock: Clock,
    pubSub: PubSub.IServer
) : RemoteVisualizable {
    internal val activeBrains: MutableMap<BrainId, BrainTransport> = mutableMapOf()
    private val pendingBrains: MutableMap<BrainId, BrainTransport> = mutableMapOf()
    private val remoteVisualizers = RemoteVisualizers()

    private var brainData by publishProperty(pubSub, Topics.brains, emptyMap())

    val brainCount: Int
        get() = activeBrains.size

    /**
     * Incorporate any pending brain changes.
     *
     * @return true if anything changed.
     */
    fun updateFixtures(): Boolean {
        if (pendingBrains.isEmpty())
            return false

        val fixturesToAdd = mutableListOf<Fixture>()
        val fixturesToRemove = mutableListOf<Fixture>()
        val newBrainData = brainData.toMutableMap()

        pendingBrains.forEach { (brainId, incomingBrainTransport) ->
            val priorBrainTransport = activeBrains[brainId]
            if (priorBrainTransport != null) {
                fixturesToRemove.add(priorBrainTransport.fixture)
            }

            if (incomingBrainTransport.hadException) {
                // Existing Brain has had exceptions so we're forgetting about it.
                activeBrains.remove(brainId)
                newBrainData.remove(brainId.uuid)
            } else {
                val fixture = incomingBrainTransport.fixture

                fixturesToAdd.add(fixture)
                activeBrains[brainId] = incomingBrainTransport

                newBrainData[brainId.uuid] = BrainInfo(
                    brainId,
                    incomingBrainTransport.brainAddress.asString(),
                    fixture.modelEntity?.name,
                    fixture.pixelCount,
                    fixture.pixelLocations.size,
                    BrainInfo.Status.Online,
                    clock.now()
                )
            }
        }

        fixtureManager.fixturesChanged(fixturesToAdd, fixturesToRemove)
        fixturesToAdd.forEach { remoteVisualizers.sendFixtureInfo(it) }

        pendingBrains.clear()
        brainData = newBrainData

        return true
    }

    fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage,
        isSimulatedBrain: Boolean = false
    ) {
        val brainId = BrainId(msg.brainId)

        logger.debug {
            "Hello from ${brainId} (surface=${msg.surfaceName ?: "[unknown]"}) at $brainAddress"
        }

        // Decide whether or not to tell this brain it should use a different firmware
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.debug {
                "The firmware daddy doesn't like $brainId" +
                        " (${mappingResults.dataForController(brainId.asControllerId())?.entity?.name ?: "[unknown]"})" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }

        val transport = BrainTransport(brainAddress, brainId, isSimulatedBrain, msg)
        val fixture = transport.fixture

        fixture.modelEntity?.let { modelSurface ->
            if (msg.surfaceName != modelSurface.name) {
                logger.debug {
                    "Sending BrainMappingMessage to $brainId, " +
                            "identified as ${modelSurface.name} with ${fixture.pixelCount} pixels"
                }
                val mappingMsg = BrainMappingMessage(
                    brainId, modelSurface.name, null, Vector2F(0f, 0f),
                    Vector2F(0f, 0f), fixture.pixelCount, fixture.pixelLocations
                )
                udpSocket.sendUdp(brainAddress, Ports.BRAIN, mappingMsg)
            } else {
                logger.debug {
                    "Not sending BrainMappingMessage to $brainId, its mapping is already correct (${modelSurface.name})."
                }
            }
        }

        val priorBrainTransport = activeBrains[brainId]
        if (priorBrainTransport != null) {
            if (priorBrainTransport.fixture.modelEntity == fixture.modelEntity) {
                // Duplicate packet?
                logger.debug {
                    "Ignore hello from ${priorBrainTransport.brainId} (${priorBrainTransport.fixture.title}), " +
                            "duplicate packet?"
                }
                return
            }

//            logger.debug(
//                "Remapping ${priorBrainInfo.brainId} from ${priorBrainInfo.surface.describe()} ->" +
//                        " ${surface.describe()}"
//            )
        }

        pendingBrains[brainId] = transport
    }

    fun createFixtureFor(msg: BrainHelloMessage, transport: Transport): Fixture {
        val controllerId = BrainId(msg.brainId).asControllerId()
        return fixtureManager.createFixtureFor(controllerId, msg.surfaceName, transport)
    }

    inner class BrainTransport(
        internal val brainAddress: Network.Address,
        val brainId: BrainId,
        private val isSimulatedBrain: Boolean,
        msg: BrainHelloMessage,
        val firmwareVersion: String? = null,
        val idfVersion: String? = null
    ) : Transport {
        val fixture: Fixture = createFixtureFor(msg, this)

        var hadException: Boolean = false
            private set

        private var pixelBuffer = pixelShader.createBuffer(0)

        override val name: String
            get() = "Brain ${brainId.uuid} at $brainAddress"

        override fun send(fixture: Fixture, resultViews: List<ResultView>) {
            val resultColors =
                PixelArrayDevice.getColorResults(resultViews)

            if (resultColors.pixelCount != pixelBuffer.colors.size) {
                pixelBuffer = pixelShader.createBuffer(resultColors.pixelCount)
            }

            pixelBuffer.indices.forEach { i ->
                pixelBuffer.colors[i] = resultColors[i]
            }
            val message = BrainShaderMessage(pixelBuffer.brainShader, pixelBuffer).toBytes()
            try {
                if (!isSimulatedBrain)
                    udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)
            } catch (e: Exception) {
                // Couldn't send to Brain? Schedule to remove it.
                hadException = true
                pendingBrains[brainId] = this

                logger.error(e) { "Error sending to $brainId, will take offline" }
            }

            networkStats.packetsSent++
            networkStats.bytesSent += message.size

            remoteVisualizers.sendFrameData(fixture) { outBuf ->
                val colors = pixelBuffer.colors
                outBuf.writeInt(colors.size)
                colors.forEach { color -> color.serializeWithoutAlpha(outBuf) }
            }
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

    override fun addRemoteVisualizer(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.addListener(listener)

        activeBrains.values.forEach {
            listener.sendFixtureInfo(it.fixture)
        }
    }

    override fun removeRemoteVisualizer(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.removeListener(listener)
    }

    companion object {
        val controllerTypeName: String = "Brain"

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