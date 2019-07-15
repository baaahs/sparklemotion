package baaahs

import baaahs.imaging.Bitmap
import baaahs.imaging.Image
import baaahs.imaging.NativeBitmap
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelShader
import baaahs.shaders.SolidShader
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.random.Random

class Mapper(
    private val network: Network,
    sheepModel: SheepModel,
    private val mapperDisplay: MapperDisplay,
    mediaDevices: MediaDevices
) : Network.UdpListener, MapperDisplay.Listener, CoroutineScope by MainScope() {
    private val maxPixelsPerBrain = SparkleMotion.MAX_PIXEL_COUNT
    val width = 640
    val height = 300

    val camera = mediaDevices.getCamera(width, height).apply {
        onImage = { image -> haveImage(image) }
    }
    private var baseBitmap: Bitmap? = null
    private lateinit var deltaBitmap: Bitmap
    private var newChangeRegion: MediaDevices.Region? = null

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private var isRunning: Boolean = true
    private var isAligned: Boolean = false
    private var isPaused: Boolean = false
    private var captureBaseImage = false

    private var suppressShowsJob: Job? = null
    private val brainMappers: MutableMap<Network.Address, BrainMapper> = mutableMapOf()

    init {
        mapperDisplay.listen(this)
        mapperDisplay.addWireframe(sheepModel)
    }

    fun start() = doRunBlocking {
        link = FragmentingUdpLink(network.link())
        udpSocket = link.listenUdp(0, this)

        launch { run() }
    }

    override fun onStart() {
        isAligned = true
    }

    override fun onPause() {
        isPaused = !isPaused
    }

    override fun onStop() {
        isAligned = false
    }

    override fun onClose() {
        println("Shutting down Mapper...")
        isRunning = false
        camera.close()

        suppressShowsJob?.cancel()
        udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(false))

        mapperDisplay.close()
    }

    private val retries = (0..1)

    suspend fun run() {
        mapperDisplay.showMessage("ESTABLISHING UPLINK…")

        // shut down Pinky, advertise for Brains...
        retry {
            udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(true))
            delay(1000L)
            udpSocket.broadcastUdp(Ports.BRAIN, solidColor(Color.BLACK))
        }

        // keep Pinky from waking up while we're running...
        suppressShows()

        retry {
            udpSocket.broadcastUdp(Ports.BRAIN, BrainIdRequest(udpSocket.serverPort))
            delay(1000L)
        }

        // wait for responses from Brains
        delay(1000L)

        // Blackout
        retry { udpSocket.broadcastUdp(Ports.BRAIN, solidColor(Color.BLACK)); delay(250L) }
        delay(250L)

        mapperDisplay.showMessage("READY PLAYER ONE…")
        // Blackout
        retry { udpSocket.broadcastUdp(Ports.BRAIN, solidColor(Color.WHITE)); delay(250L) }
        delay(250L)

        while (!isAligned) {
            delay(500)

            if (Random.nextFloat() < .1) {
                mapperDisplay.showMessage("READY PLAYER ONE…")
            } else if (Random.nextFloat() < .1) {
                mapperDisplay.showMessage("ALIGN THY SHEEP…")
            }
        }

        mapperDisplay.showMessage("CALIBRATING…")

        // Blackout
        retry { udpSocket.broadcastUdp(Ports.BRAIN, solidColor(Color.BLACK)); delay(250L) }
        delay(250L)
        captureBaseImage = true
        delay(250L)

        mapperDisplay.showMessage("MAPPING…")
        mapperDisplay.showStats(brainMappers.size, 0, -1)

        while (isRunning) {
            println("identify brains...")
            // light up each brain in an arbitrary sequence...
            brainMappers.values.forEach { brainMapper ->
                retry { brainMapper.shade { solidColor(Color.WHITE) } }
                delay(34L)

                // wait for a new image to come it...
                while (newChangeRegion == null) {
                    delay(10)
                }
                val changeRegion = newChangeRegion!!
                newChangeRegion = null

                val candidates = mapperDisplay.getCandidateSurfaces(changeRegion)
                mapperDisplay.showMessage2(
                    "Candidate panels: ${candidates.subList(
                        0,
                        min(5, candidates.size)
                    ).map { it.name }}"
                )

                println("Guessed panel ${candidates.first().name} for ${brainMapper.brainId}")

                maybePause()
                retry { brainMapper.shade { solidColor(Color.BLACK) } }
            }

            delay(1000L)

            println("identify pixels...")
            // light up each pixel...
            val pixelShader = PixelShader(PixelShader.Encoding.INDEXED_2)
            val buffer = pixelShader.createBuffer(object : Surface {
                override val pixelCount = SparkleMotion.MAX_PIXEL_COUNT

                override fun describe(): String = "Mapper surface"
            }).apply {
                palette[0] = Color.BLACK
                palette[0] = Color.WHITE
                setAll(0)
            }

            for (i in 0 until maxPixelsPerBrain) {
                if (i % 128 == 0) println("pixel $i... isRunning is $isRunning")
                buffer[i] = 1 // white
                udpSocket.broadcastUdp(Ports.BRAIN, BrainShaderMessage(pixelShader, buffer))
                buffer[i] = 0 // black
                delay(34L)
                maybePause()
            }
            println("done identifying pixels...")

            delay(1000L)
        }
        println("done identifying things... $isRunning")

        retry { udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning)) }
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
        val message = parse(bytes)
        when (message) {
            is BrainIdResponse -> {
                val brainMapper = brainMappers.getOrPut(fromAddress) { BrainMapper(fromAddress, message.id) }
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

        val bitmap = image.toBitmap()
        if (captureBaseImage) {
            baseBitmap = bitmap
            deltaBitmap = NativeBitmap(bitmap.width, bitmap.height)
            captureBaseImage = false
        } else if (baseBitmap != null) {
            deltaBitmap.copyFrom(baseBitmap!!)
            deltaBitmap.subtract(bitmap)

            val changeRegion: MediaDevices.Region = detectChangeRegion()
            this.newChangeRegion = changeRegion

            println("changeRegion = $changeRegion ${changeRegion.width} ${changeRegion.height}")

            mapperDisplay.showDiffImage(deltaBitmap, changeRegion)
        }
    }

    private fun detectChangeRegion(): MediaDevices.Region {
        var changeRegion: MediaDevices.Region = MediaDevices.Region(-1, -1, -1, -1)
        deltaBitmap.withData { data ->
            var x0 = -1
            var y0 = -1
            var x1 = -1
            var y1 = -1

            for (y in 0 until height) {
                var yAnyDiff = false

                for (x in 0 until width) {
                    val pixDiff = data[(x + y * width) * 4 + 2 /* green component */].toInt()

                    if (pixDiff != 0) {
                        if (x0 == -1 || x0 > x) x0 = x
                        if (x > x1) x1 = x
                        yAnyDiff = true
                    }
                }

                if (yAnyDiff) {
                    if (y0 == -1) y0 = y
                    y1 = y
                }
            }
            changeRegion = MediaDevices.Region(x0, y0, x1, y1)
            false
        }
        return changeRegion
    }

    inner class BrainMapper(private val address: Network.Address, val brainId: String) {
        fun shade(shaderMessage: () -> BrainShaderMessage) {
            udpSocket.sendUdp(address, Ports.BRAIN, shaderMessage())
        }
    }
}

interface MapperDisplay {
    fun listen(listener: Listener)

    fun addWireframe(sheepModel: SheepModel)
    fun getCandidateSurfaces(changeRegion: MediaDevices.Region): List<SheepModel.Panel>
    fun showCamImage(image: Image)
    fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region)
    fun showMessage(message: String)
    fun showMessage2(message: String)
    fun showStats(total: Int, mapped: Int, visible: Int)
    fun close()

    interface Listener {
        fun onStart()
        fun onPause()
        fun onStop()
        fun onClose()
    }
}