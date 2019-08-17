package baaahs

import baaahs.Brain.Companion.logger
import baaahs.geom.Matrix4
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.imaging.Bitmap
import baaahs.imaging.Image
import baaahs.imaging.NativeBitmap
import baaahs.mapper.ImageProcessing
import baaahs.mapper.MapperClient
import baaahs.mapper.MappingSession
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelShader
import baaahs.shaders.SolidShader
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.random.Random

class Mapper(
    private val network: Network,
    sheepModel: SheepModel,
    private val mapperUi: MapperUi,
    private val mediaDevices: MediaDevices,
    private val pinkyAddress: Network.Address
) : Network.UdpListener, MapperUi.Listener, CoroutineScope by MainScope() {
    //    private val maxPixelsPerBrain = SparkleMotion.MAX_PIXEL_COUNT
    private val maxPixelsPerBrain = 120

    // TODO: getCamera should just return max available size?
    lateinit var camera: MediaDevices.Camera

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private lateinit var mapperClient: MapperClient
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false
    private var newIncomingImage: Image? = null

    private var suppressShowsJob: Job? = null
    private val brainsToMap: MutableMap<Network.Address, BrainToMap> = mutableMapOf()

    private val activeColor = Color(0x07, 0xFF, 0x07)
    private val inactiveColor = Color(0x01, 0x00, 0x01)

    enum class Detector(val rgbaIndex: Int, val color: Color, val alternateColor: Color) {
        RED(0, Color.RED, Color.CYAN),
        GREEN(1, Color(0x01, 0xFF, 0x01), Color(0x01, 0x00, 0x01)),
        BLUE(2, Color.BLUE, Color.YELLOW)
    }

    val detectors = arrayOf(Detector.GREEN, Detector.GREEN, Detector.GREEN)

    private val redRgbaIndex = 0
    private val greenRgbaIndex = 1
    private val blueRgbaIndex = 2
    private val signalRgbaIndex = blueRgbaIndex
    private val indicatorRgbaIndex = greenRgbaIndex

    init {
        mapperUi.listen(this)
        mapperUi.addWireframe(sheepModel)
    }

    fun start() = doRunBlocking {
        link = FragmentingUdpLink(network.link())
        udpSocket = link.listenUdp(0, this)
        mapperClient = MapperClient(link, pinkyAddress)

        launch {
            mapperClient.listSessions().forEach { mapperUi.addExistingSession(it) }
        }
    }

    override fun onStart() {
        isPaused = false

        if (!isRunning) {
            camera = mediaDevices.getCamera().apply { onImage = { image -> haveImage(image) } }

            // Restart.
            isRunning = true
            launch { startNewSession() }
        }
    }

    override fun onPause() {
        isPaused = true
    }

    override fun onStop() {
        onClose()
    }

    override fun onClose() {
        logger.info { "Shutting down Mapper..." }
        isRunning = false
        camera.close()

        suppressShowsJob?.cancel()
        udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(false))

        mapperUi.close()
    }

    suspend fun startNewSession() {
        mapperUi.showMessage("ESTABLISHING UPLINK…")

        // shut down Pinky, advertise for Brains...
        retry {
            udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(true))
            delay(1000L)
            udpSocket.broadcastUdp(Ports.BRAIN, solidColor(inactiveColor))
        }

        // keep Pinky from waking up while we're running...
        suppressShows()

        // wait for responses from Brains
        pauseForUserInteraction("PRESS PLAY WHEN ALL SURFACES ARE GREEN")
        val brainIdRequestJob = coroutineScope {
            launch {
                while (isPaused) {
                    udpSocket.broadcastUdp(Ports.BRAIN, BrainIdRequest())
                    delay(1000L)
                }
            }
        }

        mapperUi.showMessage("${brainsToMap.size} SURFACES DISCOVERED!")
        waitUntilUnpaused()
        brainIdRequestJob.cancelAndJoin()

        if (brainsToMap.isEmpty()) {
            mapperUi.showMessage("NO SURFACES DISCOVERED! TRY AGAIN!")
            isRunning = false
            return
        }

        mapperUi.showMessage("READY PLAYER ONE…")
        pauseForUserInteraction("ALIGN MODEL AND PRESS PLAY WHEN READY")
        waitUntilUnpaused()

        Session().start()
    }

    inner class Session {
        val sessionStartTime = DateTime.now()
        val visibleSurfaces = mapperUi.getVisibleSurfaces()
        private var baseBitmap: Bitmap? = null
        val cameraOrientation = mapperUi.lockUi()
        val surfaceScheme = Detector.GREEN
        lateinit var deltaBitmap: Bitmap

        val whitePaletteIndex = 3

        val pixelShader = PixelShader(PixelShader.Encoding.INDEXED_4)
        val pixelShaderBuffer = pixelShader.createBuffer(object : Surface {
            override val pixelCount = SparkleMotion.MAX_PIXEL_COUNT

            override fun describe(): String = "Mapper surface"
        }).apply {
            palette[0] = detectors[0].alternateColor
            palette[1] = detectors[1].alternateColor
            palette[2] = detectors[2].alternateColor
            palette[3] = Color.WHITE
            setAll(0)
        }

        fun resetToBase() {
            pixelShaderBuffer.indices.forEach { pixelShaderBuffer[it] = it % 3 }
        }

        suspend fun allPixelsOff() {
            resetToBase()
            sendToAllReliably(pixelShaderBuffer)
        }

        suspend fun turnOnPixel(pixelIndex: Int) {
            resetToBase()
            pixelShaderBuffer[pixelIndex] = whitePaletteIndex
            sendToAllReliably(pixelShaderBuffer)
        }

        suspend fun start() {
            mapperUi.showMessage("CALIBRATING…")
            logger.info { "Visible surfaces: ${visibleSurfaces.joinToString { it.modelSurface.name }}" }

            // Blackout for base image.
            sendToAllReliably(solidColorBuffer(inactiveColor))
            delay(1000L) // wait for focus

            // Create base image from the brightest of a few samples.
            val bitmap = getBrightImageBitmap(5)
            baseBitmap = bitmap
            deltaBitmap = NativeBitmap(bitmap.width, bitmap.height)

            val baseImageName = mapperClient.saveImage(sessionStartTime, "base", bitmap)

            mapperUi.showMessage("MAPPING…")
            mapperUi.showStats(brainsToMap.size, 0, -1)

            try {
                logger.info { "identify surfaces..." }
                // light up each brain in an arbitrary sequence and capture its delta...
                brainsToMap.values.forEachIndexed { index, brainToMap ->
                    identifyBrain(index, brainToMap)

                    pauseForUserInteraction()

                    var retryCount = 0
                    mapperUi.setRedo {
                        identifyBrain(index, brainToMap, ++retryCount)
                    }

                    waitUntilUnpaused()
                    mapperUi.setRedo(null)

                    deliverer.send(brainToMap, solidColorBuffer(inactiveColor))
                    deliverer.await()
                }

                delay(1000L)

//            mapperUi.showMessage("SEEKING LIMITS…")
//            var maxPixel = 0
                // binary search for highest present pixel 0..MAX_PIXEL_COUNT…

                logger.info { "identify pixels..." }
                // light up each pixel...

                // Turn all pixels off.
                resetToBase()
                sendToAllReliably(pixelShaderBuffer)
                delay(1000L)

                val pixelStep = 4
                fun actualPixelIndex(pixelIndexX: Int) =
                    pixelIndexX * pixelStep % maxPixelsPerBrain + pixelIndexX * pixelStep / maxPixelsPerBrain


                for (pixelIndexX in 0 until maxPixelsPerBrain) {
                    // Reorder so we get e.g. 0, 4, 8, ..., 1, 5, 9, ..., 2, 6, 10, ..., 3, 7, 11, ...
                    val pixelIndex = actualPixelIndex(pixelIndexX)
                    val nextPixelIndex = actualPixelIndex(pixelIndexX + 1)
                    identifyPixel(pixelIndex, nextPixelIndex)

//                    pauseForUserInteraction()
                    waitUntilUnpaused()
                    allPixelsOff()
                }
                logger.info { "done identifying pixels..." }

                logger.info { "done identifying things... $isRunning" }
                mapperUi.showMessage("++LEVEL UNLOCKED++")

                delay(1000L)
            } catch (e: TimeoutException) {
                mapperUi.showMessage("Timed out: ${e.message}")
                logger.error("Timed out", e)
            }

            isRunning = false
            mapperUi.unlockUi()

            retry { udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning)) }

            logger.info { "Here's what we learned!" }

            val surfaces = mutableListOf<MappingSession.SurfaceData>()
            brainsToMap.forEach { (address, brainToMap) ->
                logger.info { "Brain ID: ${brainToMap.brainId} at ${address}:" }
                logger.info { "  Surface: ${brainToMap.guessedModelSurface}" }
                logger.debug { "  Pixels:" }

                val visibleSurface = brainToMap.guessedVisibleSurface
                if (visibleSurface != null) {
                    visibleSurface.showPixels()

                    brainToMap.pixelMapData.forEach { (pixelIndex, mapData) ->
                        val changeRegion = mapData.pixelChangeRegion
                        val position = visibleSurface.translatePixelToPanelSpace(
                            changeRegion.centerX.toFloat(),
                            changeRegion.centerY.toFloat()
                        )
                        logger.debug { "    $pixelIndex -> ${position?.x},${position?.y}" }
                    }

                    val pixels = visibleSurface.pixelsInModelSpace.mapIndexed { index, vector3F ->
                        val pixelMapData = brainToMap.pixelMapData[index]
                        val pixelChangeRegion = pixelMapData?.pixelChangeRegion
                        val screenPosition = pixelChangeRegion?.let {
                            visibleSurface.translatePixelToPanelSpace(it.centerX.toFloat(), it.centerY.toFloat())
                        }
                        MappingSession.SurfaceData.PixelData(
                            vector3F,
                            screenPosition,
                            pixelMapData?.deltaImageName
                        )
                    }

                    surfaces.add(
                        MappingSession.SurfaceData(
                            brainToMap.brainId,
                            visibleSurface.modelSurface.name,
                            pixels,
                            brainToMap.deltaImageName,
                            screenAreaInSqPixels = null,
                            screenAngle = null
                        )
                    )
                }
            }

            val mappingSession =
                MappingSession(sessionStartTime.unixMillis, surfaces, cameraOrientation.cameraMatrix, baseImageName)
            mapperClient.saveSession(mappingSession)
        }

        private suspend fun identifyPixel(pixelIndex: Int, nextPixelIndex: Int) {
            val detector = detectors[pixelIndex % detectors.size]
            mapperUi.showMessage("MAPPING PIXEL $pixelIndex / $maxPixelsPerBrain…")

            if (pixelIndex % 128 == 0) logger.debug { "pixel $pixelIndex... isRunning is $isRunning" }
            turnOnPixel(pixelIndex)

            slowCamDelay()
            val pixelOnBitmap = getBrightImageBitmap(2)
            ImageProcessing.diff(pixelOnBitmap, baseBitmap!!, deltaBitmap, detector)
            mapperUi.showDiffImage(deltaBitmap)
            val pixelOnImageName =
                mapperClient.saveImage(sessionStartTime, "pixel-$pixelIndex", deltaBitmap)

            if (nextPixelIndex < maxPixelsPerBrain) turnOnPixel(pixelIndex)

            brainsToMap.values.forEach { brainToMap ->
                identifyBrainPixel(pixelIndex, brainToMap, pixelOnBitmap, deltaBitmap, detector, pixelOnImageName)

                delay(1)
                //                    pauseForUserInteraction()
                waitUntilUnpaused()
            }
        }

        suspend fun identifyBrain(index: Int, brainToMap: BrainToMap, retryCount: Int = 0) {
            mapperUi.showMessage("MAPPING SURFACE $index / ${brainsToMap.size} (${brainToMap.brainId})…")

            deliverer.send(brainToMap, solidColorBuffer(activeColor))
            deliverer.await()
            slowCamDelay()

            val surfaceOnBitmap = getBrightImageBitmap(3)
            val surfaceAnalysis = ImageProcessing.diff(surfaceOnBitmap, baseBitmap!!, deltaBitmap, surfaceScheme)
            val surfaceChangeRegion = surfaceAnalysis.detectChangeRegion(.25f)
            logger.debug {
                "surfaceChangeRegion(${brainToMap.brainId}) =" +
                        " $surfaceChangeRegion ${surfaceChangeRegion.width}x${surfaceChangeRegion.height}"
            }

            mapperUi.showDiffImage(deltaBitmap, surfaceChangeRegion)

            brainToMap.changeRegion = surfaceChangeRegion

            val thresholdValue = surfaceAnalysis.thresholdValueFor(.25f)
            //                val pxAboveThreshold = surfaceAnalysis.hist.sumValues(thresholdValue..255)
            val sampleLocations = mutableListOf<Pair<Int, Int>>()
            ImageProcessing.pixels(surfaceOnBitmap, surfaceScheme, surfaceChangeRegion) { x, y, value ->
                if (value >= thresholdValue && Random.nextFloat() < .05f) {
                    sampleLocations.add(x to y)
                }
            }

            val surfaceBallot = Ballot<MapperUi.VisibleSurface>()
            while (surfaceBallot.totalVotes < 10) {
                val (x, y) = sampleLocations.random()!!
                val visibleSurface = mapperUi.intersectingSurface(x, y, visibleSurfaces)
                val surface = visibleSurface?.modelSurface
                surface?.let {
                    surfaceBallot.cast(surface.name, visibleSurface)
                }
            }

            //                val orderedPanels = visibleSurfaces.map { visiblePanel ->
            //                    visiblePanel to visiblePanel.boxOnScreen.distanceTo(surfaceChangeRegion)
            //                }.sortedBy { it.second }
            //
            //                mapperUi.showCandidates(orderedPanels)
            //
            //                val firstGuess = orderedPanels.first().first
            val firstGuess = surfaceBallot.winner()
            val firstGuessSurface = firstGuess.modelSurface

            mapperUi.showMessage("$index / ${brainsToMap.size}: ${brainToMap.brainId} — surface is ${firstGuessSurface.name}?")
            mapperUi.showMessage2("Candidate panels: ${surfaceBallot.summarize()}")

            logger.info { "Guessed panel ${firstGuessSurface.name} for ${brainToMap.brainId}" }
            brainToMap.guessedModelSurface = firstGuessSurface
            brainToMap.guessedVisibleSurface = firstGuess
            brainToMap.panelDeltaBitmap = deltaBitmap.clone()
            brainToMap.deltaImageName =
                mapperClient.saveImage(sessionStartTime, "brain-${brainToMap.brainId}-$retryCount", deltaBitmap)
        }

        private fun identifyBrainPixel(
            pixelIndex: Int,
            brainToMap: BrainToMap,
            pixelOnBitmap: Bitmap,
            deltaBitmap: Bitmap,
            detector: Detector,
            pixelOnImageName: String
        ) {
            mapperUi.showMessage("MAPPING PIXEL $pixelIndex / $maxPixelsPerBrain (${brainToMap.brainId})…")
            val surfaceChangeRegion = brainToMap.changeRegion
            val visibleSurface = brainToMap.guessedVisibleSurface

            if (surfaceChangeRegion != null && surfaceChangeRegion.sqPix() > 0 && visibleSurface != null) {
                mapperUi.showAfter(brainToMap.panelDeltaBitmap!!)

                val analysis = ImageProcessing.diff(
                    pixelOnBitmap,
                    baseBitmap!!,
                    deltaBitmap,
                    detector,
                    brainToMap.panelDeltaBitmap!!,
                    surfaceChangeRegion
                )
                val pixelChangeRegion = analysis.detectChangeRegion(.5f)
                logger.debug {
                    "pixelChangeRegion($pixelIndex,${brainToMap.guessedModelSurface?.name} =" +
                            " $pixelChangeRegion ${pixelChangeRegion.width}x${pixelChangeRegion.height}"
                }

                mapperUi.showDiffImage(deltaBitmap, pixelChangeRegion)
                mapperUi.showBefore(pixelOnBitmap)
                mapperUi.showAfter(brainToMap.panelDeltaBitmap!!)

                if (!pixelChangeRegion.isEmpty()) {
                    val center = Vector3F(
                        (pixelChangeRegion.centerX - surfaceChangeRegion.x0) / surfaceChangeRegion.width.toFloat(),
                        (pixelChangeRegion.centerY - surfaceChangeRegion.y0) / surfaceChangeRegion.height.toFloat(),
                        0f
                    )

                    visibleSurface.addPixel(
                        pixelIndex,
                        pixelChangeRegion.centerX.toFloat(),
                        pixelChangeRegion.centerY.toFloat()
                    )
                    brainToMap.pixelMapData[pixelIndex] = PixelMapData(pixelChangeRegion, pixelOnImageName)
                    logger.debug { "$pixelIndex/${brainToMap.brainId}: center = $center" }
                }
            }
        }
    }

    private suspend fun slowCamDelay() {
        getImage()
        getImage()
        getImage()
    }

    private suspend fun getBrightImageBitmap(samples: Int): Bitmap {
        val bitmap = getImage().toBitmap()
        for (i in 1 until samples) {
            bitmap.lighten(getImage().toBitmap())
        }
        return bitmap
    }

    private fun pauseForUserInteraction(message: String = "PRESS PLAY WHEN READY") {
        isPaused = true
        mapperUi.pauseForUserInteraction()
        mapperUi.showMessage2(message)
    }

    private suspend fun waitUntilUnpaused() {
        while (isPaused) delay(50L)
        mapperUi.showMessage2("")
    }

    private suspend fun sendToAllReliably(buffer: Shader.Buffer) {
        brainsToMap.values.forEach {
            deliverer.send(it, buffer)
        }
        deliverer.await()
    }

    private suspend fun retry(fn: suspend () -> Unit) {
        fn()
        delay(10)
        fn()
    }

    // keep Pinky from restarting a show up while Mapper is running...
    private fun suppressShows() {
        suppressShowsJob = launch(CoroutineName("Suppress Pinky")) {
            while (isRunning) {
                delay(10000L)
                udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
            }
        }
    }

    private fun solidColor(color: Color): BrainShaderMessage {
        val buf = solidColorBuffer(color)
        return BrainShaderMessage(buf.shader, buf)
    }

    private fun solidColorBuffer(color: Color): Shader.Buffer {
        val solidShader = SolidShader()
        val buffer = solidShader.createBuffer(object : Surface {
            override val pixelCount = SparkleMotion.MAX_PIXEL_COUNT

            override fun describe(): String = "Mapper surface"
        }).apply { this.color = color }
        return buffer
    }

    private val deliverer = ReliableShaderMessageDeliverer()

    inner class ReliableShaderMessageDeliverer {
        val outstanding = mutableMapOf<List<Byte>, DeliveryAttempt>()
        val pongs = Channel<PingMessage>()

        fun send(brainToMap: BrainToMap, buffer: Shader.Buffer) {
            val deliveryAttempt = DeliveryAttempt(brainToMap, buffer)
//            logger.debug { "attempting reliable delivery with key ${deliveryAttempt.key.stringify()}" }
            outstanding[deliveryAttempt.key] = deliveryAttempt
            deliveryAttempt.attemptDelivery()
        }

        suspend fun await(retryAfterMillis: Double = 200.0, failAfterMillis: Double = 10000.0) {
            logger.debug { "Waiting pongs from ${outstanding.values.map { it.brainToMap.brainId }}..." }

            outstanding.values.forEach {
                it.retryAt = it.sentAt + retryAfterMillis
                it.failAt = it.sentAt + failAfterMillis
            }

            while (outstanding.isNotEmpty()) {
                val waitingFor =
                    outstanding.values.map { it.brainToMap.guessedModelSurface?.name ?: it.brainToMap.brainId }
                        .sorted()
                mapperUi.showMessage2("Waiting for PONG from ${waitingFor.joinToString(",")}")
//                logger.debug { "pongs outstanding: ${outstanding.keys.map { it.stringify() }}" }

                var sleepUntil = Double.MAX_VALUE

                val nowMs = getTimeMillis().toDouble()

                outstanding.values.forEach {
                    if (it.failAt < nowMs) {
                        throw TimeoutException("Timed out waiting for ${it.brainToMap.brainId} pong ${it.key.stringify()}")
                    }
                    if (sleepUntil > it.failAt) sleepUntil = it.failAt

                    if (it.retryAt < nowMs) {
                        logger.warn {
                            "Haven't heard from ${it.brainToMap.brainId} after ${nowMs - it.retryAt}," +
                                    " retrying (attempt ${++it.retryCount})..."
                        }
                        it.attemptDelivery()
                        it.retryAt = nowMs + retryAfterMillis
                    }
                    if (sleepUntil > it.retryAt) sleepUntil = it.retryAt
                }

                val timeoutMs = sleepUntil - nowMs
//                logger.debug { "Before pongs.receive() withTimeout(${timeoutMs}ms)" }
                val pong = withTimeoutOrNull(timeoutMs.toLong()) {
                    pongs.receive()
                }

                if (pong != null) {
                    val pongTag = pong.data.toList()
//                    logger.debug { "Received pong(${pongTag.stringify()})" }

                    val deliveryAttempt = outstanding.remove(pongTag)
                    if (deliveryAttempt != null) {
                        deliveryAttempt.succeeded()
                    } else {
                        logger.warn { "huh? no such pong tag ${pongTag.stringify()}!" }
                    }
                }

                mapperUi.showMessage2("")
            }
        }

        fun gotPong(pingMessage: PingMessage) {
            launch {
                pongs.send(pingMessage)
            }
        }
    }

    class TimeoutException(message: String) : Exception(message)

    inner class DeliveryAttempt(val brainToMap: BrainToMap, val buffer: Shader.Buffer) {
        private val tag = Random.nextBytes(8)
        val key get() = tag.toList()
        val sentAt = getTimeMillis().toDouble()
        var retryAt = 0.0
        var failAt = 0.0
        var retryCount = 0

        fun attemptDelivery() {
            udpSocket.sendUdp(brainToMap.address, brainToMap.port, BrainShaderMessage(buffer.shader, buffer, tag))
        }

        fun succeeded() {
            logger.debug { "${brainToMap.brainId} shader message pong after ${getTimeMillis() - sentAt}ms" }
        }
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
//        logger.debug { "Mapper received message from $fromAddress:$fromPort ${bytes[0]}" }
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> {
                logger.debug { "Heard from Brain ${message.brainId} surface=${message.surfaceName ?: "unknown"}" }
                val brainToMap = brainsToMap.getOrPut(fromAddress) { BrainToMap(fromAddress, message.brainId) }
                mapperUi.showMessage("${brainsToMap.size} SURFACES DISCOVERED!")
                brainToMap.shade { solidColor(Color.GREEN) }
            }

            is PingMessage -> {
                if (message.isPong) {
                    deliverer.gotPong(message)
                }
            }
        }
    }

    private fun haveImage(image: Image) {
//        println("image: $image")
        mapperUi.showCamImage(image)

        newIncomingImage = image
    }

    /**
     * Get an image from the camera that was [we hope was] captured after the moment the method was called.
     */
    private suspend fun getImage(): Image {
        newIncomingImage = null

        while (newIncomingImage == null) {
            delay(2L)
        }

        val image = newIncomingImage!!
        newIncomingImage = null
        return image
    }

    private suspend fun getImage(tries: Int = 5, test: (Image) -> Boolean): Image {
        var image = getImage()
        var remainingTries = tries - 1
        while (!test(image) && remainingTries-- > 0) {
            image = getImage()
        }
        return image
    }

    inner class BrainToMap(val address: Network.Address, val brainId: String) {
        val port get() = Ports.BRAIN
        var changeRegion: MediaDevices.Region? = null
        var guessedModelSurface: Model.Surface? = null
        var guessedVisibleSurface: MapperUi.VisibleSurface? = null
        var panelDeltaBitmap: Bitmap? = null
        var deltaImageName: String? = null
        val pixelMapData: MutableMap<Int, PixelMapData> = mutableMapOf()

        fun shade(shaderMessage: () -> BrainShaderMessage) {
            udpSocket.sendUdp(address, Ports.BRAIN, shaderMessage())
        }
    }

    class PixelMapData(val pixelChangeRegion: MediaDevices.Region, val deltaImageName: String)

    private class Ballot<T> {
        private val box = hashMapOf<String, Vote<T>>()
        var totalVotes: Int = 0
            private set

        fun cast(key: String, value: T) {
            box.getOrPut(key) { Vote(value) }.votes++
            totalVotes++
        }

        fun winner(): T {
            return box.values.sortedByDescending { it.votes }.first().item
        }

        fun summarize(): String {
            return box.entries
                .sortedByDescending { (_, v) -> v.votes }
                .map { (k, v) -> "$k=${v.votes}" }
                .joinToString(", ")
        }

        private class Vote<T>(val item: T) {
            var votes = 0
        }
    }

    companion object {
        val logger = Logger("Mapper")
    }

    fun List<Byte>.stringify(): String {
        return map { (it.toInt() and 0xff).toString(16).padStart(2, '0') }.joinToString("")
    }
}

