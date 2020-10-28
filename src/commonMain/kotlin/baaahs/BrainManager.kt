package baaahs

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
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

class BrainManager(
    private val fixtureManager: FixtureManager,
    private val firmwareDaddy: FirmwareDaddy,
    private val model: Model<*>,
    private val mappingResults: MappingResults,
    private val udpSocket: Network.UdpSocket,
    private val networkStats: Pinky.NetworkStats,
    private val surfacePixelStrategy: SurfacePixelStrategy = LinearSurfacePixelStrategy()
) {
    internal val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val listeningVisualizers = hashSetOf<Pinky.ListeningVisualizer>()

    val brainCount: Int
        get() = brainInfos.size

    /**
     * Incorporate any pending brain changes.
     *
     * @return true if anything changed.
     */
    fun updateFixtures(): Boolean {
        if (pendingBrainInfos.isEmpty())
            return false

        val brainFixturesToRemove = mutableListOf<ShowRunner.FixtureReceiver>()
        val brainFixturesToAdd = mutableListOf<ShowRunner.FixtureReceiver>()

        pendingBrainInfos.forEach { (brainId, incomingBrainInfo) ->
            val priorBrainInfo = brainInfos[brainId]
            if (priorBrainInfo != null) {
                brainFixturesToRemove.add(priorBrainInfo.fixtureReceiver)
            }

            if (incomingBrainInfo.hadException) {
                // Existing Brain has had exceptions so we're forgetting about it.
                brainInfos.remove(brainId)
            } else {
                brainFixturesToAdd.add(incomingBrainInfo.fixtureReceiver)
                brainInfos[brainId] = incomingBrainInfo
            }
        }

        fixtureManager.fixturesChanged(brainFixturesToAdd, brainFixturesToRemove)
        listeningVisualizers.forEach { listeningVisualizer ->
            brainFixturesToAdd.forEach {
                listeningVisualizer.sendPixelData(it.fixture)
            }
        }

        pendingBrainInfos.clear()

        return true
    }

    fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage,
        isSimulatedBrain: Boolean = false
    ) {
        val brainId = BrainId(msg.brainId)

        Pinky.logger.debug {
            "Hello from ${brainId.uuid}" +
                    " (${mappingResults.dataFor(brainId)?.surface?.name ?: "[unknown]"})" +
                    " at $brainAddress: $msg"
        }
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            Pinky.logger.debug {
                "The firmware daddy doesn't like $brainId" +
                        " (${mappingResults.dataFor(brainId)?.surface?.name ?: "[unknown]"})" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }


        // println("Heard from brain $brainId at $brainAddress for $surfaceName")
        val fixture = createFixtureFor(msg)

        fixture.modelSurface?.let { modelSurface ->
            if (msg.surfaceName != modelSurface.name) {
                val mappingMsg = BrainMappingMessage(
                    brainId, modelSurface.name, null, Vector2F(0f, 0f),
                    Vector2F(0f, 0f), fixture.pixelCount, fixture.pixelLocations
                )
                udpSocket.sendUdp(brainAddress, Ports.BRAIN, mappingMsg)
            }
        }

        val priorBrainInfo = brainInfos[brainId]
        if (priorBrainInfo != null) {
            if (priorBrainInfo.brainId == brainId && priorBrainInfo.fixture == fixture) {
                // Duplicate packet?
//                logger.debug(
//                    "Ignore ${priorBrainInfo.brainId} ${priorBrainInfo.surface.describe()} ->" +
//                            " ${surface.describe()} because probably duplicate?"
//                )
                return
            }

//            logger.debug(
//                "Remapping ${priorBrainInfo.brainId} from ${priorBrainInfo.surface.describe()} ->" +
//                        " ${surface.describe()}"
//            )
        }

        val sendFn: (BrainShader.Buffer) -> Unit = { shaderBuffer ->
            val message = BrainShaderMessage(shaderBuffer.brainShader, shaderBuffer).toBytes()
            try {
                if (!isSimulatedBrain)
                    udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)
            } catch (e: Exception) {
                // Couldn't send to Brain? Schedule to remove it.
                val brainInfo = brainInfos[brainId]!!
                brainInfo.hadException = true
                pendingBrainInfos[brainId] = brainInfo

                Pinky.logger.error("Error sending to $brainId, will take offline", e)
            }

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }

        val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.DIRECT_RGB)
        val fixtureReceiver = object : ShowRunner.FixtureReceiver {
            override val fixture = fixture
            private val pixelBuffer = pixelShader.createBuffer(fixture)

            override fun send(pixels: Pixels) {
                pixelBuffer.indices.forEach { i ->
                    pixelBuffer.colors[i] = pixels[i]
                }
                sendFn(pixelBuffer)

                updateListeningVisualizers(fixture, pixelBuffer.colors)
            }
        }

        val brainInfo = BrainInfo(brainAddress, brainId, fixture, msg.firmwareVersion, msg.idfVersion, fixtureReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo

        // Decide whether or not to tell this brain it should use a different firmware
    }

    fun createFixtureFor(msg: BrainHelloMessage): Fixture {
        val brainId = BrainId(msg.brainId)

        val mappingData = mappingResults.dataFor(brainId)
            ?: mappingResults.dataFor(msg.surfaceName ?: "__nope")
            ?: msg.surfaceName?.let { MappingResults.Info(model.findModelSurface(it), null) }

        val modelSurface = mappingData?.surface
        val pixelCount = mappingData?.pixelLocations?.size
            ?: modelSurface?.expectedPixelCount
            ?: SparkleMotion.MAX_PIXEL_COUNT
        val pixelLocations = mappingData?.pixelLocations?.map { it ?: Vector3F(0f, 0f, 0f) }
            ?: surfacePixelStrategy.forFixture(pixelCount, modelSurface, model)

        return Fixture(modelSurface, pixelCount, pixelLocations)
    }

    /** If we want a pong back from a [BrainShaderMessage], send this. */
    private fun generatePongPayload(): ByteArray {
        return ByteArrayWriter().apply {
            writeLong(getTimeMillis())
        }.toBytes()
    }

    fun receivedPing(fromAddress: Network.Address, message: PingMessage) {
        if (message.isPong) {
            val originalSentAt = ByteArrayReader(message.data).readLong()
            val elapsedMs = getTimeMillis() - originalSentAt
            Pinky.logger.debug { "Shader pong from $fromAddress took ${elapsedMs}ms" }
        }
    }

    fun addListeningVisualizer(listeningVisualizer: Pinky.ListeningVisualizer) {
        listeningVisualizers.add(listeningVisualizer)

        brainInfos.values.forEach { listeningVisualizer.sendPixelData(it.fixture) }
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
}