package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

class Pinky(val network: Network, val display: PinkyDisplay) : Network.Listener {
    private lateinit var link: Network.Link
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()
    private val beatProvider = BeatProvider(120.0f)

    fun run() {
        link = network.link()
        link.listen(Ports.PINKY, this)
    }

    fun start() {
        GlobalScope.launch {
            run()
        }

        GlobalScope.launch {
            beatProvider.run()
        }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        when (parse(bytes)) {
            is BrainHelloMessage -> {
                foundBrain(RemoteBrain(fromAddress))
            }

            is MapperHelloMessage -> {
                sendMapperPong(fromAddress)
            }
        }

    }

    @Synchronized
    private fun sendMapperPong(fromAddress: Network.Address) {
        link.send(
            fromAddress,
            Ports.MAPPER,
            PinkyPongMessage(brains.values.map { it.fromAddress.toString() }).toBytes()
        )
    }

    @Synchronized
    private fun foundBrain(remoteBrain: RemoteBrain) {
        brains.put(remoteBrain.fromAddress, remoteBrain)
        display.brainCount = brains.size
    }

    inner class BeatProvider(val bpm: Float) {
        var startTimeMillis = 0L
        var beat = 0
        var beatsPerMeasure = 4

        suspend fun run() {
            startTimeMillis = getTimeMillis()

            while (true) {
                display.beat = beat

                val offsetMillis = getTimeMillis() - startTimeMillis
                val millisPerBeat = (1000 / (bpm / 60)).toLong()
                val delayTimeMillis = millisPerBeat - offsetMillis % millisPerBeat
                delay(delayTimeMillis)
                beat = (beat + 1) % beatsPerMeasure
            }
        }
    }
}

class RemoteBrain(val fromAddress: Network.Address) {
}