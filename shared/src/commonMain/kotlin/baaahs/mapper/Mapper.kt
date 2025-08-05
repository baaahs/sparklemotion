package baaahs.mapper

import baaahs.MediaDevices
import baaahs.SparkleMotion
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.imaging.Bitmap
import baaahs.imaging.Dimen
import baaahs.imaging.Image
import baaahs.imaging.createWritableBitmap
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.SceneProvider
import baaahs.shaders.SolidBrainShader
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.proto.BrainShader
import baaahs.ui.Observable
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Stats
import kotlinx.coroutines.*
import kotlin.random.Random

/** [SolidBrainShader] appears to be busted as of 2020/09. */
const val USE_SOLID_SHADERS = false

interface MapperBuilder {
    fun build(): Mapper
}

abstract class Mapper(
    private val plugins: Plugins,
    private val network: Network,
    private val sceneProvider: SceneProvider,
    private val mediaDevices: MediaDevices,
    private val pinkyAddress: Network.Address,
    private val clock: Clock,
    private val mapperScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : Observable(), CoroutineScope by MainScope() {
    private val facade = Facade()

    abstract val ui: MapperUi

    private var selectedDevice: MediaDevices.Device? = null

    // TODO: getCamera should just return max available size?
    lateinit var camera: MediaDevices.Camera

    protected lateinit var mapperBackend: MapperBackend
    protected lateinit var udpSockets: UdpSockets
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false
    private var newIncomingImage: Image? = null

    private var suppressShowsJob: Job? = null
    private val brainsToMap: MutableMap<Network.Address, MappableBrain> = mutableMapOf()

    private val sessions: MutableList<String> = arrayListOf()
    private val stats = mapperStats

    abstract var devices: List<MediaDevices.Device>

    var model: Model? = null
    var mappingStrategy: MappingStrategy = TwoLogNMappingStrategy
    var mappingController: MappableBrain? = null

    init {
        sceneProvider.addObserver(fireImmediately = false) {
            setModel()
        }
    }

    open fun onLaunch() {
        mapperScope.launch {
            setModel()
            start()
        }
    }

    private fun setModel() {
        sceneProvider.openScene?.model?.let {
            model = it
            ui.addWireframe(it)
        }
    }

    fun start() {
        val link = network.link("mapper")
        udpSockets = UdpSockets(link, clock, this, ui) { mappableBrain ->
            brainsToMap[mappableBrain.address] = mappableBrain

            ui.showMessage("${brainsToMap.size} SURFACES DISCOVERED!")

            mappableBrain.shadeSolidColor()
        }
        mapperBackend = MapperBackend(plugins, clock, link, pinkyAddress, udpSockets)

        launch {
            mapperBackend.listSessions().forEach {
                ui.addExistingSession(it)
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
        mapperBackend.adviseMapperStatus(false)

        ui.close()
    }

    abstract fun onNewSession(sessionName: String, mappingSession: MappingSession)

    fun useCamera(selectedDevice: MediaDevices.Device?) {
        this.selectedDevice = selectedDevice
        openCamera()
    }

    suspend fun loadMappingSession(name: String): MappingSession =
        withContext(mapperScope.coroutineContext) {
            mapperBackend.loadSession(name)
        }

    private fun openCamera() {
        if (this::camera.isInitialized) camera.close()
        camera = mediaDevices.getCamera(selectedDevice).apply {
            onImage = { image -> haveImage(image) }
        }
    }

    fun showCamImage(image: Image, changeRegion: MediaDevices.Region? = null) {
        ui.showLiveCamImage(image, changeRegion)
    }

    private suspend fun startNewSession() {
        ui.showMessage("ESTABLISHING UPLINK…")

        // shut down Pinky, advertise for Brains...
        retry {
            mapperBackend.adviseMapperStatus(true)
            delay(1000L)
            udpSockets.allDark()
        }

        // keep Pinky from waking up while we're running...
        suppressShows()

        // wait for responses from Brains
        pauseForUserInteraction("PRESS PLAY WHEN ALL SURFACES ARE GREEN")
        val brainIdRequestJob = coroutineScope {
            launch {
                while (isPaused) {
                    udpSockets.requestBrainIds()
                    delay(1000L)
                }
            }
        }

        ui.showMessage("${brainsToMap.size} SURFACES DISCOVERED!")
        waitUntilUnpaused()
        brainIdRequestJob.cancelAndJoin()

        if (brainsToMap.isEmpty()) {
            ui.showMessage("NO SURFACES DISCOVERED! TRY AGAIN!")
            isRunning = false
            return
        }

        ui.showMessage("READY PLAYER ONE…")
        pauseForUserInteraction("ALIGN MODEL AND PRESS PLAY WHEN READY")
        waitUntilUnpaused()

        coroutineScope {
            Session(this).start()
        }
    }

    inner class Session(
        scope: CoroutineScope
    ) {
        internal val sessionStartTime = clock.now()
        private val mappingStrategySession = mappingStrategy.beginSession(
            scope, this@Mapper, this@Session, stats, ui, brainsToMap, mapperBackend
        )

        private val visibleSurfaces = ui.getVisibleSurfaces()
        internal var baseBitmap: Bitmap? = null
        private val cameraPosition = ui.lockUi()
        internal lateinit var deltaBitmap: Bitmap
        var metadata: MappingStrategy.SessionMetadata? = null

        internal fun resetToBase() {
            brainsToMap.values.forEach {
                it.pixelShaderBuffer.setAll(0)
            }
        }

        internal fun brainsWithPixel(pixelIndex: Int) =
            brainsToMap.values.filter { pixelIndex < it.expectedPixelCountOrDefault }

        internal suspend fun turnOnPixel(pixelIndex: Int) {
            resetToBase()

            val relevantBrains = brainsWithPixel(pixelIndex)
            relevantBrains.forEach {
                it.pixelShaderBuffer[pixelIndex] = 1
            }

            sendToAllReliably(relevantBrains) { it.pixelShaderBuffer }
        }

        suspend fun start() {
            ui.showMessage("CALIBRATING…")
            logger.info { "Visible surfaces: ${visibleSurfaces.joinToString { it.entity.name }}" }

            // Blackout for base image.
            sendToAllReliably(brainsToMap.values) { MapperUtil.solidColorBuffer(MapperUtil.inactiveColor) }
            delay(1000L) // wait for focus

            // Create base image from the brightest of a few samples.
            val bitmap = getBrightImageBitmap(5)
            baseBitmap = bitmap
            deltaBitmap = createWritableBitmap(bitmap.width, bitmap.height)

            val baseImageName = mapperBackend.saveImage(sessionStartTime, "base", bitmap)

            ui.showMessage("MAPPING…")
            ui.showStats(brainsToMap.size, 0, -1)

            try {
                logger.info { "identify surfaces..." }
                // light up each brain in an arbitrary sequence and capture its delta...
                brainsToMap.values.forEachIndexed { index, brainToMap ->
                    mappingController = brainToMap
                    identifyBrain(index, brainToMap)

                    // the next line causes the UI to wait after each panel has been identified...
                    pauseForUserInteraction()

                    var retryCount = 0
                    ui.setRedo {
                        identifyBrain(index, brainToMap, ++retryCount)
                    }

                    waitUntilUnpaused()
                    ui.setRedo(null)

                    udpSockets.deliverer.send(brainToMap, MapperUtil.solidColorBuffer(MapperUtil.inactiveColor))
                    udpSockets.deliverer.await()
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

                mappingStrategySession.capturePixelData()
                logger.info { "done identifying pixels..." }

                logger.info { "done identifying things... $isRunning" }
                ui.showMessage("++LEVEL UNLOCKED++")

                delay(10000L)
            } catch (e: TimeoutException) {
                ui.showMessage("Timed out: ${e.message}")
                logger.error(e) { "Timed out" }
            }

            logger.info { "Here's what we learned!" }

            val surfaces = gatherResults()

            // Show mapping diagnostic test pattern!
//            showTestPattern()

            // Save data.
            val mappingSession =
                MappingSession(
                    sessionStartTime,
                    surfaces,
                    null,
                    cameraPosition,
                    baseImageName,
                    metadata,
                    savedAt = clock.now()
                )
            val sessionName: String = mapperBackend.saveSession(mappingSession)
            onNewSession(sessionName, mappingSession)

            // We're done!

            isRunning = false
            ui.unlockUi()

            retry {
                mapperBackend.adviseMapperStatus(isRunning)
            }
        }


        private fun gatherResults(): List<MappingSession.SurfaceData> {
            val surfaces = mutableListOf<MappingSession.SurfaceData>()
            brainsToMap.forEach { (address, mappableBrain) ->
                logger.info { "Brain ID: ${mappableBrain.brainId} at ${address}:" }
                logger.info { "  Surface: ${mappableBrain.guessedEntity}" }
                logger.debug { "  Pixels:" }

                val visibleSurface = mappableBrain.guessedVisibleSurface
                if (visibleSurface != null) {
                    visibleSurface.showPixels()

//                    mappableBrain.pixelMapData.forEach { (pixelIndex, mapData) ->
//                        val changeRegion = mapData.pixelChangeRegion
//                        val position = visibleSurface.translatePixelToPanelSpace(
//                            Uv.fromXY(changeRegion.centerX, changeRegion.centerY, changeRegion.sourceDimen)
//                        )
//                        logger.debug { "    $pixelIndex -> ${position?.x},${position?.y}" }
//                    }

                    val pixels = visibleSurface.pixelsInModelSpace.mapIndexed { index, modelPosition ->
                        val pixelMapData = mappableBrain.pixelMapData[index]
                        val pixelChangeRegion = pixelMapData?.pixelChangeRegion
                        val screenPosition = pixelChangeRegion?.let {
                            visibleSurface.translatePixelToPanelSpace(Uv.fromXY(it.centerX, it.centerY, it.sourceDimen))
                        }
                        MappingSession.SurfaceData.PixelData(
                            modelPosition,
                            screenPosition,
                            null,
                            pixelMapData?.mappingStrategyData
                        )
                    }

                    val surfaceData = MappingSession.SurfaceData(
                        BrainManager.controllerTypeName,
                        mappableBrain.brainId,
                        visibleSurface.entity.name,
                        pixels.size,
                        mappableBrain.pixelFormat,
                        pixels,
                        mappableBrain.deltaImageName,
                        screenAreaInSqPixels = null,
                        screenAngle = null
                    )
                    surfaces.add(surfaceData)
                }
            }
            return surfaces
        }

        private suspend fun detectBrains(
            index: Int,
            mappableBrain: MappableBrain
        ): Ballot<VisibleSurface>? {
            ui.showMessage("MAPPING SURFACE $index / ${brainsToMap.size} (${mappableBrain.brainId})…")

            udpSockets.deliverer.send(mappableBrain, MapperUtil.solidColorBuffer(MapperUtil.activeColor))
            udpSockets.deliverer.await()
            slowCamDelay()
            slowCamDelay()
            slowCamDelay()

            val surfaceOnBitmap = getBrightImageBitmap(3)
            ui.showSnapshot(surfaceOnBitmap)

            ui.showBaseImage(baseBitmap!!)
            val surfaceAnalysis = ImageProcessing.diff(surfaceOnBitmap, baseBitmap!!, deltaBitmap)
            val surfaceChangeRegion = surfaceAnalysis.detectChangeRegion(.25f)
            logger.debug {
                "surfaceChangeRegion(${mappableBrain.brainId}) =" +
                        " $surfaceChangeRegion ${surfaceChangeRegion.width}x${surfaceChangeRegion.height}"
            }
            ui.showDiffImage(deltaBitmap, surfaceChangeRegion)
            ui.showPanelMask(deltaBitmap, surfaceChangeRegion)

            mappableBrain.changeRegion = surfaceChangeRegion

            val thresholdValue = surfaceAnalysis.thresholdValueFor(.25f)
            //                val pxAboveThreshold = surfaceAnalysis.hist.sumValues(thresholdValue..255)
            val sampleLocations = mutableListOf<Uv>()
            ImageProcessing.pixels(surfaceOnBitmap, surfaceChangeRegion) { x, y, value ->
                if (value >= thresholdValue && Random.nextFloat() < .05f) {
                    sampleLocations.add(Uv.fromXY(x, y, surfaceOnBitmap.dimen))
                }
            }

            if (sampleLocations.isEmpty()) {
                logger.warn { "Failed to match anything up with ${mappableBrain.brainId}, bailing." }
                return null
            }

            val surfaceBallot = Ballot<VisibleSurface>()
            var tries = 1000
            while (surfaceBallot.totalVotes < 10 && tries-- > 0) {
                val uv = sampleLocations.random()
                val visibleSurface = ui.intersectingSurface(uv, visibleSurfaces)
                val surface = visibleSurface?.entity
                surface?.let {
                    surfaceBallot.cast(surface.name, visibleSurface)
                }
            }

            if (tries == 0 || surfaceBallot.noVotes()) {
                logger.warn {
                    "Failed to cast sufficient votes (${surfaceBallot.totalVotes}) after 1000 tries" +
                            " on ${mappableBrain.brainId}, bailing."
                }
                return null
            }

            return surfaceBallot
        }

        private suspend fun identifyBrain(index: Int, mappableBrain: MappableBrain, retryCount: Int = 0) {
            val surfaceBallot = detectBrains(index, mappableBrain)
                ?: return

            //                val orderedPanels = visibleSurfaces.map { visiblePanel ->
            //                    visiblePanel to visiblePanel.boxOnScreen.distanceTo(surfaceChangeRegion)
            //                }.sortedBy { it.second }
            //
            //                mapperUi.showCandidates(orderedPanels)
            //
            //                val firstGuess = orderedPanels.first().first
            val firstGuess = surfaceBallot.winner()
            val firstGuessEntity = firstGuess.entity
            val firstGuessSurface = firstGuessEntity as? Model.Surface
            val defaultFixtureOptions = firstGuessSurface?.defaultFixtureOptions

            ui.showMessage("$index / ${brainsToMap.size}: ${mappableBrain.brainId} — surface is ${firstGuessEntity.name}?")
            ui.showMessage2("Candidate panels: ${surfaceBallot.summarize()}")

            logger.info { "Guessed panel ${firstGuessEntity.name} for ${mappableBrain.brainId}" }
            mappableBrain.guessedEntity = firstGuessEntity
            mappableBrain.guessedVisibleSurface = firstGuess
            mappableBrain.expectedPixelCount = firstGuessSurface?.expectedPixelCount
                ?: defaultFixtureOptions?.pixelCount
            mappableBrain.pixelFormat = defaultFixtureOptions?.pixelFormat
            mappableBrain.panelDeltaBitmap = deltaBitmap.clone()
            mappableBrain.deltaImageName =
                mapperBackend.saveImage(sessionStartTime, "brain-${mappableBrain.brainId}-$retryCount", deltaBitmap)
            mappingStrategySession.captureControllerData(mappableBrain)
        }
    }

    suspend fun slowCamDelay() {
        getImage()
        getImage()
//        getImage()
    }

    suspend fun getBrightImageBitmap(samples: Int): Bitmap {
        val bitmap = getImage().toBitmap()
        for (i in 1 until samples) {
            bitmap.lighten(getImage().toBitmap())
        }
        return bitmap
    }

    fun pauseForUserInteraction(message: String = "PRESS PLAY WHEN READY") {
        isPaused = true
        ui.pauseForUserInteraction()
        ui.showMessage2(message)
    }

    internal suspend fun waitUntilUnpaused() {
        while (isPaused) delay(50L)
        ui.showMessage2("")
    }

    internal suspend fun sendToAllReliably(
        brains: Collection<MappableBrain>,
        fn: (MappableBrain) -> BrainShader.Buffer
    ) {
        sendToAll(brains, fn)
        waitForDelivery()
    }

    private fun sendToAll(
        brains: Collection<MappableBrain>,
        fn: (MappableBrain) -> BrainShader.Buffer
    ) {
        brains.forEach {
            udpSockets.deliverer.send(it, fn(it))
        }
    }

    internal suspend fun waitForDelivery() {
        udpSockets.deliverer.await()
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
                mapperBackend.adviseMapperStatus(isRunning)
            }
        }
    }

    class TimeoutException(message: String) : Exception(message)


    private fun haveImage(image: Image) {
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

    class PixelMapData(
        val pixelChangeRegion: MediaDevices.Region,
        val mappingStrategyData: MappingStrategy.PixelMetadata
    )

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

        internal const val maxPixelsPerBrain = SparkleMotion.MAX_PIXEL_COUNT
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

fun List<Byte>.stringify(): String {
    return joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
}