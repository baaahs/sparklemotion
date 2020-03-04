package baaahs

import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.glsl.GlslBase
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.io.Fs
import baaahs.mapper.MapperEndpoint
import baaahs.mapper.MappingResults
import baaahs.mapper.Storage
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelShader
import baaahs.shaders.SoundAnalysisPlugin
import baaahs.shows.SolidColorShow
import com.soywiz.klock.DateTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import kotlin.math.min

class Pinky(
    val model: Model<*>,
    val shows: List<Show>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val beatSource: BeatSource,
    val clock: Clock,
    val fs: Fs,
    val firmwareDaddy: FirmwareDaddy,
    val display: PinkyDisplay,
    soundAnalyzer: SoundAnalyzer,
    private val prerenderPixels: Boolean = false,
    private val switchShowAfterIdleSeconds: Int? = 600,
    private val adjustShowAfterIdleSeconds: Int? = null
) : Network.UdpListener {
    private val storage = Storage(fs)
    private val mappingResults = storage.loadMappingData(model)

    private val link = FragmentingUdpLink(network.link())
    val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)
    private val mdns = link.mdns()

    private val beatDisplayer = PinkyBeatDisplayer(beatSource)
    private var mapperIsRunning = false
    private var selectedShow = shows.first()
        set(value) {
            field = value
            display.selectedShow = value
            showRunner.nextShow = selectedShow
        }

    private val pubSub: PubSub.Server = PubSub.Server(httpServer).apply { install(gadgetModule) }
    private val gadgetManager = GadgetManager(pubSub)
    private val movingHeadManager = MovingHeadManager(fs, pubSub, model.movingHeads)
    private val showRunner =
        ShowRunner(model, selectedShow, gadgetManager, beatSource, dmxUniverse, movingHeadManager, clock)

    private val selectedShowChannel: PubSub.Channel<String>
    private var selectedNewShowAt = DateTime.now()

    private val brainToSurfaceMap_CHEAT = mutableMapOf<BrainId, Model.Surface>()
    private val surfaceToPixelLocationMap_CHEAT = mutableMapOf<Model.Surface, List<Vector3F>>()

    private val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    // This needs to go last-ish, otherwise we start getting network traffic too early.
    private val udpSocket = link.listenUdp(Ports.PINKY, this)

    private val json = Json(JsonConfiguration.Stable)

    init {
        httpServer.routing {
            get("/brains/{name}") { call ->
                val brainInfo = call.param("name")?.let { name -> brainInfos[BrainId(name)] }
                val mappedInfo = call.param("name")?.let { name -> mappingResults.dataForBrain(BrainId(name)) }
                if (brainInfo == null && mappedInfo == null) {
                    Network.TextResponse(404, "not found")
                } else {
                    Network.JsonResponse(json, 200, JsonObject(mapOf(
                        "name" to JsonPrimitive(call.param("name")),
                        "surface" to JsonPrimitive(mappedInfo?.surface?.name ?: (brainInfo?.surface as? IdentifiedSurface)?.name),
                        "mapped" to JsonPrimitive(mappedInfo?.pixelLocations?.isNotEmpty() ?: false),
                        "online" to JsonPrimitive(brainInfo != null),
                        "pixels" to JsonArray(mappedInfo?.pixelLocations?.map { pixel ->
                            JsonObject(mapOf(
                                "x" to JsonPrimitive(pixel?.x),
                                "y" to JsonPrimitive(pixel?.y),
                                "z" to JsonPrimitive(pixel?.z)
                            ))
                        } ?: listOf())
                    )))
                }
            }
            get("/brains") {
                val allBrains = mutableMapOf<BrainId, Triple<String?, Boolean, Boolean>>()
                for ((k, v) in brainInfos) {
                    val info = allBrains.getOrPut(k, { Triple(null, false, false) })
                    val surfaceName = (v.surface as? IdentifiedSurface)?.name
                    val hasPixels   = (v.surface as? IdentifiedSurface)?.pixelLocations?.isNotEmpty() ?: false
                    allBrains[k] = info.copy(first = surfaceName, second = hasPixels, third = true)
                }
                mappingResults.forEachBrain {
                    val info = allBrains.getOrPut(it.key, { Triple(null, false, false) })
                    val surfaceName = it.value.surface.name
                    val hasPixels = it.value.pixelLocations?.isNotEmpty() ?: false
                    allBrains[it.key] = info.copy(first = surfaceName, second = hasPixels)
                }

                Network.JsonResponse(json, 200, JsonObject(mapOf(
                    *allBrains.entries.map { (brainId, triple) ->
                        Pair(brainId.uuid, JsonObject(mapOf(
                            "surface" to JsonPrimitive(triple.first),
                            "mapped"  to JsonPrimitive(triple.second),
                            "online"  to JsonPrimitive(triple.third)
                        )))
                    }.toTypedArray()
                )))
            }
            get("/surfaces/{name}") { call ->
                val surface = call.param("name")?.let { name -> model.findModelSurface(name) }
                if (surface != null) {
                    val allVertices = model.geomVertices
                    val vertices = surface.allVertices()
                    val mappedBrains = mappingResults.dataForSurface(surface.name)

                    Network.JsonResponse(json, 200, JsonObject(mapOf(
                        "name" to JsonPrimitive(surface.name),
                        "description" to JsonPrimitive(surface.description),
                        "expectedPixelCount" to JsonPrimitive(surface.expectedPixelCount ?: -1),
                        "vertices" to JsonArray(vertices.map { vertex ->
                            JsonObject(mapOf(
                                "x" to JsonPrimitive(vertex.x),
                                "y" to JsonPrimitive(vertex.y),
                                "z" to JsonPrimitive(vertex.z)
                            ))
                        }),
                        "faces" to JsonArray(surface.faces.map { face ->
                            JsonArray(face.vertexIds.map { vertexIndex ->
                                JsonPrimitive(vertices.indexOf(allVertices.get(vertexIndex)))
                            })
                        }),
                        "lines" to JsonArray(surface.lines.map {
                            JsonArray(it.vertices.map { vertex ->
                                JsonPrimitive(vertices.indexOf(vertex))
                            })
                        }),
                        "brains" to JsonObject(mapOf(*mappedBrains?.entries?.map { (brainId, info) ->
                            Pair(brainId.uuid, JsonObject(mapOf(
                                "mapped" to JsonPrimitive(info.pixelLocations?.isNotEmpty() ?: false),
                                "online" to JsonPrimitive(brainInfos[brainId] != null))))
                        }?.toTypedArray() ?: arrayOf()))
                    )))
                } else {
                    Network.TextResponse(404, "not found")
                }
            }
            get("/surfaces") {
                Network.JsonResponse(json, 200,
                    JsonArray(model.allSurfaces.map { surface -> JsonPrimitive(surface.name) })
                )
            }
        }
        httpServer.listenWebSocket("/ws/mapper") { MapperEndpoint(storage) }

        pubSub.publish(Topics.availableShows, shows.map { show -> show.name }) {}
        selectedShowChannel = pubSub.publish(Topics.selectedShow, shows[0].name) { selectedShow ->
            this.selectedShow = shows.find { it.name == selectedShow }!!
        }

        GlslBase.plugins.add(SoundAnalysisPlugin(soundAnalyzer))

        // save these if we want to explicitly unregister them or update their TXT records later
        mdns.register(link.myHostname, "_sparklemotion-pinky", "_udp", Ports.PINKY, "local.", mutableMapOf(Pair("MAX_UDP_SIZE", "1450")))
        mdns.register(link.myHostname, "_sparklemotion-pinky", "_tcp", Ports.PINKY_UI_TCP, "local.")
        mdns.listen("_sparklemotion-brain", "_udp", "local.", MdnsBrainListenHandler())
    }

    suspend fun run(): Show.Renderer {
        GlobalScope.launch { beatDisplayer.run() }
        GlobalScope.launch {
            while (true) {
                if (mapperIsRunning) {
                    logger.info { "Mapping ${brainInfos.size} brains..." }
                } else {
                    logger.info { "Sending to ${brainInfos.size} brains..." }
                }
                delay(10000)
            }
        }

        display.listShows(shows)
        display.selectedShow = selectedShow

        display.onShowChange = { switchToShow(display.selectedShow!!) }

        while (true) {
            if (mapperIsRunning) {
                disableDmx()
                delay(50)
                continue
            }

            updateSurfaces()

            networkStats.reset()
            val elapsedMs = time {
                try {
                    drawNextFrame()
                } catch (e: Exception) {
                    logger.error("Error rendering frame for ${selectedShow.name}", e)
                    delay(1000)
                    switchToShow(SolidColorShow)
                }
            }
            display.showFrameMs = elapsedMs.toInt()
            display.stats = networkStats

            maybeChangeThingsIfUsersAreIdle()

            delay(30)
        }
    }

    private fun maybeChangeThingsIfUsersAreIdle() {
        val now = DateTime.now()
        val secondsSinceUserInteraction = now.minus(gadgetManager.lastUserInteraction).seconds
        if (switchShowAfterIdleSeconds != null
            && now.minus(selectedNewShowAt).seconds > switchShowAfterIdleSeconds
            && secondsSinceUserInteraction > switchShowAfterIdleSeconds
        ) {
            switchToShow(shows.random()!!)
            selectedNewShowAt = now
        }

        if (adjustShowAfterIdleSeconds != null
            && secondsSinceUserInteraction > adjustShowAfterIdleSeconds
        ) {
            gadgetManager.adjustSomething()
        }
    }

    fun switchToShow(nextShow: Show) {
        this.selectedShow = nextShow
        selectedShowChannel.onChange(nextShow.name)
    }

    internal fun updateSurfaces() {
        if (pendingBrainInfos.isNotEmpty()) {
            val brainSurfacesToRemove = mutableListOf<ShowRunner.SurfaceReceiver>()
            val brainSurfacesToAdd = mutableListOf<ShowRunner.SurfaceReceiver>()

            pendingBrainInfos.forEach { (brainId, incomingBrainInfo) ->
                val priorBrainInfo = brainInfos[brainId]
                if (priorBrainInfo != null) {
                    brainSurfacesToRemove.add(priorBrainInfo.surfaceReceiver)
                }

                if (incomingBrainInfo.hadException) {
                    // Existing Brain has had exceptions so we're forgetting about it.
                    brainInfos.remove(brainId)
                } else {
                    brainSurfacesToAdd.add(incomingBrainInfo.surfaceReceiver)
                    brainInfos[brainId] = incomingBrainInfo
                }
            }

            showRunner.surfacesChanged(brainSurfacesToAdd, brainSurfacesToRemove)

            pendingBrainInfos.clear()
        }

        display.brainCount = brainInfos.size
    }

    internal fun drawNextFrame() {
        aroundNextFrame {
            showRunner.nextFrame()
        }
    }

    private fun disableDmx() {
        dmxUniverse.allOff()
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> foundBrain(fromAddress, message)
            is MapperHelloMessage -> {
                logger.debug { "Mapper isRunning=${message.isRunning}" }
                mapperIsRunning = message.isRunning
            }
            is PingMessage -> if (message.isPong) receivedPong(message, fromAddress)
        }
    }

    private fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage
    ) {
        val brainId = BrainId(msg.brainId)
        val surfaceName = msg.surfaceName

        logger.info {
            "Hello from ${brainId.uuid}" +
                    " (${mappingResults.dataForBrain(brainId)?.surface?.name ?: "[unknown]"})" +
                    " at $brainAddress: $msg"
        }
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.info {
                "The firmware daddy doesn't like $brainId" +
                        " (${mappingResults.dataForBrain(brainId)?.surface?.name ?: "[unknown]"})" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }


        // println("Heard from brain $brainId at $brainAddress for $surfaceName")
        val dataFor = mappingResults.dataForBrain(brainId) ?: findMappingInfo_CHEAT(surfaceName, brainId)

        val surface = dataFor?.let {
            val pixelLocations = dataFor.pixelLocations?.map { it ?: Vector3F(0f, 0f, 0f) } ?: emptyList()
            val pixelCount = dataFor.pixelLocations?.size ?: SparkleMotion.MAX_PIXEL_COUNT

            if (msg.surfaceName != dataFor.surface.name) {
                val mappingMsg = BrainMappingMessage(
                    brainId, dataFor.surface.name, null, Vector2F(0f, 0f),
                    Vector2F(0f, 0f), pixelCount, pixelLocations
                )
                udpSocket.sendUdp(brainAddress, Ports.BRAIN, mappingMsg)
            }

            IdentifiedSurface(dataFor.surface, pixelCount, dataFor.pixelLocations)
        } ?: AnonymousSurface(brainId)


        val priorBrainInfo = brainInfos[brainId]
        if (priorBrainInfo != null) {
            if (priorBrainInfo.brainId == brainId && priorBrainInfo.surface == surface) {
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

        val sendFn: (Shader.Buffer) -> Unit = { shaderBuffer ->
            val message = BrainShaderMessage(shaderBuffer.shader, shaderBuffer).toBytes()
            try {
                udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)
            } catch (e: Exception) {
                // Couldn't send to Brain? Schedule to remove it.
                val brainInfo = brainInfos[brainId]!!
                brainInfo.hadException = true
                pendingBrainInfos[brainId] = brainInfo

                logger.error("Error sending to $brainId, will take offline", e)
            }

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }

        val surfaceReceiver = if (prerenderPixels) {
            PrerenderingSurfaceReceiver(surface, sendFn)
        } else {
            ShowRunner.SurfaceReceiver(surface, sendFn)
        }

        val brainInfo = BrainInfo(brainAddress, brainId, surface, msg.firmwareVersion, msg.idfVersion, surfaceReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo

        // Decide whether or not to tell this brain it should use a different firmware

    }

    private fun findMappingInfo_CHEAT(surfaceName: String?, brainId: BrainId): MappingResults.Info? {
        val modelSurface = surfaceName?.let { model.findModelSurface(surfaceName) } ?: brainToSurfaceMap_CHEAT[brainId]
        return if (modelSurface != null) {
            MappingResults.Info(modelSurface, surfaceToPixelLocationMap_CHEAT[modelSurface])
        } else {
            null
        }
    }

    /** If we want a pong back from a [BrainShaderMessage], send this. */
    private fun generatePongPayload(): ByteArray {
        return ByteArrayWriter().apply {
            writeLong(getTimeMillis())
        }.toBytes()
    }

    private fun receivedPong(message: PingMessage, fromAddress: Network.Address) {
        val originalSentAt = ByteArrayReader(message.data).readLong()
        val elapsedMs = getTimeMillis() - originalSentAt
        logger.debug { "Shader pong from $fromAddress took ${elapsedMs}ms" }
    }

    fun providePanelMapping_CHEAT(brainId: BrainId, surface: Model.Surface) {
        brainToSurfaceMap_CHEAT[brainId] = surface
    }

    fun providePixelMapping_CHEAT(surface: Model.Surface, pixelLocations: List<Vector3F>) {
        surfaceToPixelLocationMap_CHEAT[surface] = pixelLocations
    }

    inner class PinkyBeatDisplayer(val beatSource: BeatSource) {
        suspend fun run() {
            while (true) {
                val beatData = beatSource.getBeatData()
                display.beat = beatData.beatWithinMeasure(clock).toInt()
                display.bpm = beatData.bpm
                display.beatConfidence = beatData.confidence
                delay(10)
            }
        }
    }

    class NetworkStats(var bytesSent: Int = 0, var packetsSent: Int = 0) {
        internal fun reset() {
            bytesSent = 0
            packetsSent = 0
        }
    }

    private inner class PrerenderingSurfaceReceiver(surface: Surface, sendFn: (Shader.Buffer) -> Unit) :
        ShowRunner.SurfaceReceiver(surface, sendFn) {
        var currentRenderTree: Brain.RenderTree<*>? = null
        private var currentPoolKey: Any? = null
        var pixels: PixelsAdapter? = null
        var currentBuffer: Shader.Buffer? = null

        @Suppress("UNCHECKED_CAST")
        override fun send(shaderBuffer: Shader.Buffer) {
            val shader = shaderBuffer.shader as Shader<Shader.Buffer>
            var renderTree = currentRenderTree
            if (renderTree == null || renderTree.shader != shader) {
                val priorPoolKey = currentPoolKey
                var newPoolKey: Any? = null

                val renderer = shader.createRenderer(surface, object : RenderContext {
                    override fun <T : PooledRenderer> registerPooled(key: Any, fn: () -> T): T {
                        newPoolKey = key
                        return poolingRenderContext.registerPooled(key, fn)
                    }
                })

                if (newPoolKey != priorPoolKey) {
                    if (priorPoolKey != null) {
                        poolingRenderContext.decrement(priorPoolKey)
                    }
                    currentPoolKey = newPoolKey
                }

                renderTree = Brain.RenderTree(shader, renderer, shaderBuffer)
                currentRenderTree?.release()
                currentRenderTree = renderTree

                if (pixels == null) {
                    val pixelBuffer = PixelShader(PixelShader.Encoding.DIRECT_RGB).createBuffer(surface)
                    pixels = PixelsAdapter(pixelBuffer)
                }
            }

            val renderer = currentRenderTree!!.renderer as Shader.Renderer<Shader.Buffer>
            renderer.beginFrame(shaderBuffer, pixels!!.size)

            // we need to reorder the draw cycle, so don't do the rest of the render yet!
            currentBuffer = shaderBuffer
        }

        @Suppress("UNCHECKED_CAST")
        fun actuallySend() {
            val renderTree = currentRenderTree
            if (renderTree != null) {
                val renderer = renderTree.renderer as Shader.Renderer<Shader.Buffer>
                val pixels = pixels!!
                val currentBuffer = currentBuffer!!

                for (i in pixels.indices) {
                    pixels[i] = renderer.draw(currentBuffer, i)
                }
                this.currentBuffer = null

                renderer.endFrame()
                pixels.finishedFrame()

                renderTree.draw(pixels)

                super.send(pixels.buffer)
            }
        }
    }

    var poolingRenderContext = PoolingRenderContext()
    var lastSentAt: Long = 0

    private fun aroundNextFrame(callNextFrame: () -> Unit) {
        /**
         * [ShowRunner.SurfaceReceiver.send] is called here; if [prerenderPixels] is true, it won't
         * actually send; we need to do that ourselves.
         */
        callNextFrame()

        if (prerenderPixels) {
            val preDrawElapsed = timeSync {
                poolingRenderContext.preDraw()
            }

            val sendElapsed = timeSync {
                brainInfos.values.forEach { brainInfo ->
                    val surfaceReceiver = brainInfo.surfaceReceiver as PrerenderingSurfaceReceiver
                    surfaceReceiver.actuallySend()
                }
            }

//            println("preDraw took ${preDrawElapsed}ms, send took ${sendElapsed}ms")
        }
        val now = getTimeMillis()
        val elapsedMs = now - lastSentAt
//        println("It's been $elapsedMs")
        lastSentAt = now
    }


    class PoolingRenderContext : RenderContext {
        private val pooledRenderers = hashMapOf<Any, Holder<*>>()

        @Suppress("UNCHECKED_CAST")
        override fun <T : PooledRenderer> registerPooled(key: Any, fn: () -> T): T {
            val holder = pooledRenderers.getOrPut(key) { Holder(fn()) }
            holder.count++
            return holder.pooledRenderer as T
        }

        fun decrement(key: Any) {
            val holder = pooledRenderers[key]!!
            holder.count--
            if (holder.count == 0) {
                logger.debug { "Removing pooled renderer for $key" }
                pooledRenderers.remove(key)
            }
        }

        fun preDraw() {
            pooledRenderers.values.forEach { holder ->
                holder.pooledRenderer.preDraw()
            }
        }

        class Holder<T : PooledRenderer>(val pooledRenderer: T, var count: Int = 0)
    }

    private class PixelsAdapter(internal val buffer: PixelShader.Buffer) : Pixels {
        override val size: Int = buffer.colors.size

        override fun get(i: Int): Color = buffer.colors[i]

        override fun set(i: Int, color: Color) {
            buffer.colors[i] = color
        }

        override fun set(colors: Array<Color>) {
            for (i in 0 until min(colors.size, size)) {
                buffer.colors[i] = colors[i]
            }
        }
    }

    private inner class MdnsBrainListenHandler : Network.MdnsListenHandler {
        override fun resolved(service: Network.MdnsService) {
            val brainId = service.hostname
            val address = service.getAddress()
            if (address != null) {
                val version = service.getTXT("version")
                val idfVersion = service.getTXT("idf_ver")
                val msg = BrainHelloMessage(brainId, null, version, idfVersion)
                foundBrain(address, msg)
            }
        }

        override fun removed(service: Network.MdnsService) {
            TODO("not implemented: What do when brain disconnects?")
        }
    }

    companion object {
        val logger = Logger("Pinky")
    }
}

data class BrainId(val uuid: String)

class BrainInfo(
    val address: Network.Address,
    val brainId: BrainId,
    val surface: Surface,
    val firmwareVersion: String?,
    val idfVersion: String?,
    val surfaceReceiver: ShowRunner.SurfaceReceiver,
    var hadException: Boolean = false
)
