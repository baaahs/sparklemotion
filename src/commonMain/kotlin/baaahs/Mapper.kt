package baaahs

import baaahs.shaders.SolidShader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Mapper(
    val network: Network,
    val sheepModel: SheepModel,
    mediaDevices: MediaDevices
) : Network.UdpListener {
    val width = 640
    val height = 300
    val mapperDisplay = MapperDisplay(sheepModel, { onClose() })
    val camera = mediaDevices.getCamera(width, height).apply {
        onImage = this@Mapper::haveImage
    }
    val baseBitmap = UByteArray(width * height * 4)
    val displayBitmap = UByteArray(width * height * 4)

    private val closeListeners = mutableListOf<() -> Unit>()
    private lateinit var link: Network.Link
    private var isRunning: Boolean = false

    fun start() {
        link = network.link()
        link.listenUdp(Ports.MAPPER, this)

        isRunning = true

        GlobalScope.launch {
            run()
        }
    }

    private fun onClose() {
        camera.close()
        isRunning = false
        closeListeners.forEach { it.invoke() }
    }

    private fun haveImage(image: MediaDevices.Image) {
//        println("image: $image")
        mapperDisplay.showCamImage(image)

        val toBitmap = image.toMonoBitmap()
//        println("toMonoBitmap = ${toMonoBitmap}")
    }

    suspend fun run() {
        // shut down Pinky, advertise for Brains...
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
        delay(1000L)
        link.broadcastUdp(Ports.BRAIN, BrainShaderMessage(SolidShader().apply { buffer.color = Color.BLACK }))
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
        delay(1000L)
        link.broadcastUdp(Ports.BRAIN, BrainShaderMessage(SolidShader().apply { buffer.color = Color.BLACK }))
        link.broadcastUdp(Ports.BRAIN, BrainIdRequest(Ports.MAPPER))

        while (isRunning) {
            link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))

            delay(10000L)
        }

        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainIdResponse -> {
                link.sendUdp(
                    fromAddress,
                    Ports.BRAIN,
                    BrainShaderMessage(SolidShader().apply { buffer.color = Color.WHITE })
                )
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
}

expect class MapperDisplay(sheepModel: SheepModel, onExit: () -> Unit) {
    fun showCamImage(image: MediaDevices.Image)
}