package baaahs.mapper

import baaahs.Color
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.sm.brain.proto.*
import baaahs.util.Clock
import baaahs.util.asMillis
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

class UdpSockets(
    link: Network.Link,
    private val clock: Clock,
    private val brainsToMap: MutableMap<Network.Address, MappableBrain>,
    private val mapper: Mapper,
    private val ui: MapperUi
): Network.UdpListener {
    private val udpSocket = link.listenFragmentingUdp(0, this)
    val deliverer = ReliableShaderMessageDeliverer()

    fun adviseMapperStatus(isRunning: Boolean) {
        udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    fun allDark() {
        udpSocket.broadcastUdp(Ports.BRAIN, MapperUtil.solidColor(MapperUtil.inactiveColor))
    }

    fun requestBrainIds() {
        udpSocket.broadcastUdp(Ports.BRAIN, BrainIdRequest())
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
//        logger.debug { "Mapper received message from $fromAddress:$fromPort ${bytes[0]}" }
        when (val message = parse(bytes)) {
            is BrainHelloMessage -> {
                Mapper.logger.debug { "Heard from Brain ${message.brainId} surface=${message.surfaceName ?: "unknown"}" }
                val mappableBrain = brainsToMap.getOrPut(fromAddress) {
                    MappableBrain(fromAddress, message.brainId) { message ->
                        udpSocket.sendUdp(fromAddress, fromPort, message)
                    }
                }
                ui.showMessage("${brainsToMap.size} SURFACES DISCOVERED!")

                // Less voltage causes less LED glitches.
                mappableBrain.shade { MapperUtil.solidColor(Color.GREEN.withBrightness(.4f)) }
            }

            is PingMessage -> {
                if (message.isPong) {
                    deliverer.gotPong(message)
                }
            }
        }
    }

    inner class ReliableShaderMessageDeliverer {
        private val outstanding = mutableMapOf<List<Byte>, DeliveryAttempt>()
        private val pongs = Channel<PingMessage>()

        fun send(mappableBrain: MappableBrain, buffer: BrainShader.Buffer) {
            val deliveryAttempt = DeliveryAttempt(mappableBrain, buffer)
//            logger.debug { "attempting reliable delivery with key ${deliveryAttempt.key.stringify()}" }
            outstanding[deliveryAttempt.key] = deliveryAttempt
            deliveryAttempt.attemptDelivery()
        }

        suspend fun await(retryAfterSeconds: Double = .25, failAfterSeconds: Double = 10.0) {
            val oldMessage2 = ui.message2

            Mapper.logger.debug { "Waiting pongs from ${outstanding.values.map { it.mappableBrain.brainId }}..." }

            outstanding.values.forEach {
                it.retryAt = it.sentAt + retryAfterSeconds
                it.failAt = it.sentAt + failAfterSeconds
            }

            while (outstanding.isNotEmpty()) {
                val waitingFor =
                    outstanding.values.map { it.mappableBrain.guessedEntity?.name ?: it.mappableBrain.brainId }
                        .sorted()
                ui.message2 = "Waiting for PONG from ${waitingFor.joinToString(",")}"
//                logger.debug { "pongs outstanding: ${outstanding.keys.map { it.stringify() }}" }

                var sleepUntil = Double.MAX_VALUE

                val now = clock.now()

                outstanding.values.removeAll {
                    if (it.failAt < now) {
                        Mapper.logger.debug {
                            "Timed out waiting after ${now - it.sentAt}s for ${it.mappableBrain.brainId}" +
                                    " pong ${it.key.stringify()}"
                        }
                        it.failed()
                        true
                    } else {
                        if (sleepUntil > it.failAt) sleepUntil = it.failAt

                        if (it.retryAt < now) {
                            Mapper.logger.warn {
                                "Haven't heard from ${it.mappableBrain.brainId} after ${now - it.sentAt}s," +
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
                        Mapper.logger.warn { "huh? no such pong tag ${pongTag.stringify()}!" }
                    }
                }

                ui.message2 = oldMessage2
            }
        }

        fun gotPong(pingMessage: PingMessage) {
            mapper.launch {
                pongs.send(pingMessage)
            }
        }
    }

    inner class DeliveryAttempt(val mappableBrain: MappableBrain, val buffer: BrainShader.Buffer) {
        private val tag = Random.nextBytes(8)
        val key get() = tag.toList()
        val sentAt = clock.now()
        var retryAt = 0.0
        var failAt = 0.0
        var retryCount = 0

        fun attemptDelivery() {
            mappableBrain.send(BrainShaderMessage(buffer.brainShader, buffer, tag))
        }

        fun succeeded() {
            Mapper.logger.debug { "${mappableBrain.brainId} shader message pong after ${clock.now() - sentAt}s" }
        }

        fun failed() {
            Mapper.logger.error { "${mappableBrain.brainId} shader message pong not received after ${clock.now() - sentAt}s" }
        }
    }
}