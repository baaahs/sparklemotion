package baaahs.net

import baaahs.Logger
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.min

class FragmentingUdpLink(private val wrappedLink: Network.Link) : Network.Link {
    override val myAddress: Network.Address get() = wrappedLink.myAddress
    override val udpMtu: Int get() = wrappedLink.udpMtu

    /**
     * Header is 12 bytes long; format is:
     * * message ID (short)
     * * this frame size (short)
     * * total payload size (long)
     * * this frame offset (long)
     */
    companion object {
        const val headerSize = 12

        val logger = Logger("FragmentingUdpLink")
    }

    private val mtu = wrappedLink.udpMtu
    private var nextMessageId: Short = 0

    private var fragments = arrayListOf<Fragment>()

    class Fragment(val messageId: Short, val offset: Int, val bytes: ByteArray) {
        override fun toString(): String {
            return "Fragment(messageId=$messageId, offset=$offset, bytes=${bytes.size})"
        }
    }

    override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
        return FragmentingUdpSocket(wrappedLink.listenUdp(port, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
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

                val frameBytes = reader.readNBytes(size)
                if (offset == 0 && size == totalSize) {
                    udpListener.receive(fromAddress, fromPort, frameBytes)
                } else {
                    val thisFragment = Fragment(messageId, offset, frameBytes)
                    synchronized(fragments) {
                        fragments.add(thisFragment)
                    }

//                        println("received fragment: ${thisFragment}")
                    if (offset + size == totalSize) {
                        // final fragment, try to reassemble…

                        val myFragments = removeMessageId(messageId)

                        val actualTotalSize = myFragments.map { it.bytes.size }.reduce { acc, i -> acc + i }
                        if (actualTotalSize == totalSize) {
                            val reassembleBytes = ByteArray(totalSize)
                            myFragments.forEach {
                                it.bytes.copyInto(reassembleBytes, it.offset)
                            }

                            udpListener.receive(fromAddress, fromPort, reassembleBytes)
                        } else {
                            // todo: this should probably be a warn, not an error...
                            logger.warn {
                                "incomplete fragmented UDP packet from $fromAddress:$fromPort:" +
                                        " actualTotalSize=$actualTotalSize != totalSize=$totalSize" +
                                        " for messageId=$messageId" +
                                        " (have ${myFragments.map { it.bytes.size }.joinToString(",")})"
                            }

                            synchronized(fragments) {
                                fragments.addAll(myFragments)
                            }
                        }

                    }
                }
            }
        }))
    }

    private fun removeMessageId(messageId: Short): List<Fragment> {
        val myFragments = arrayListOf<Fragment>()

        synchronized(fragments) {
            fragments.removeAll { fragment ->
                val remove = fragment.messageId == messageId
                if (remove) myFragments.add(fragment)
                remove
            }

            val offsets = hashSetOf<Int>()
            myFragments.removeAll { fragment ->
                val alreadyThere = !offsets.add(fragment.offset)
                if (alreadyThere) {
                    println("already there: ${fragment}")
                    println("from: $myFragments")
                }
                alreadyThere // duplicate, ignore
            }

            if (myFragments.isEmpty()) {
                println("remaining fragments = ${fragments}")
            }
        }

        return myFragments.sortedBy { it.offset }
    }

    inner class FragmentingUdpSocket(private val delegate: Network.UdpSocket) : Network.UdpSocket {
        override val serverPort: Int get() = delegate.serverPort

        /** Sends payloads which might be larger than the network's MTU. */
        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            transmitMultipartUdp(bytes) { fragment -> delegate.sendUdp(toAddress, port, fragment) }
        }

        /** Broadcasts payloads which might be larger than the network's MTU. */
        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            transmitMultipartUdp(bytes) { fragment -> delegate.broadcastUdp(port, fragment) }
        }

        /** Sends payloads which might be larger than the network's MTU. */
        private fun transmitMultipartUdp(bytes: ByteArray, fn: (bytes: ByteArray) -> Unit) {
            val maxSize = 65535 * 2 // arbitrary but probably sensible
            if (bytes.size > maxSize) {
                throw IllegalArgumentException("buffer too big! ${bytes.size} must be < $maxSize")
            }

            val messageId = nextMessageId++
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
                writer.writeNBytes(bytes, offset, offset + thisFrameSize)
                fn(writer.toBytes())

//                println("Sending UDP: messageId=$messageId thisFrameSize=${thisFrameSize.toShort()} totalSize=${bytes.size} offset=${offset}")

                offset += thisFrameSize
            }
        }
    }

    override fun startHttpServer(port: Int): Network.HttpServer =
        wrappedLink.startHttpServer(port)

    override fun connectWebSocket(
        toAddress: Network.Address,
        port: Int,
        path: String,
        webSocketListener: Network.WebSocketListener
    ): Network.TcpConnection =
        wrappedLink.connectWebSocket(toAddress, port, path, webSocketListener)

}
