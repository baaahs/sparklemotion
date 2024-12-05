package baaahs.mapper

import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.sm.brain.proto.*
import baaahs.util.Clock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class UdpSockets(
    link: Network.Link,
    private val clock: Clock,
    private val mapper: Mapper,
    private val ui: MapperUi,
    private val onMappableBrain: (MappableBrain) -> Unit
): Network.UdpListener {
    private val udpSocket = link.listenFragmentingUdp(0, this)
    val deliverer = ReliableShaderMessageDeliverer()

    private val mappableBrains: MutableMap<Network.Address, MappableBrain> = mutableMapOf()

    fun adviseMapperStatus(isRunning: Boolean) {
        udpSocket.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    fun allDark() {
        udpSocket.broadcastUdp(Ports.BRAIN, MapperUtil.solidColor(MapperUtil.inactiveColor))
    }

    fun requestBrainIds() {
        udpSocket.broadcastUdp(Ports.BRAIN, BrainIdRequest())
    }

    fun pixelOnByBroadcast(pixelIndex: Int) {
        val brainShader = MapperUtil.singlePixelOnBuffer(pixelIndex)
        udpSocket.broadcastUdp(Ports.BRAIN, BrainShaderMessage(brainShader.brainShader, brainShader))
    }

    override suspend fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
//        logger.debug { "Mapper received message from $fromAddress:$fromPort ${bytes[0]}" }
        when (val message = parse(bytes)) {
            is BrainHelloMessage -> {
                Mapper.logger.debug { "Heard from Brain ${message.brainId} surface=${message.surfaceName ?: "unknown"}" }
                val mappableBrain = mappableBrains.getOrPut(fromAddress) {
                    MappableBrain(fromAddress, message.brainId) { message ->
                        udpSocket.sendUdp(fromAddress, fromPort, message)
                    }
                }
                onMappableBrain(mappableBrain)
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

        suspend fun await(
            retryAfter: Duration = .25.seconds,
            failAfter: Duration = 10.0.seconds
        ) {
            val oldMessage2 = ui.message2

            Mapper.logger.debug { "Waiting pongs from ${outstanding.values.map { it.mappableBrain.brainId }}..." }

            outstanding.values.forEach {
                it.retryAt = it.sentAt + retryAfter
                it.failAt = it.sentAt + failAfter
            }

            while (outstanding.isNotEmpty()) {
                val waitingFor =
                    outstanding.values.map { it.mappableBrain.guessedEntity?.name ?: it.mappableBrain.brainId }
                        .sorted()
                ui.message2 = "Waiting for PONG from ${waitingFor.joinToString(",")}"
//                logger.debug { "pongs outstanding: ${outstanding.keys.map { it.stringify() }}" }

                var sleepUntil = Instant.DISTANT_FUTURE

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
                            it.retryAt = now + retryAfter
                        }
                        if (sleepUntil > it.retryAt) sleepUntil = it.retryAt
                        false
                    }
                }

                val timeoutSec = sleepUntil - now
//                logger.debug { "Before pongs.receive() withTimeout(${timeoutSec}s)" }
                val pong = withTimeoutOrNull(timeoutSec.inWholeMilliseconds) {
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
        var retryAt = Instant.DISTANT_PAST
        var failAt = Instant.DISTANT_PAST
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