interface MapperUi {
    fun listen(listener: Listener)

    fun addWireframe(sheepModel: SheepModel)
    fun showCamImage(image: Image, changeRegion: MediaDevices.Region? = null)
    fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region? = null)
    fun showMessage(message: String)
    fun showMessage2(message: String)
    fun showBefore(bitmap: Bitmap)
    fun showAfter(bitmap: Bitmap)
    fun setRedo(fn: (suspend () -> Unit)?)
    fun lockUi(): CameraOrientation
    fun unlockUi()
    fun getVisibleSurfaces(): List<VisibleSurface>
    fun showCandidates(orderedPanels: List<Pair<VisibleSurface, Float>>)
    fun intersectingSurface(x: Int, y: Int, visibleSurfaces: List<VisibleSurface>): VisibleSurface?
    fun showStats(total: Int, mapped: Int, visible: Int)
    fun close()
    fun addExistingSession(name: String)
    fun pauseForUserInteraction()

    interface Listener {
        fun onStart()
        fun onPause()
        fun onStop()
        fun onClose()
    }

    interface VisibleSurface {
        val modelSurface: Model.Surface
        val boxOnScreen: MediaDevices.Region
        val pixelsInModelSpace: List<Vector3F?>
        fun translatePixelToPanelSpace(screenX: Float, screenY: Float): Vector2F?
        fun addPixel(pixelIndex: Int, x: Float, y: Float)
        fun showPixels()
        fun hidePixels()
    }

    interface CameraOrientation {
        val cameraMatrix: Matrix4
        val aspect: Double
    }
}
