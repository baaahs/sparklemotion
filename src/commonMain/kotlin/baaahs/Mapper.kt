package baaahs

import baaahs.geom.Matrix4
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.imaging.Bitmap
import baaahs.imaging.Image
import baaahs.imaging.NativeBitmap
import baaahs.mapper.*
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelShader
import baaahs.shaders.SolidShader
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.random.Random

class Mapper(
    private val network: Network,
    sheepModel: SheepModel,
    private val mapperDisplay: MapperDisplay,
    mediaDevices: MediaDevices,
    private val pinkyAddress: Network.Address
) : Network.UdpListener, MapperDisplay.Listener, CoroutineScope by MainScope() {
    //    private val maxPixelsPerBrain = SparkleMotion.MAX_PIXEL_COUNT
    private val maxPixelsPerBrain = 120

    // TODO: getCamera should just return max available size?
    val camera = mediaDevices.getCamera().apply {
        onImage = { image -> haveImage(image) }
    }
    private var baseBitmap: Bitmap? = null

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private lateinit var mapperClient: MapperClient
    private var isRunning: Boolean = true
    private var isAligned: Boolean = false
    private var isPaused: Boolean = false
    private var newIncomingImage: Image? = null

    private var suppressShowsJob: Job? = null
    private val brainMappers: MutableMap<Network.Address, BrainMapping> = mutableMapOf()

    private val activeColor = Color(0x07, 0xFF, 0x07)
    private val inactiveColor = Color(0x07, 0x00, 0x07)

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
        mapperDisplay.listen(this)
        mapperDisplay.addWireframe(sheepModel)
    }

    fun start() = doRunBlocking {
        link = FragmentingUdpLink(network.link())
        udpSocket = link.listenUdp(0, this)
        mapperClient = MapperClient(link, pinkyAddress)

        launch { run() }
    }

    override fun onStart() {
        if (!isRunning) {
            // Restart.
            isAligned = false
            isRunning = true
            launch { run() }
        } else {
            isAligned = true
        }
    }

    override fun onPause() {
        isPaused = !isPaused
    }

    override fun onStop() {
        isAligned = false
        onClose()
    }

    override fun onClose() {
        println("Shutting down Mapper...")
        isRunning = false
        camera.close()

        suppressShowsJob?.cancel()
        udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(false))

        mapperDisplay.close()
    }

    suspend fun run() {
        mapperDisplay.showMessage("ESTABLISHING UPLINK…")

        // shut down Pinky, advertise for Brains...
        retry {
            udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(true))
            delay(1000L)
            udpSocket.broadcastUdp(Ports.BRAIN, solidColor(inactiveColor))
        }

        // keep Pinky from waking up while we're running...
        suppressShows()

        retry {
            udpSocket.broadcastUdp(Ports.BRAIN, BrainIdRequest())
            delay(1000L)
        }

        // wait for responses from Brains
        delay(1000L)

        // Blackout
        retry { udpSocket.broadcastUdp(Ports.BRAIN, solidColor(inactiveColor)); delay(250L) }
        delay(250L)

        mapperDisplay.showMessage("READY PLAYER ONE…")
        // Blackout
        retry { udpSocket.broadcastUdp(Ports.BRAIN, solidColor(activeColor)); delay(250L) }
        delay(250L)

        while (!isAligned) {
            delay(500)

            if (Random.nextFloat() < .1) {
                mapperDisplay.showMessage("READY PLAYER ONE…")
            } else if (Random.nextFloat() < .1) {
                mapperDisplay.showMessage("ALIGN THY SHEEP…")
            }
        }

        val sessionStartTime = DateTime.now()

        mapperDisplay.showMessage("CALIBRATING…")
        val visibleSurfaces = mapperDisplay.getVisibleSurfaces()
        println("Visible surfaces: ${visibleSurfaces.map { it.modelSurface.name }.joinToString()}")

        // Blackout
        retry { udpSocket.broadcastUdp(Ports.BRAIN, solidColor(inactiveColor)); delay(250L) }
        delay(1000L) // wait for focus

        val bitmap = getImage().toBitmap()
        baseBitmap = bitmap
        val baseImageName = mapperClient.saveImage(sessionStartTime, "base", bitmap)
        val deltaBitmap: Bitmap = NativeBitmap(bitmap.width, bitmap.height)

        mapperDisplay.lockUi()
        mapperDisplay.showMessage("MAPPING…")
        mapperDisplay.showStats(brainMappers.size, 0, -1)

        val surfaceScheme = Detector.GREEN

        while (isRunning) {
            println("identify surfaces...")
            // light up each brain in an arbitrary sequence and capture its delta...
            brainMappers.values.forEachIndexed { index, brainMapper ->
                mapperDisplay.showMessage("MAPPING SURFACE $index / ${brainMappers.size} (${brainMapper.brainId})…")

                retry { brainMapper.shade { solidColor(activeColor) } }
                delay(200L)

                var panelOnImage: Image = getImage()
                var tries = 5
                while (
                    ImageProcessing.findChanges(
                        panelOnImage.toBitmap(),
                        baseBitmap!!,
                        deltaBitmap,
                        surfaceScheme
                    ).isEmpty()
                    && tries-- > 0
                ) {
                    mapperDisplay.showDiffImage(deltaBitmap)
                    println("No changes detected for brain ${brainMapper.brainId}, trying again...")
                    panelOnImage = getImage()
                }

                // wait for a new image to come in...
                val panelOnBitmap = panelOnImage.toBitmap()
                val changeRegion = ImageProcessing.findChanges(
                    panelOnBitmap,
                    baseBitmap!!,
                    deltaBitmap,
                    surfaceScheme
                )
                mapperDisplay.showDiffImage(deltaBitmap, changeRegion)

                brainMapper.changeRegion = changeRegion
                mapperDisplay.showCamImage(panelOnImage, changeRegion)

                val orderedPanels = visibleSurfaces.map() { visiblePanel ->
                    visiblePanel to visiblePanel.boxOnScreen.distanceTo(changeRegion)
                }.sortedBy { it.second }

                mapperDisplay.showCandidates(orderedPanels)

                val firstGuess = orderedPanels.first().first
                val firstGuessSurface = firstGuess.modelSurface

                mapperDisplay.showMessage2(
                    "Candidate panels: ${orderedPanels.subList(
                        0,
                        min(5, orderedPanels.size)
                    ).map { firstGuessSurface.name }}"
                )

                println("Guessed panel ${firstGuessSurface.name} for ${brainMapper.brainId}")
                brainMapper.guessedModelSurface = firstGuessSurface
                brainMapper.guessedVisibleSurface = firstGuess
                brainMapper.panelDeltaBitmap = deltaBitmap
                brainMapper.deltaImageName =
                    mapperClient.saveImage(sessionStartTime, "brain-${brainMapper.brainId}", deltaBitmap)

                maybePause()
                retry { brainMapper.shade { solidColor(inactiveColor) } }
            }

            delay(1000L)

