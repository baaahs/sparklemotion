package baaahs

import baaahs.shaders.PixelShader
import baaahs.shaders.SolidShader
import kotlinx.coroutines.*

class Mapper(val network: Network, val display: MapperDisplay) : Network.UdpListener {
    val maxPixelsPerBrain = 512
    private lateinit var link: Network.Link
    var scope = CoroutineScope(Dispatchers.Main)
    private var isRunning: Boolean = false
        set(value) { display.isRunning = value; field = value }
    private val brainMappers: MutableMap<Network.Address, BrainMapper> = mutableMapOf()

    fun start() = doRunBlocking {
        link = network.link()
        link.listenUdp(Ports.MAPPER, this)

        display.onStart = {
            println("onStart...")
            if (!isRunning) {
                isRunning = true

                scope = CoroutineScope(Dispatchers.Main)
                scope.launch { run() }
            }
        }

        display.onStop = {
            println("onStop...")
            if (isRunning) {
                println("scope.cancel")
                scope.cancel()
                println("set isRunning to false...")
                isRunning = false
                link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
            }
        }

    }

    private val retries = (0..1)

    suspend fun run() {
        // shut down Pinky, advertise for Brains...
        retries.forEach {
            link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
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

    inner class BrainMapper(private val address: Network.Address) {
        fun shade(shaderMessage: () -> BrainShaderMessage) {
            link.sendUdp(address, Ports.BRAIN, shaderMessage())
        }
    }
}
