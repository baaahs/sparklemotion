package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.util.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile
import kotlin.math.min

fun Network.Link.listenFragmentingUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
    return FragmentingUdpSocket(this, port, udpListener)
}


class FragmentingUdpSocket(
    private val link: Network.Link,
    port: Int,
    private val udpListener: Network.UdpListener,
) : Network.UdpSocket, Network.UdpListener {
    private val delegate = link.listenUdp(port, this)
    override val serverPort: Int get() = delegate.serverPort

    init {
        if (delegate is FragmentingUdpSocket) {
            error("You're trying to wrap a FragmentingUdpSocket in a FragmentingUdpSocket?")
        }
    }

    @Volatile
    private var nextMessageId: Short = 0
    private val incoming = linkedMapOf<Number, MessageAssembler>()
    private val incomingMutex = Mutex()

    class MessageAssembler(private val messageId: Short, val totalSize: Int) {
        private val fragments = hashMapOf<Int, Int>() // offset to size
        val bytes = ByteArray(totalSize)

        fun receive(offset: Int, size: Int, reader: ByteArrayReader) {
            var newData = false
            fragments.getOrPut(offset) { newData = true; size }
            if (newData) {
                reader.readBytes(bytes, size, offset)
            } else {
                reader.skipBytes(size)
            }
        }

        fun isComplete(): Boolean = bytesReceived() == totalSize

        fun bytesReceived() = fragments.values.sum()

        override fun toString(): String {
            return "MessageAssembler(messageId=$messageId, bytes received=${bytesReceived()}, total bytes=$totalSize)"
        }
    }

    /** Sends payloads which might be larger than the network's MTU. */
    override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
        transmitMultipartUdp(bytes) { fragment -> delegate.sendUdp(toAddress, port, fragment) }
    }

    /** Broadcasts payloads which might be larger than the network's MTU. */
    override fun broadcastUdp(port: Int, bytes: ByteArray) {
        transmitMultipartUdp(bytes) { fragment -> delegate.broadcastUdp(port, fragment) }
    }

    override fun close() {
        delegate.close()
    }

    /** Sends payloads which might be larger than the network's MTU. */
    private fun transmitMultipartUdp(bytes: ByteArray, fn: (bytes: ByteArray) -> Unit) {
        val maxSize = 65535 * 2 // arbitrary but probably sensible
        if (bytes.size > maxSize) {
            throw IllegalArgumentException("buffer too big! ${bytes.size} must be < $maxSize")
        }

        val messageId = nextMessageId++
        val mtu = link.udpMtu
        val messageCount = (bytes.size - 1) / (mtu - headerSize) + 1
        val buf = ByteArray(mtu)
        var offset = 0
        for (i in 0 until messageCount) {
            val writer = ByteArrayWriter(buf)
            val thisFrameSize = min((mtu - headerSize), bytes.size - offset)
            writer.writeShort(messageId)
            writer.writeShort(thisFrameSize.toShort())
            writer.writeInt(bytes.size)
            writer.writeInt(offset)
            writer.writeBytes(bytes, offset, offset + thisFrameSize)
            fn(writer.copyBytes())

//                println("Sending UDP: messageId=$messageId thisFrameSize=${thisFrameSize.toShort()} totalSize=${bytes.size} offset=${offset}")

            offset += thisFrameSize
        }
    }

    override suspend fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        // reassemble fragmented payloads...
        val reader = ByteArrayReader(bytes)
        val messageId = reader.readShort()
        val size = reader.readShort().toInt()
        val totalSize = reader.readInt()
        val offset = reader.readInt()
//                println("Received UDP: messageId=$messageId thisFrameSize=${size} totalSize=${totalSize} offset=${offset} packetSize=${bytes.size}")

        if (size + headerSize > bytes.size) {
            logger.debug { "Discarding short UDP message: ${size + headerSize} > ${bytes.size} available" }
            return
        }

        if (offset == 0 && size == totalSize) {
            // Full frame in a single packet, awesome!
            val frameBytes = reader.readBytes(size)
            udpListener.receive(fromAddress, fromPort, frameBytes)
        } else {
            val messageAssembler = incomingMutex.withLock {
                assembleSegments(messageId, totalSize, offset, size, reader)
            }

            if (messageAssembler != null) {
                udpListener.receive(fromAddress, fromPort, messageAssembler.bytes)
            }
        }
    }

    private fun assembleSegments(
        messageId: Short,
        totalSize: Int,
        offset: Int,
        size: Int,
        reader: ByteArrayReader
    ): MessageAssembler? {
        // Part of a frame.
        val messageAssembler = incoming.getOrPut(messageId) { MessageAssembler(messageId, totalSize) }
        messageAssembler.receive(offset, size, reader)

        if (messageAssembler.isComplete()) {
            incoming.remove(messageId)
            return messageAssembler
        }

        val incomingCount = incoming.size
        if (incomingCount > incomingMessageWindow) {
            val key = incoming.keys.first()
            val value = incoming.remove(key)
            if (value != null) {
                logger.debug {
                    "Dropped incomplete incoming message $key " +
                            "with ${value.bytesReceived()} of ${value.totalSize} bytes; " +
                            "window size is ${incomingMessageWindow}."
                }
            } else {
                logger.debug {
                    "Dropped incomplete incoming message $key " +
                            "but no message segments matching; " +
                            "window size is ${incomingMessageWindow}."
                }
            }
        }

        return null
    }

    internal suspend fun receiveBypassingFragmentation(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        udpListener.receive(fromAddress, fromPort, bytes)
    }

    /**
     * Header is 12 bytes long; format is:
     * * message ID (short)
     * * this frame size (short)
     * * total payload size (long)
     * * this frame offset (long)
     */
    companion object {
        const val headerSize = 12
        const val incomingMessageWindow = 10

        private val logger = Logger<FragmentingUdpSocket>()
    }
}