//            mapperDisplay.showMessage("SEEKING LIMITS…")
//            var maxPixel = 0
            // binary search for highest present pixel 0..MAX_PIXEL_COUNT…

            println("identify pixels...")
            // light up each pixel...
            val pixelShader = PixelShader(PixelShader.Encoding.INDEXED_4)
            val buffer = pixelShader.createBuffer(object : Surface {
                override val pixelCount = SparkleMotion.MAX_PIXEL_COUNT

                override fun describe(): String = "Mapper surface"
            }).apply {
                palette[0] = detectors[0].alternateColor
                palette[1] = detectors[1].alternateColor
                palette[2] = detectors[2].alternateColor
                palette[3] = Color.WHITE
                setAll(0)
            }
            val whitePaletteIndex = 3

            fun resetToBase() {
                buffer.indices.forEach { buffer[it] = it % 3 }
            }

            resetToBase()
            udpSocket.broadcastUdp(Ports.BRAIN, BrainShaderMessage(pixelShader, buffer))
            delay(1000L)
            println("getImage took ${time { getImage() }}")
            println("getImage took ${time { getImage() }}")
            println("getImage took ${time { getImage() }}")
            println("getImage took ${time { getImage() }}")
            println("getImage took ${time { getImage() }}")
            getImage()
            baseBitmap = getImage().toBitmap()

            val pixelStep = 4
            fun actualPixelIndex(pixelIndexX: Int) =
                pixelIndexX * pixelStep % maxPixelsPerBrain + pixelIndexX * pixelStep / maxPixelsPerBrain
            fun turnOnPixel(pixelIndex: Int) {
                resetToBase()
                buffer[pixelIndex] = whitePaletteIndex
                udpSocket.broadcastUdp(Ports.BRAIN, BrainShaderMessage(pixelShader, buffer))
            }

            for (pixelIndexX in 0 until maxPixelsPerBrain) {
                // Reorder so we get e.g. 0, 4, 8, ..., 1, 5, 9, ..., 2, 6, 10, ..., 3, 7, 11, ...
                val pixelIndex = actualPixelIndex(pixelIndexX)

                val detector = detectors[pixelIndex % detectors.size]
                mapperDisplay.showMessage("MAPPING PIXEL $pixelIndex / $maxPixelsPerBrain ($detector)…")

                if (pixelIndex % 128 == 0) println("pixel $pixelIndex... isRunning is $isRunning")
                turnOnPixel(pixelIndex)

                var pixelOnImage = getImage()
                var tries = 5
                while (
                    ImageProcessing.findChanges(pixelOnImage.toBitmap(), baseBitmap!!, deltaBitmap, detector).isEmpty()
                    && tries-- > 0
                ) {
                    mapperDisplay.showDiffImage(deltaBitmap)
                    println("No changes detected for pixel $pixelIndex, trying again...")
                    pixelOnImage = getImage()
                }

                val nextPixelIndex = actualPixelIndex(pixelIndexX + 1)
                if (nextPixelIndex < maxPixelsPerBrain) turnOnPixel(pixelIndex)

                brainMappers.values.forEach { brainMapper ->
                    val surfaceChangeRegion = brainMapper.changeRegion
                    val visibleSurface = brainMapper.guessedVisibleSurface

                    if (surfaceChangeRegion != null && surfaceChangeRegion.sqPix() > 0 && visibleSurface != null) {
                        val pixelOnBitmap = pixelOnImage.toBitmap()
                        val pixelOnImageName =
                            mapperClient.saveImage(sessionStartTime, "pixel-$pixelIndex", deltaBitmap)
                        val pixelChangeRegion =
                            ImageProcessing.findChanges(
                                pixelOnBitmap,
                                baseBitmap!!,
                                deltaBitmap,
                                detector,
                                surfaceChangeRegion
                            )
                        mapperDisplay.showDiffImage(deltaBitmap, pixelChangeRegion)

                        if (!pixelChangeRegion.isEmpty()) {
                            val center = SheepModel.Point(
                                (pixelChangeRegion.centerX - surfaceChangeRegion.x0) / surfaceChangeRegion.width.toFloat(),
                                (pixelChangeRegion.centerY - surfaceChangeRegion.y0) / surfaceChangeRegion.height.toFloat(),
                                0f
                            )

                            visibleSurface.addPixel(
                                pixelIndex,
                                pixelChangeRegion.centerX.toFloat(),
                                pixelChangeRegion.centerY.toFloat()
                            )
                            brainMapper.pixelMapData[pixelIndex] = PixelMapData(pixelChangeRegion, pixelOnImageName)
                            println("$pixelIndex/${brainMapper.brainId}: center = $center")
                        }
                    }
                }

                delay(100L)
                maybePause()
            }
            println("done identifying pixels...")

            delay(1000L)

            isRunning = false
        }
        println("done identifying things... $isRunning")
        mapperDisplay.showMessage("++LEVEL UNLOCKED++")

        mapperDisplay.unlockUi()

        retry { udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning)) }

        println("Here's what we learned!")

        val surfaces = mutableListOf<MappingSession.SurfaceData>()
        brainMappers.forEach { (address, brainMapper) ->
            println("Brain ID: ${brainMapper.brainId} at ${address}:")
            println("  Surface: ${brainMapper.guessedModelSurface}")
            println("  Pixels:")

            val visibleSurface = brainMapper.guessedVisibleSurface
            if (visibleSurface != null) {
                visibleSurface.showPixels()

                brainMapper.pixelMapData.forEach { (pixelIndex, mapData) ->
                    val changeRegion = mapData.pixelChangeRegion
                    val position = visibleSurface.translatePixelToPanelSpace(
                        changeRegion.centerX.toFloat(),
                        changeRegion.centerY.toFloat()
                    )
                    println("    $pixelIndex -> ${position?.x},${position?.y}")
                }

                val pixels = visibleSurface.pixelsInModelSpace.mapIndexed { index, vector3F ->
                    val pixelMapData = brainMapper.pixelMapData[index]
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
                        brainMapper.brainId,
                        visibleSurface.modelSurface.name,
                        pixels,
                        brainMapper.deltaImageName
                    )
                )
            }
        }

        val cameraMatrix = Matrix4(arrayOf())
        val mappingSession = MappingSession(sessionStartTime.unixMillis, surfaces, cameraMatrix, baseImageName)
        mapperClient.saveSession(mappingSession)
    }

    private suspend fun retry(fn: suspend () -> Unit) {
        fn()
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

    private suspend fun maybePause() {
        while (isPaused) {
            delay(100L)
        }
    }

    private fun solidColor(color: Color): BrainShaderMessage {
        val solidShader = SolidShader()
        val buffer = solidShader.createBuffer(object : Surface {
            override val pixelCount = SparkleMotion.MAX_PIXEL_COUNT

            override fun describe(): String = "Mapper surface"
        }).apply { this.color = color }
        return BrainShaderMessage(solidShader, buffer)
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        println("Mapper received message from $fromAddress:$fromPort ${bytes[0]}")
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> {
                println("Heard from Brain ${message.brainId} surface=${message.surfaceName ?: "unknown"}")
                val brainMapper = brainMappers.getOrPut(fromAddress) { BrainMapping(fromAddress, message.brainId) }
                brainMapper.shade { solidColor(Color.GREEN) }
            }

            is PinkyPongMessage -> {
                message.brainIds.forEach { id ->
                    println("id = ${id}")
//                    display.
                }
            }
        }
    }

    private fun haveImage(image: Image) {
//        println("image: $image")
        mapperDisplay.showCamImage(image)

        newIncomingImage = image
    }

    private suspend fun getImage(): Image {
        newIncomingImage = null

        while (newIncomingImage == null) {
            delay(2L)
        }

        val image = newIncomingImage!!
        newIncomingImage = null
        return image
    }

    inner class BrainMapping(private val address: Network.Address, val brainId: String) {
        var changeRegion: MediaDevices.Region? = null
        var guessedModelSurface: Model.Surface? = null
        var guessedVisibleSurface: MapperDisplay.VisibleSurface? = null
        var panelDeltaBitmap: Bitmap? = null
        var deltaImageName: String? = null
        val pixelMapData: MutableMap<Int, PixelMapData> = mutableMapOf()

        fun shade(shaderMessage: () -> BrainShaderMessage) {
            udpSocket.sendUdp(address, Ports.BRAIN, shaderMessage())
        }
    }

    class PixelMapData(val pixelChangeRegion: MediaDevices.Region, val deltaImageName: String)
}

interface MapperDisplay {
    fun listen(listener: Listener)

    fun addWireframe(sheepModel: SheepModel)
    fun showCamImage(image: Image, changeRegion: MediaDevices.Region? = null)
    fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region? = null)
    fun showMessage(message: String)
    fun showMessage2(message: String)
    fun lockUi(): CameraOrientation
    fun unlockUi()
    fun getVisibleSurfaces(): List<VisibleSurface>
    fun showCandidates(orderedPanels: List<Pair<VisibleSurface, Float>>)
    fun showStats(total: Int, mapped: Int, visible: Int)
    fun close()

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
    }
}
