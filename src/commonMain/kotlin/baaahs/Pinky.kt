package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class Pinky(val network: Network, val display: PinkyDisplay) : Network.Listener {
    private lateinit var link: Network.Link
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()
    private val beatProvider = BeatProvider(120.0f)
    private val show = SomeDumbShow()
    private var mapperIsRunning = false

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

        GlobalScope.launch {
            while (true) {
                if (!mapperIsRunning) {
                    show.nextFrame(display.color, beatProvider.beat, brains, link)
                }
                delay(50)
            }
        }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> {
                foundBrain(RemoteBrain(fromAddress))
            }

            is MapperHelloMessage -> {
                mapperIsRunning = message.isRunning
            }
        }

    }

    @Synchronized
    private fun foundBrain(remoteBrain: RemoteBrain) {
        brains.put(remoteBrain.address, remoteBrain)
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

class RemoteBrain(val address: Network.Address)

class SomeDumbShow {
    fun nextFrame(color: Color?, beat: Int, brains: MutableMap<Network.Address, RemoteBrain>, link: Network.Link) {
        brains.values.forEach { brain ->
            val brainSeed = brain.address.toString().hashCode()
            val saturation = Random(brainSeed).nextFloat() *
                    abs(sin(brainSeed + getTimeMillis() / 1000.toDouble())).toFloat()
            val desaturatedColor = color!!.withSaturation(saturation)
            link.send(brain.address, Ports.BRAIN, BrainShaderMessage(desaturatedColor))
        }
    }
}