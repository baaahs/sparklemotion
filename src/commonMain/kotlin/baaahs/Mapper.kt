package baaahs

import baaahs.shaders.PixelShader
import baaahs.shaders.SolidShader
import kotlinx.coroutines.*
import kotlin.random.Random

class Mapper(
    private val network: Network,
    private val sheepModel: SheepModel,
    private val mapperDisplay: MapperDisplay,
    mediaDevices: MediaDevices
) : Network.UdpListener {
    val maxPixelsPerBrain = 512
    val width = 640
    val height = 300

    val camera = mediaDevices.getCamera(width, height).apply {
        onImage = this@Mapper::haveImage
    }
    private var baseBitmap : MediaDevices.MonoBitmap? = null
    private lateinit var deltaBitmap : MediaDevices.MonoBitmap

    private val closeListeners = mutableListOf<() -> Unit>()
    private lateinit var link: Network.Link
    private var isRunning: Boolean = true
    private var isAligned: Boolean = false
    private var captureBaseImage = false

    var scope = CoroutineScope(Dispatchers.Main)
    private val brainMappers: MutableMap<Network.Address, BrainMapper> = mutableMapOf()

    init {
        mapperDisplay.onStart = { onStart() }
        mapperDisplay.onClose = { onClose() }
        mapperDisplay.addWireframe(sheepModel)
    }

    fun start() = doRunBlocking {
        link = network.link()
        link.listenUdp(Ports.MAPPER, this)

        scope = CoroutineScope(Dispatchers.Main)
        scope.launch { run() }
    }

    private fun onStart() {
        isAligned = true
    }

    private fun onClose() {
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

        scope.launch {
            while (isRunning) {
                println("identify brains...")
                // light up each brain in an arbitrary sequence...
                brainMappers.values.forEach { brainMapper ->
                    retries.forEach { brainMapper.shade { solidColor(Color.WHITE) } }
                    delay(34L)
                    retries.forEach { brainMapper.shade { solidColor(Color.BLACK) } }
                }

                delay(1000L)

                println("identify pixels...")
                // light up each pixel...
                val pixelShader = PixelShader()
                pixelShader.buffer.setAll(Color.BLACK)
                for (i in 0 until maxPixelsPerBrain) {
                    if (i % 128 == 0) println("pixel $i... isRunning is $isRunning")
                    pixelShader.buffer.colors[i] = Color.WHITE
                    link.broadcastUdp(Ports.BRAIN, BrainShaderMessage(pixelShader))
                    pixelShader.buffer.colors[i] = Color.BLACK
                    delay(34L)
                }
                println("done identifying pixels...")

                delay(1000L)
            }
            println("done identifying things... $isRunning")
        }

        println("Mapper isRunning: $isRunning")
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    private fun solidColor(color: Color) = BrainShaderMessage(SolidShader().apply { buffer.color = color })

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainIdResponse -> {
                val brainMapper = brainMappers.getOrPut(fromAddress) { BrainMapper(fromAddress) }
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

    private fun haveImage(image: MediaDevices.Image) {
//        println("image: $image")
        mapperDisplay.showCamImage(image)

        val monoBitmap = image.toMonoBitmap()
        if (captureBaseImage) {
            baseBitmap = monoBitmap
            deltaBitmap = MediaDevices.MonoBitmap(monoBitmap.width, monoBitmap.height)
            captureBaseImage = false
        } else if (baseBitmap != null) {
            deltaBitmap.copyFrom(baseBitmap!!)
            val changeRegion = deltaBitmap.subtract(monoBitmap)
            println("changeRegion = $changeRegion")

            mapperDisplay.showDiffImage(deltaBitmap, changeRegion)
        }
    }

    inner class BrainMapper(private val address: Network.Address) {
        fun shade(shaderMessage: () -> BrainShaderMessage) {
            link.sendUdp(address, Ports.BRAIN, shaderMessage())
        }
    }
}

interface MapperDisplay {
    var onStart: () -> Unit
    var onClose: () -> Unit

    fun addWireframe(sheepModel: SheepModel)
    fun showCamImage(image: MediaDevices.Image)
    fun showDiffImage(deltaBitmap: MediaDevices.MonoBitmap, changeRegion: MediaDevices.Region)
    fun showMessage(message: String)
    fun close()
}