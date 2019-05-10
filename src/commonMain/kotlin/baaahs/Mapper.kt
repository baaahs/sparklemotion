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
) : Network.UdpListener, MapperDisplay.Listener {
    val maxPixelsPerBrain = 512
    val width = 640
    val height = 300

    val camera = mediaDevices.getCamera(width, height).apply {
        onImage = { image -> haveImage(image) }
    }
    private var baseBitmap: Bitmap? = null
    private lateinit var deltaBitmap: Bitmap
    private var newChangeRegion: MediaDevices.Region? = null

    private val closeListeners = mutableListOf<() -> Unit>()
    private lateinit var link: Network.Link
    private var isRunning: Boolean = true
    private var isAligned: Boolean = false
    private var isPaused: Boolean = false
    private var captureBaseImage = false

    var scope = CoroutineScope(Dispatchers.Main)
    private val brainMappers: MutableMap<Network.Address, BrainMapper> = mutableMapOf()

    init {
        mapperDisplay.listen(this)
        mapperDisplay.addWireframe(sheepModel)
    }

    fun start() = doRunBlocking {
        link = FragmentingUdpLink(network.link())
        link.listenUdp(Ports.MAPPER, this)

        scope = CoroutineScope(Dispatchers.Main)
        scope.launch { run() }
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
        isRunning = false
        camera.close()

        scope.cancel()
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(false))

        closeListeners.forEach { it.invoke() }

        mapperDisplay.close()
    }

    private val retries = (0..1)

    suspend fun run() {
        mapperDisplay.showMessage("ESTABLISHING UPLINK…")

        // shut down Pinky, advertise for Brains...
        retries.forEach {
            link.broadcastUdp(Ports.PINKY, MapperHelloMessage(true))
            delay(1000L)
            link.broadcastUdp(Ports.BRAIN, solidColor(Color.BLACK))
        }

        retries.forEach {
            link.broadcastUdp(Ports.BRAIN, BrainIdRequest(Ports.MAPPER))
            delay(1000L)
        }

        // wait for responses from Brains
        delay(1000L)

        // Blackout
        retries.forEach { link.broadcastUdp(Ports.BRAIN, solidColor(Color.BLACK)); delay(250L) }
        delay(250L)

        // keep Pinky from waking up while we're running...
        scope.launch {
            while (isRunning) {
                link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
                delay(10000L)
            }
        }

        mapperDisplay.showMessage("READY PLAYER ONE…")
        // Blackout
        retries.forEach { link.broadcastUdp(Ports.BRAIN, solidColor(Color.WHITE)); delay(250L) }
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
        retries.forEach { link.broadcastUdp(Ports.BRAIN, solidColor(Color.BLACK)); delay(250L) }
        delay(250L)
        captureBaseImage = true;
        delay(250L)

        mapperDisplay.showMessage("MAPPING…")
        mapperDisplay.showStats(brainMappers.size, 0, -1)

        scope.launch {
            while (isRunning) {
                println("identify brains...")
                // light up each brain in an arbitrary sequence...
                brainMappers.values.forEach { brainMapper ->
                    retries.forEach { brainMapper.shade { solidColor(Color.WHITE) } }
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

                    println("Guessed panel ${candidates.first().name} for ${brainMapper.surfaceName}")

                    maybePause()
                    retries.forEach { brainMapper.shade { solidColor(Color.BLACK) } }
                }

                delay(1000L)

                println("identify pixels...")
                // light up each pixel...
                val pixelShader = PixelShader()
                val buffer = pixelShader.createBuffer(object : Surface {
                    override val pixelCount = SparkleMotion.DEFAULT_PIXEL_COUNT
                })
                buffer.setAll(Color.BLACK)
                for (i in 0 until maxPixelsPerBrain) {
                    if (i % 128 == 0) println("pixel $i... isRunning is $isRunning")
                    buffer.colors[i] = Color.WHITE
                    link.broadcastUdp(Ports.BRAIN, BrainShaderMessage(pixelShader, buffer))
                    buffer.colors[i] = Color.BLACK
                    delay(34L)
                    maybePause()
                }
                println("done identifying pixels...")

                delay(1000L)
            }
            println("done identifying things... $isRunning")
        }

        println("Mapper isRunning: $isRunning")
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    private suspend fun maybePause() {
        while (isPaused) {
            delay(100L)
        }
    }

    private fun solidColor(color: Color): BrainShaderMessage {
        val solidShader = SolidShader()
        val buffer = solidShader.createBuffer(object : Surface {
            override val pixelCount = SparkleMotion.DEFAULT_PIXEL_COUNT
        }).apply { this.color = color }
        return BrainShaderMessage(solidShader, buffer)
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainIdResponse -> {
                val brainMapper = brainMappers.getOrPut(fromAddress) { BrainMapper(fromAddress, message.name) }
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

    fun addCloseListener(listener: () -> Unit) {
        closeListeners.add(listener)
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

    inner class BrainMapper(private val address: Network.Address, val surfaceName: String) {
        fun shade(shaderMessage: () -> BrainShaderMessage) {
            link.sendUdp(address, Ports.BRAIN, shaderMessage())
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
    fun showMessage2(s: String)
    fun showStats(total: Int, mapped: Int, visible: Int)
    fun close()

    interface Listener {
        fun onStart()
        fun onPause()
        fun onStop()
        fun onClose()
    }
}