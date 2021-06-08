package baaahs

import baaahs.fixtures.*
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.SurfacePixelStrategy
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.mapper.MappingResults
import baaahs.model.Model
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelBrainShader
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.asMillis
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
    private val model: Model,
    private val mappingResults: MappingResults,
    private val udpSocket: Network.UdpSocket,
    private val networkStats: Pinky.NetworkStats,
    private val clock: Clock,
    private val surfacePixelStrategy: SurfacePixelStrategy = LinearSurfacePixelStrategy()
) {
    internal val activeBrains: MutableMap<BrainId, BrainTransport> = mutableMapOf()
    private val pendingBrains: MutableMap<BrainId, BrainTransport> = mutableMapOf()
    private val listeningVisualizers = hashSetOf<Pinky.ListeningVisualizer>()

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

        pendingBrains.forEach { (brainId, incomingBrainTransport) ->
            val priorBrainTransport = activeBrains[brainId]
            if (priorBrainTransport != null) {
                fixturesToRemove.add(priorBrainTransport.fixture)
            }

            if (incomingBrainTransport.hadException) {
                // Existing Brain has had exceptions so we're forgetting about it.
                activeBrains.remove(brainId)
            } else {
                fixturesToAdd.add(incomingBrainTransport.fixture)
                activeBrains[brainId] = incomingBrainTransport
            }
        }

        fixtureManager.fixturesChanged(fixturesToAdd, fixturesToRemove)
        listeningVisualizers.forEach { listeningVisualizer ->
            fixturesToAdd.forEach {
                listeningVisualizer.sendPixelData(it)
            }
        }

        pendingBrains.clear()

        return true
    }

    fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage,
        isSimulatedBrain: Boolean = false
    ) {
        val brainId = BrainId(msg.brainId)

        logger.debug {
            "Hello from ${brainId.uuid} (surface=${msg.surfaceName ?: "[unknown]"}) at $brainAddress"
        }

        // Decide whether or not to tell this brain it should use a different firmware
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.debug {
                "The firmware daddy doesn't like $brainId" +
                        " (${mappingResults.dataFor(brainId)?.surface?.name ?: "[unknown]"})" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }

        val transport = BrainTransport(brainAddress, brainId, isSimulatedBrain, msg.firmwareVersion, msg.idfVersion)
        val fixture = createFixtureFor(msg, transport)
            .also { transport.fixture = it }

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
        val brainId = BrainId(msg.brainId)

        val mappingData = mappingResults.dataFor(brainId)
            ?: mappingResults.dataFor(msg.surfaceName ?: "__nope")
            ?: msg.surfaceName?.let { MappingResults.Info(model.findSurface(it), null) }

        val modelSurface = mappingData?.surface
        val pixelCount = mappingData?.pixelLocations?.size
            ?: modelSurface?.expectedPixelCount
            ?: SparkleMotion.MAX_PIXEL_COUNT
        val pixelLocations = mappingData?.pixelLocations?.map { it ?: Vector3F(0f, 0f, 0f) }
            ?: surfacePixelStrategy.forFixture(pixelCount, modelSurface, model)

        return Fixture(modelSurface, pixelCount, pixelLocations, PixelArrayDevice, transport = transport)
    }

    inner class BrainTransport(
        private val brainAddress: Network.Address,
        val brainId: BrainId,
        private val isSimulatedBrain: Boolean,
        val firmwareVersion: String? = null,
        val idfVersion: String? = null
    ) : Transport {
        // This is weirdly circular. :-/
        lateinit var fixture: Fixture

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

            updateListeningVisualizers(fixture, pixelBuffer.colors)
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

    fun addListeningVisualizer(listeningVisualizer: Pinky.ListeningVisualizer) {
        listeningVisualizers.add(listeningVisualizer)

        activeBrains.values.forEach { listeningVisualizer.sendPixelData(it.fixture) }
    }

    fun removeListeningVisualizer(listeningVisualizer: Pinky.ListeningVisualizer) {
        listeningVisualizers.remove(listeningVisualizer)
    }

    private fun updateListeningVisualizers(fixture: Fixture, colors: List<Color>) {
        if (listeningVisualizers.isNotEmpty()) {
            listeningVisualizers.forEach {
                it.sendFrame(fixture, colors)
            }
        }
    }

    companion object {
        private val logger = Logger<BrainManager>()
        private val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.DIRECT_RGB)
    }
}

@Serializable(with = BrainIdSerializer::class)
data class BrainId(val uuid: String)

class BrainIdSerializer : KSerializer<BrainId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BrainId", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): BrainId = BrainId(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: BrainId) = encoder.encodeString(value.uuid)
}