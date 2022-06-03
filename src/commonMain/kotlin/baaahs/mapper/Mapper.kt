package baaahs.mapper

import baaahs.Color
import baaahs.MediaDevices
import baaahs.SparkleMotion
import baaahs.api.ws.WebSocketClient
import baaahs.geom.Matrix4F
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.imaging.Bitmap
import baaahs.imaging.Dimen
import baaahs.imaging.Image
import baaahs.imaging.NativeBitmap
import baaahs.model.Model
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.scene.SceneProvider
import baaahs.shaders.PixelBrainShader
import baaahs.shaders.SolidBrainShader
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.proto.*
import baaahs.ui.Observable
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Stats
import baaahs.util.asMillis
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.random.Random

/** [SolidBrainShader] appears to be busted as of 2020/09. */
const val USE_SOLID_SHADERS = false

abstract class Mapper(
    private val network: Network,
    sceneProvider: SceneProvider,
    private val mediaDevices: MediaDevices,
    private val pinkyAddress: Network.Address,
    private val clock: Clock,
    private val mapperScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : Observable(), Network.UdpListener, CoroutineScope by MainScope() {
    private val facade = Facade()

    private var selectedDevice: MediaDevices.Device? = null

    // TODO: getCamera should just return max available size?
    lateinit var camera: MediaDevices.Camera

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    internal lateinit var webSocketClient: WebSocketClient
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false
    private var newIncomingImage: Image? = null

    private var suppressShowsJob: Job? = null
    private val brainsToMap: MutableMap<Network.Address, BrainToMap> = mutableMapOf()

    private val activeColor = Color(0x07, 0xFF, 0x07)
    private val inactiveColor = Color(0x01, 0x00, 0x01)

    private val sessions: MutableList<String> = arrayListOf()
    private val stats = mapperStats

    abstract var devices: List<MediaDevices.Device>

    init {
        sceneProvider.addObserver(fireImmediately = true) {
            it.openScene?.model?.let {
                addWireframe(it)
            }
        }
    }

    open fun onLaunch() {
        mapperScope.launch { start() }
    }

    fun start() {
        link = network.link("mapper")
        udpSocket = link.listenFragmentingUdp(0, this)
        webSocketClient = WebSocketClient(link, pinkyAddress)

        launch {
            webSocketClient.listSessions().forEach {
                addExistingSession(it)
                sessions.add(it)
            }

            facade.notifyChanged()
        }

        launch {
            devices = mediaDevices.enumerate()
        }
    }

    fun onStart() {
        isPaused = false

        if (!isRunning) {
            openCamera()

            // Restart.
            isRunning = true
            launch { startNewSession() }
        }
    }

    fun onPause() {
        isPaused = true
    }

    fun onStop() {
        onClose()
    }

    open fun onClose() {
        logger.info { "Shutting down Mapper..." }
        isRunning = false
        if (this::camera.isInitialized) camera.close()

        suppressShowsJob?.cancel()
        udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(false))

        close()
    }

    fun useCamera(selectedDevice: MediaDevices.Device?) {
        this.selectedDevice = selectedDevice
        openCamera()
    }

    suspend fun loadMappingSession(name: String): MappingSession =
        withContext(mapperScope.coroutineContext) {
            webSocketClient.loadSession(name)
        }

    private fun openCamera() {
        if (this::camera.isInitialized) camera.close()
        camera = mediaDevices.getCamera(selectedDevice).apply {
            onImage = { image -> haveImage(image) }
        }
    }

    fun showCamImage(image: Image, changeRegion: MediaDevices.Region? = null) {
        showLiveCamImage(image, changeRegion)
    }

    private suspend fun startNewSession() {
        showMessage("ESTABLISHING UPLINK…")

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

        showMessage("${brainsToMap.size} SURFACES DISCOVERED!")
        waitUntilUnpaused()
        brainIdRequestJob.cancelAndJoin()

        if (brainsToMap.isEmpty()) {
            showMessage("NO SURFACES DISCOVERED! TRY AGAIN!")
            isRunning = false
            return
        }

        showMessage("READY PLAYER ONE…")
        pauseForUserInteraction("ALIGN MODEL AND PRESS PLAY WHEN READY")
        waitUntilUnpaused()

        Session().start()
    }

    inner class Session {
        private val sessionStartTime = DateTime.now()
        private val visibleSurfaces = getVisibleSurfaces()
        private var baseBitmap: Bitmap? = null
        private val cameraOrientation = lockUi()
        private lateinit var deltaBitmap: Bitmap

        private fun resetToBase() {
            brainsToMap.values.forEach {
                it.pixelShaderBuffer.setAll(0)
            }
        }

        private suspend fun allPixelsOff() {
            resetToBase()
            sendToAllReliably(brainsToMap.values) { it.pixelShaderBuffer }
        }

        private fun brainsWithPixel(pixelIndex: Int) =
            brainsToMap.values.filter { pixelIndex < it.expectedPixelCountOrDefault }

        private suspend fun turnOnPixel(pixelIndex: Int) {
            resetToBase()

            val relevantBrains = brainsWithPixel(pixelIndex)
            relevantBrains.forEach {
                it.pixelShaderBuffer[pixelIndex] = 1
            }

            sendToAllReliably(relevantBrains) { it.pixelShaderBuffer }
        }

        private suspend fun turnOnPixelsByMask(maskBit: Int, invert: Boolean) {
            resetToBase()
            val mask = 1 shl maskBit
            brainsToMap.values.forEach { brainToMap ->
                for (i in 0 until brainToMap.expectedPixelCountOrDefault) {
                    val pixelOn = if (i and mask != 0) !invert else invert
                    if (pixelOn) {
                        brainToMap.pixelShaderBuffer[i] = 1
                    }
                }
            }

            sendToAllReliably(brainsToMap.values) { it.pixelShaderBuffer }
        }

        suspend fun start() {
            showMessage("CALIBRLATING…")
            logger.info { "Visible surfaces: ${visibleSurfaces.joinToString { it.entity.name }}" }

            // Blackout for base image.
            sendToAllReliably(brainsToMap.values) { solidColorBuffer(inactiveColor) }
            delay(1000L) // wait for focus

            // Create base image from the brightest of a few samples.
            val bitmap = getBrightImageBitmap(5)
            baseBitmap = bitmap
            deltaBitmap = NativeBitmap(bitmap.width, bitmap.height)

            val baseImageName = webSocketClient.saveImage(sessionStartTime, "base", bitmap)

            showMessage("MAPPING…")
            showStats(brainsToMap.size, 0, -1)

            try {
                logger.info { "identify surfaces..." }
                // light up each brain in an arbitrary sequence and capture its delta...
                brainsToMap.values.forEachIndexed { index, brainToMap ->
                    identifyBrain(index, brainToMap)

                    // the next line causes the UI to wait after each panel has been identified...
                    pauseForUserInteraction()

                    var retryCount = 0
                    setRedo {
                        identifyBrain(index, brainToMap, ++retryCount)
                    }

                    waitUntilUnpaused()
                    setRedo(null)

                    deliverer.send(brainToMap, solidColorBuffer(inactiveColor))
                    deliverer.await()
                }

                delay(1000L)

//            showMessage("SEEKING LIMITS…")
//            var maxPixel = 0
                // binary search for highest present pixel 0..MAX_PIXEL_COUNT…

                logger.info { "identify pixels..." }
                // light up each pixel...

                // Turn all pixels off.
                resetToBase()
                sendToAllReliably(brainsToMap.values) { it.pixelShaderBuffer }
                delay(1000L)

                val maxPixelForTheseBrains = brainsToMap.values.maxOf { it.expectedPixelCountOrDefault }
                val pixelStep = 4
                fun actualPixelIndex(pixelIndexX: Int) =
                    pixelIndexX * pixelStep % maxPixelForTheseBrains + pixelIndexX * pixelStep / maxPixelForTheseBrains


                val useBitMaskAlgorithm = true
                if (useBitMaskAlgorithm) {
                    val neededBits = ceil(log2(maxPixelForTheseBrains.toDouble())).toInt()
                    val maskBitmaps = arrayListOf<Pair<Bitmap, Bitmap>>()

                    for (b in 0 until neededBits) {
                        val bitmap0 = captureMaskedPixelsImage(b, false)
                        delay(250)
                        val bitmap1 = captureMaskedPixelsImage(b, true)
                        delay(250)
                        maskBitmaps.add(bitmap0 to bitmap1)
                    }

                    for (pixelIndex in 0 until maxPixelForTheseBrains) {
                        val progress = (pixelIndex.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
                        showMessage("MAPPING PIXEL $pixelIndex / $maxPixelForTheseBrains ($progress%)…")
                        var compositeBitmap: Bitmap? = null

                        for (b in 0 until neededBits) {
                            val shouldBeOn = pixelIndex ushr b and 1 == 1

                            val mask = maskBitmaps[b].let { if (shouldBeOn) it.first else it.second }
                            if (compositeBitmap == null) {
                                compositeBitmap = mask.clone()
                            } else {
                                compositeBitmap.darken(mask)
                            }
                            showDiffImage(compositeBitmap)
                        }

                        val analysis = stats.diffImage.time {
                            ImageProcessing.analyze(compositeBitmap!!)
                        }
                        val pixelChangeRegion = stats.detectChangeRegion.time {
                            analysis.detectChangeRegion(.9f)
                        }
                        showDiffImage(compositeBitmap!!, pixelChangeRegion)
//                        delay(500)

                        brainsToMap.values.forEach { brainToMap ->
                            val visibleSurface = brainToMap.guessedVisibleSurface

                            logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                            logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                            if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                                val centerUv = pixelChangeRegion.centerUv
                                visibleSurface?.setPixel(pixelIndex, centerUv)
                                val pixelOnImageName = "fancy-mask-pixel-$pixelIndex.png"
                                brainToMap.pixelMapData[pixelIndex] = PixelMapData(pixelChangeRegion, pixelOnImageName)
                                logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                            } else {
                                showMessage2("looks like no pixel $pixelIndex for ${brainToMap.brainId}…")
                                logger.debug { "looks like no pixel $pixelIndex for ${brainToMap.brainId}…" }
                            }
                        }
                    }
                } else {
                    for (pixelIndexX in 0 until maxPixelForTheseBrains) {
                        // Reorder so we get e.g. 0, 4, 8, ..., 1, 5, 9, ..., 2, 6, 10, ..., 3, 7, 11, ...
                        val pixelIndex = actualPixelIndex(pixelIndexX)
                        identifyPixel(pixelIndex, maxPixelForTheseBrains, pixelIndexX)

//                    pauseForUserInteraction()
                        waitUntilUnpaused()
                        allPixelsOff()
                    }
                }
                logger.info { "done identifying pixels..." }

                logger.info { "done identifying things... $isRunning" }
                showMessage("++LEVEL UNLOCKED++")

                delay(1000L)
            } catch (e: TimeoutException) {
                showMessage("Timed out: ${e.message}")
                logger.error(e) { "Timed out" }
            }

            logger.info { "Here's what we learned!" }

            val surfaces = gatherResults()

            // Show mapping diagnostic test pattern!
//            showTestPattern()

            // Save data.
            val mappingSession =
                MappingSession(
                    sessionStartTime.unixMillis,
                    surfaces,
                    cameraOrientation.cameraMatrix,
                    cameraOrientation.cameraPosition,
                    baseImageName
                )
            webSocketClient.saveSession(mappingSession)

            // We're done!

            isRunning = false
            unlockUi()

            retry { udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning)) }
        }

        private suspend fun captureMaskedPixelsImage(maskBit: Int, invert: Boolean): Bitmap {
            turnOnPixelsByMask(maskBit, invert)
            val pixelOnBitmap = stats.captureImage.stime {
                slowCamDelay()
                getBrightImageBitmap(2)
            }

            showBaseImage(baseBitmap!!)
            stats.diffImage.time {
                ImageProcessing.diff(pixelOnBitmap, baseBitmap!!, deltaBitmap)
            }
            showDiffImage(deltaBitmap)
            val thisDelta = deltaBitmap
            deltaBitmap = NativeBitmap(deltaBitmap.width, deltaBitmap.height)
            return thisDelta
        }

        private fun gatherResults(): List<MappingSession.SurfaceData> {
            val surfaces = mutableListOf<MappingSession.SurfaceData>()
            brainsToMap.forEach { (address, brainToMap) ->
                logger.info { "Brain ID: ${brainToMap.brainId} at ${address}:" }
                logger.info { "  Surface: ${brainToMap.guessedEntity}" }
                logger.debug { "  Pixels:" }

                val visibleSurface = brainToMap.guessedVisibleSurface
                if (visibleSurface != null) {
                    visibleSurface.showPixels()

//                    brainToMap.pixelMapData.forEach { (pixelIndex, mapData) ->
//                        val changeRegion = mapData.pixelChangeRegion
//                        val position = visibleSurface.translatePixelToPanelSpace(
//                            Uv.fromXY(changeRegion.centerX, changeRegion.centerY, changeRegion.sourceDimen)
//                        )
//                        logger.debug { "    $pixelIndex -> ${position?.x},${position?.y}" }
//                    }

                    val pixels = visibleSurface.pixelsInModelSpace.mapIndexed { index, modelPosition ->
                        val pixelMapData = brainToMap.pixelMapData[index]
                        val pixelChangeRegion = pixelMapData?.pixelChangeRegion
                        val screenPosition = pixelChangeRegion?.let {
                            visibleSurface.translatePixelToPanelSpace(Uv.fromXY(it.centerX, it.centerY, it.sourceDimen))
                        }
                        MappingSession.SurfaceData.PixelData(
                            modelPosition,
                            screenPosition,
                            pixelMapData?.deltaImageName
                        )
                    }

                    val surfaceData = MappingSession.SurfaceData(
                        BrainManager.controllerTypeName,
                        brainToMap.brainId,
                        visibleSurface.entity.name,
                        pixels.size,
                        pixels,
                        brainToMap.deltaImageName,
                        screenAreaInSqPixels = null,
                        screenAngle = null
                    )
                    surfaces.add(surfaceData)
                }
            }
            return surfaces
        }

        private suspend fun identifyPixel(pixelIndex: Int, maxPixelForTheseBrains: Int, pixelIndexX: Int) {
            val progress = (pixelIndexX.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
            showMessage("MAPPING PIXEL $pixelIndex / $maxPixelForTheseBrains ($progress%)…")

            if (pixelIndex % 128 == 0) logger.debug { "pixel $pixelIndex... isRunning is $isRunning" }
            stats.turnOnPixel.stime { turnOnPixel(pixelIndex) }

            val pixelOnBitmap = stats.captureImage.stime {
                slowCamDelay()
                getBrightImageBitmap(2)
            }

            // TODO: for now we're doing this later so the pixel remains lit while debugging.
//            // turn off pixel now so it doesn't leak into next frame...
//            resetToBase()
//            stats.turnOffPixel.stime {
//                sendToAllReliably(brainsWithPixel(pixelIndex)) { it.pixelShaderBuffer }
//            }
//            // we won't block here yet...

            showBaseImage(baseBitmap!!)
            stats.diffImage.time {
                ImageProcessing.diff(pixelOnBitmap, baseBitmap!!, deltaBitmap)
            }
            showDiffImage(deltaBitmap)
            val pixelOnImageName = webSocketClient.saveImage(sessionStartTime, "pixel-$pixelIndex", deltaBitmap)

            brainsToMap.values.forEach { brainToMap ->
                stats.identifyPixel.time {
                    identifyBrainPixel(pixelIndex, brainToMap, pixelOnBitmap, deltaBitmap, pixelOnImageName)
                }

                delay(1)
//                pauseForUserInteraction()
                waitUntilUnpaused()
            }

            // turn off pixel now so it doesn't leak into next frame...
            resetToBase()
            stats.turnOffPixel.stime {
                sendToAllReliably(brainsWithPixel(pixelIndex)) { it.pixelShaderBuffer }
            }
            // we won't block here yet...

            waitForDelivery() // ... of resetting to black above.
        }

        private suspend fun detectBrains(
            index: Int,
            brainToMap: BrainToMap
        ): Ballot<VisibleSurface>? {
            showMessage("MAPPING SURFACE $index / ${brainsToMap.size} (${brainToMap.brainId})…")

            deliverer.send(brainToMap, solidColorBuffer(activeColor))
            deliverer.await()
            slowCamDelay()
            slowCamDelay()
            slowCamDelay()

            val surfaceOnBitmap = getBrightImageBitmap(3)
            showSnapshot(surfaceOnBitmap)

            showBaseImage(baseBitmap!!)
            val surfaceAnalysis = ImageProcessing.diff(surfaceOnBitmap, baseBitmap!!, deltaBitmap)
            val surfaceChangeRegion = surfaceAnalysis.detectChangeRegion(.25f)
            logger.debug {
                "surfaceChangeRegion(${brainToMap.brainId}) =" +
                        " $surfaceChangeRegion ${surfaceChangeRegion.width}x${surfaceChangeRegion.height}"
            }
            showDiffImage(deltaBitmap, surfaceChangeRegion)
            showPanelMask(deltaBitmap, surfaceChangeRegion)

            brainToMap.changeRegion = surfaceChangeRegion

            val thresholdValue = surfaceAnalysis.thresholdValueFor(.25f)
            //                val pxAboveThreshold = surfaceAnalysis.hist.sumValues(thresholdValue..255)
            val sampleLocations = mutableListOf<Uv>()
            ImageProcessing.pixels(surfaceOnBitmap, surfaceChangeRegion) { x, y, value ->
                if (value >= thresholdValue && Random.nextFloat() < .05f) {
                    sampleLocations.add(Uv.fromXY(x, y, surfaceOnBitmap.dimen))
                }
            }

            if (sampleLocations.isEmpty()) {
                logger.warn { "Failed to match anything up with ${brainToMap.brainId}, bailing." }
                return null
            }

            val surfaceBallot = Ballot<VisibleSurface>()
            var tries = 1000
            while (surfaceBallot.totalVotes < 10 && tries-- > 0) {
                val uv = sampleLocations.random()
                val visibleSurface = intersectingSurface(uv, visibleSurfaces)
                val surface = visibleSurface?.entity
                surface?.let {
                    surfaceBallot.cast(surface.name, visibleSurface)
                }
            }

            if (tries == 0 || surfaceBallot.noVotes()) {
                logger.warn {
                    "Failed to cast sufficient votes (${surfaceBallot.totalVotes}) after 1000 tries" +
                            " on ${brainToMap.brainId}, bailing."
                }
                return null
            }

            return surfaceBallot
        }

        private suspend fun identifyBrain(index: Int, brainToMap: BrainToMap, retryCount: Int = 0) {
            val surfaceBallot = detectBrains(index, brainToMap)
                ?: return

            //                val orderedPanels = visibleSurfaces.map { visiblePanel ->
            //                    visiblePanel to visiblePanel.boxOnScreen.distanceTo(surfaceChangeRegion)
            //                }.sortedBy { it.second }
            //
            //                mapperUi.showCandidates(orderedPanels)
            //
            //                val firstGuess = orderedPanels.first().first
            val firstGuess = surfaceBallot.winner()
            val firstGuessSurface = firstGuess.entity

            showMessage("$index / ${brainsToMap.size}: ${brainToMap.brainId} — surface is ${firstGuessSurface.name}?")
            showMessage2("Candidate panels: ${surfaceBallot.summarize()}")

            logger.info { "Guessed panel ${firstGuessSurface.name} for ${brainToMap.brainId}" }
            brainToMap.guessedEntity = firstGuessSurface
            brainToMap.guessedVisibleSurface = firstGuess
            brainToMap.expectedPixelCount = (firstGuessSurface as? Model.Surface)?.expectedPixelCount
            brainToMap.panelDeltaBitmap = deltaBitmap.clone()
            brainToMap.deltaImageName =
                webSocketClient.saveImage(sessionStartTime, "brain-${brainToMap.brainId}-$retryCount", deltaBitmap)
        }

        private fun identifyBrainPixel(
            pixelIndex: Int,
            brainToMap: BrainToMap,
            pixelOnBitmap: Bitmap,
            deltaBitmap: Bitmap,
            pixelOnImageName: String
        ) {
            showMessage("MAPPING PIXEL $pixelIndex / $maxPixelsPerBrain (${brainToMap.brainId})…")
            val surfaceChangeRegion = brainToMap.changeRegion
            val visibleSurface = brainToMap.guessedVisibleSurface

            if (surfaceChangeRegion != null && surfaceChangeRegion.sqPix() > 0 && visibleSurface != null) {
                showPanelMask(brainToMap.panelDeltaBitmap!!)

                showBaseImage(baseBitmap!!)
                showSnapshot(pixelOnBitmap)
                val analysis = stats.diffImage.time {
                    ImageProcessing.diff(
                        pixelOnBitmap,
                        baseBitmap!!,
                        deltaBitmap,
                        brainToMap.panelDeltaBitmap!!,
                        surfaceChangeRegion
                    )
                }
                val pixelChangeRegion = stats.detectChangeRegion.time {
                    analysis.detectChangeRegion(.9f)
                }
                showDiffImage(deltaBitmap, pixelChangeRegion)
                showPanelMask(brainToMap.panelDeltaBitmap!!, pixelChangeRegion)
                logger.debug {
                    "pixelChangeRegion($pixelIndex,${brainToMap.guessedEntity?.name} =" +
                            " $pixelChangeRegion ${pixelChangeRegion.width}x${pixelChangeRegion.height}"
                }

                logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                logger.debug { "* surfaceChangeRegion=$surfaceChangeRegion" }
                if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                    val centerUv = pixelChangeRegion.centerUv
                    visibleSurface.setPixel(pixelIndex, centerUv)
                    brainToMap.pixelMapData[pixelIndex] = PixelMapData(pixelChangeRegion, pixelOnImageName)
                    logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                } else {
                    showMessage2("looks like no pixel $pixelIndex for ${brainToMap.brainId}…")
                    logger.debug { "looks like no pixel $pixelIndex for ${brainToMap.brainId}…" }
                }
            }
        }
    }

    private suspend fun slowCamDelay() {
        getImage()
        getImage()
//        getImage()
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
        pauseForUserInteraction()
        showMessage2(message)
    }

    private suspend fun waitUntilUnpaused() {
        while (isPaused) delay(50L)
        showMessage2("")
    }

    private suspend fun sendToAllReliably(
        brains: Collection<BrainToMap>,
        fn: (BrainToMap) -> BrainShader.Buffer
    ) {
        sendToAll(brains, fn)
        waitForDelivery()
    }

    private fun sendToAll(
        brains: Collection<BrainToMap>,
        fn: (BrainToMap) -> BrainShader.Buffer
    ) {
        brains.forEach {
            deliverer.send(it, fn(it))
        }
    }

    private suspend fun waitForDelivery() {
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
        return BrainShaderMessage(buf.brainShader, buf)
    }

    private fun solidColorBuffer(color: Color): BrainShader.Buffer {
        return if (USE_SOLID_SHADERS) {
            val solidShader = SolidBrainShader()
            solidShader.createBuffer(maxPixelsPerBrain).apply { this.color = color }
        } else {
            val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.INDEXED_2)
            pixelShader.createBuffer(maxPixelsPerBrain).apply {
                palette[0] = Color.BLACK
                palette[1] = color
                setAll(1)
            }
        }
    }

    private val deliverer = ReliableShaderMessageDeliverer()

    inner class ReliableShaderMessageDeliverer {
        private val outstanding = mutableMapOf<List<Byte>, DeliveryAttempt>()
        private val pongs = Channel<PingMessage>()

        fun send(brainToMap: BrainToMap, buffer: BrainShader.Buffer) {
            val deliveryAttempt = DeliveryAttempt(brainToMap, buffer)
//            logger.debug { "attempting reliable delivery with key ${deliveryAttempt.key.stringify()}" }
            outstanding[deliveryAttempt.key] = deliveryAttempt
            deliveryAttempt.attemptDelivery()
        }

        suspend fun await(retryAfterSeconds: Double = .25, failAfterSeconds: Double = 10.0) {
            logger.debug { "Waiting pongs from ${outstanding.values.map { it.brainToMap.brainId }}..." }

            outstanding.values.forEach {
                it.retryAt = it.sentAt + retryAfterSeconds
                it.failAt = it.sentAt + failAfterSeconds
            }

            while (outstanding.isNotEmpty()) {
                val waitingFor =
                    outstanding.values.map { it.brainToMap.guessedEntity?.name ?: it.brainToMap.brainId }
                        .sorted()
                showMessage2("Waiting for PONG from ${waitingFor.joinToString(",")}")
//                logger.debug { "pongs outstanding: ${outstanding.keys.map { it.stringify() }}" }

                var sleepUntil = Double.MAX_VALUE

                val now = clock.now()

                outstanding.values.removeAll {
                    if (it.failAt < now) {
                        logger.debug {
                            "Timed out waiting after ${now - it.sentAt}s for ${it.brainToMap.brainId}" +
                                    " pong ${it.key.stringify()}"
                        }
                        it.failed()
                        true
                    } else {
                        if (sleepUntil > it.failAt) sleepUntil = it.failAt

                        if (it.retryAt < now) {
                            logger.warn {
                                "Haven't heard from ${it.brainToMap.brainId} after ${now - it.sentAt}s," +
                                        " retrying (attempt ${++it.retryCount})..."
                            }
                            it.attemptDelivery()
                            it.retryAt = now + retryAfterSeconds
                        }
                        if (sleepUntil > it.retryAt) sleepUntil = it.retryAt
                        false
                    }
                }

                val timeoutSec = sleepUntil - now
//                logger.debug { "Before pongs.receive() withTimeout(${timeoutSec}s)" }
                val pong = withTimeoutOrNull(timeoutSec.asMillis()) {
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

                showMessage2("")
            }
        }

        fun gotPong(pingMessage: PingMessage) {
            launch {
                pongs.send(pingMessage)
            }
        }
    }

    class TimeoutException(message: String) : Exception(message)

    inner class DeliveryAttempt(val brainToMap: BrainToMap, val buffer: BrainShader.Buffer) {
        private val tag = Random.nextBytes(8)
        val key get() = tag.toList()
        val sentAt = clock.now()
        var retryAt = 0.0
        var failAt = 0.0
        var retryCount = 0

        fun attemptDelivery() {
            udpSocket.sendUdp(brainToMap.address, brainToMap.port, BrainShaderMessage(buffer.brainShader, buffer, tag))
        }

        fun succeeded() {
            logger.debug { "${brainToMap.brainId} shader message pong after ${clock.now() - sentAt}s" }
        }

        fun failed() {
            logger.error { "${brainToMap.brainId} shader message pong not received after ${clock.now() - sentAt}s" }
        }
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
//        logger.debug { "Mapper received message from $fromAddress:$fromPort ${bytes[0]}" }
        when (val message = parse(bytes)) {
            is BrainHelloMessage -> {
                logger.debug { "Heard from Brain ${message.brainId} surface=${message.surfaceName ?: "unknown"}" }
                val brainToMap = brainsToMap.getOrPut(fromAddress) { BrainToMap(fromAddress, message.brainId) }
                showMessage("${brainsToMap.size} SURFACES DISCOVERED!")

                // Less voltage causes less LED glitches.
                brainToMap.shade { solidColor(Color.GREEN.withBrightness(.4f)) }
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
        stats.processImage.time {
            showCamImage(image)
        }

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

    abstract fun addWireframe(model: Model)
    abstract fun showLiveCamImage(image: Image, changeRegion: MediaDevices.Region? = null)
    abstract fun showSnapshot(bitmap: Bitmap)
    abstract fun showBaseImage(bitmap: Bitmap)
    abstract fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region? = null)
    abstract fun showPanelMask(bitmap: Bitmap, changeRegion: MediaDevices.Region? = null)
    abstract fun showMessage(message: String)
    abstract fun showMessage2(message: String)
    abstract fun setRedo(fn: (suspend () -> Unit)?)
    abstract fun lockUi(): CameraOrientation
    abstract fun unlockUi()
    abstract fun getAllSurfaceVisualizers(): List<EntityDepiction>
    abstract fun getVisibleSurfaces(): List<VisibleSurface>
    abstract fun showCandidates(orderedPanels: List<Pair<VisibleSurface, Float>>)
    abstract fun intersectingSurface(uv: Uv, visibleSurfaces: List<VisibleSurface>): VisibleSurface?
    abstract fun showStats(total: Int, mapped: Int, visible: Int)
    abstract fun close()
    abstract fun addExistingSession(name: String)
    abstract fun pauseForUserInteraction()

    inner class BrainToMap(val address: Network.Address, val brainId: String) {
        val port get() = Ports.BRAIN

        var expectedPixelCount: Int? = null
        val expectedPixelCountOrDefault: Int
            get() = (guessedEntity as? Model.Surface)?.expectedPixelCount
                ?: expectedPixelCount
                ?: SparkleMotion.DEFAULT_PIXEL_COUNT

        var changeRegion: MediaDevices.Region? = null
        var guessedEntity: Model.Entity? = null
        var guessedVisibleSurface: VisibleSurface? = null
        var panelDeltaBitmap: Bitmap? = null
        var deltaImageName: String? = null
        val pixelMapData: MutableMap<Int, PixelMapData> = mutableMapOf()

        private val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.INDEXED_2)
        val pixelShaderBuffer = pixelShader.createBuffer(maxPixelsPerBrain).apply {
            palette[0] = Color.BLACK
            palette[1] = Color.WHITE
            setAll(0)
        }

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

        fun noVotes(): Boolean = box.isEmpty()

        fun winner(): T {
            return box.values.maxByOrNull { it.votes }!!.item
        }

        fun summarize(): String {
            return box.entries
                .sortedByDescending { (_, v) -> v.votes }
                .joinToString(", ") { (k, v) -> "$k=${v.votes}" }
        }

        private class Vote<T>(val item: T) {
            var votes = 0
        }
    }

    companion object {
        val mapperStats = MapperStats()
        val logger = Logger<Mapper>()

        private const val maxPixelsPerBrain = SparkleMotion.MAX_PIXEL_COUNT
    }

    fun List<Byte>.stringify(): String {
        return joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
    }


    inner class Facade : baaahs.ui.Facade() {
    }

    interface EntityDepiction {
        val entity: Model.Entity

        fun setPixel(index: Int, modelPosition: Vector3F?)
        fun setPixels(pixels: List<MappingSession.SurfaceData.PixelData?>)
        fun showPixels()
        fun resetPixels()
    }

    interface VisibleSurface {
        val entity: Model.Entity
        val boxOnScreen: MediaDevices.Region
        val pixelsInModelSpace: List<Vector3F?>
        fun translatePixelToPanelSpace(uv: Uv): Vector2F?
        fun setPixel(pixelIndex: Int, uv: Uv)
        fun showPixels()
        fun hidePixels()
        fun resetPixels()
    }

    interface CameraOrientation {
        val cameraMatrix: Matrix4F
        val cameraPosition: CameraPosition
        val aspect: Double
    }
}

class MapperStats : Stats() {
    val turnOnPixel by statistic
    val captureImage by statistic
    val turnOffPixel by statistic
    val processImage by statistic
    val diffImage by statistic
    val identifyPixel by statistic
    val detectChangeRegion by statistic
}

/**
 * @param u Horizontal coordinate [0,1) where 0 is left.
 * @param v Vertical coordinate [0,1) where 0 is top.
 */
data class Uv(val u: Float, val v: Float) {
    companion object {
        fun fromXY(x: Number, y: Number, screenDimen: Dimen): Uv =
            Uv(x.toFloat() / screenDimen.width, y.toFloat() / screenDimen.height)
    }
}